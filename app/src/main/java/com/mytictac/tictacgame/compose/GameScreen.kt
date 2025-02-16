package com.mytictac.tictacgame.compose


import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mytictac.data.Field
import com.mytictac.tictacgame.GameDialog
import com.mytictac.tictacgame.GameRouter
import com.mytictac.tictacgame.GameUIEvents
import com.mytictac.tictacgame.GameUIState
import com.mytictac.tictacgame.GameViewModel
import com.mytictac.ui.components.TicTacButton
import com.mytictac.ui.components.TicTacDialog
import com.mytictac.ui.debouncedFieldClick
import com.mytictac.ui.theme.MyTicTacTheme
import com.mytictac.ui.theme.Padding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val STANDARD_ANIMATION_DURATION = 500

@Composable
fun TicTacScreen(
    viewModel: GameViewModel,
    router: GameRouter,
) {

    val gameDialog: MutableState<GameDialog?> = rememberSaveable { mutableStateOf(null) }
    val animationEvent: MutableState<AnimationEvent?> = remember{ mutableStateOf(null) }

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest {
            when (it) {
                is GameUIEvents.ShowDialog -> {
                    gameDialog.value = it.dialog
                }
                is GameUIEvents.NavigateToMainScreen -> {
                    router.backToMainScreen()
                }
                is GameUIEvents.ComputerMove -> {
                    animationEvent.value = AnimationEvent.AnimateComputerMove(it.fieldId)
                }
                GameUIEvents.ResetGame -> {
                    animationEvent.value = AnimationEvent.ResetAnimations
                }
                is GameUIEvents.VictoryLine -> {
                    animationEvent.value = AnimationEvent.AnimateWinningLine(it.winningFields)
                }
            }
        }
    }

    BackHandler {
        viewModel.onGestureBack()
    }

    Column(
        modifier = Modifier.background(Color.White).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val result = state) {
            GameUIState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is GameUIState.CurrentCurrentGameUI -> {
                GameCurrentPlayerHeader(
                    modifier = Modifier.fillMaxWidth().padding(top = Padding.medium),
                    state = result
                )
                BoxWithConstraints(
                    modifier = Modifier.weight(1F)
                ) {
                    val centerOffset = remember {
                        Offset(x = constraints.maxWidth / 2F, y = constraints.maxHeight / 2F)
                    }
                    val lineLength = remember { constraints.maxWidth * 0.7F }
                    val fields = rememberFieldCoordinatesController(centerOffset, lineLength)

                    TicTacToeField(
                        state = result,
                        fieldCoordinatesController = fields,
                        animationEvent = animationEvent.value,
                        onTap = { id -> viewModel.fieldTapped(id, false) },
                        setDefault = viewModel::setDefault
                    )
                }

                TicTacButton(
                    modifier = Modifier.padding(Padding.large),
                    width = 120.dp,
                    height = 40.dp,
                    textSize = 12.sp,
                    enabledPrimaryColor = MyTicTacTheme.colours.interactiveTertiary,
                    enabledSecondaryColor = MyTicTacTheme.colours.interactiveTertiaryContent,
                    text = "Reset",
                    isSelected = true,
                    onClick = viewModel::reset
                )
            }
        }
    }
    val dialogToShow = gameDialog.value
    if (dialogToShow != null) {
        TicTacDialog(
            gameDialog = dialogToShow,
            onConfirm = {
                gameDialog.value = null
                viewModel.navigateToMainScreen()
            },
            onCancel = {
                gameDialog.value = null
            }
        )
    }
}

@Composable
fun TicTacToeField(
    state: GameUIState.CurrentCurrentGameUI,
    fieldCoordinatesController: FieldCoordinatesController,
    animationEvent: AnimationEvent?,
    onTap: (Int) -> Unit,
    setDefault: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val animations by remember { mutableStateOf(emptyAnimations()) }
    var winningLineAnimation by remember { mutableStateOf(Animatable(0F)) }

    LaunchedEffect(animationEvent) {
        when (animationEvent) {
            is AnimationEvent.AnimateComputerMove -> {
                if (animationEvent.fieldId > 0) {
                    animateFloatToOne(animations[getAnimationIndex(animationEvent.fieldId)])
                }
            }

            AnimationEvent.ResetAnimations -> {
                animations.forEach { it.snapTo(1F) }
                winningLineAnimation.snapTo(0F)
                setDefault()
            }

            is AnimationEvent.AnimateWinningLine -> {
                if (animationEvent.winningFields.isNotEmpty()) {
                    winningLineAnimation.animateTo(
                        20F,
                        tween(STANDARD_ANIMATION_DURATION, STANDARD_ANIMATION_DURATION)
                    )
                }
            }

            else -> Unit
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .debouncedFieldClick(
                pointerInputKey = true,
                onClick = { position ->
                    fieldCoordinatesController.getFieldXYFromOffset(position)?.let {
                        onTap(it.id)
                        scope.animateFloatToOne(animations[getAnimationIndex(it.id)])
                    }
                }
            )
    ) {
        drawTicTacToeField(
            lineColor = Color.LightGray,
            field = fieldCoordinatesController.fieldPitch
        )
        state.cross.moves.forEach { field ->
            val fieldXY = fieldCoordinatesController.getFieldXYFromId(field)
            drawCross(
                topLeft = fieldXY.topLeft,
                bottomRight = fieldXY.bottomRight,
                width = animations[getAnimationIndex(field.id)].value
            )
        }

        state.circle.moves.forEach { field ->
            val fieldXY = fieldCoordinatesController.getFieldXYFromId(field)
            drawTicCircle(
                topLeft = fieldXY.topLeft,
                bottomRight = fieldXY.bottomRight,
                width = animations[getAnimationIndex(field.id)].value
            )
        }

        if (animationEvent is AnimationEvent.AnimateWinningLine) {
            val winningLine = fieldCoordinatesController.getWinningLine(
                start = fieldCoordinatesController
                    .getFieldXYFromId(animationEvent.winningFields.first()),
                end = fieldCoordinatesController
                    .getFieldXYFromId(animationEvent.winningFields.last())
            )

            if (winningLineAnimation.value > 0F) {
                drawLine(
                    color = Color.Gray,
                    start = winningLine.first,
                    end = winningLine.second,
                    strokeWidth = winningLineAnimation.value
                )
            }
        }
    }
}

private fun CoroutineScope.animateFloatToOne(animatable: Animatable<Float, AnimationVector1D>) {
    launch {
        animatable.animateTo(
            targetValue = 20f,
            animationSpec = tween(
                durationMillis = STANDARD_ANIMATION_DURATION
            )
        )
    }
}

fun emptyAnimations(): List<Animatable<Float, AnimationVector1D>> {
    return List(9) { Animatable(1f) }
}

private val animationIndexMap = mapOf(
    11 to 0, 12 to 1, 13 to 2,
    21 to 3, 22 to 4, 23 to 5,
    31 to 6, 32 to 7, 33 to 8
)

fun getAnimationIndex(id: Int) = animationIndexMap[id] ?: -1


sealed interface AnimationEvent {
    data object ResetAnimations : AnimationEvent
    data class AnimateWinningLine(val winningFields: Set<Field>) : AnimationEvent
    data class AnimateComputerMove(val fieldId: Int) : AnimationEvent
}