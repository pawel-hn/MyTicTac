package com.mytictac.tictacgame.compose

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mytictac.data.GameEndResult
import com.mytictac.tictacgame.GameState
import com.mytictac.tictacgame.TicTacViewModel
import com.mytictac.ui.components.TicTacButton
import com.mytictac.ui.debouncedFieldClick
import com.mytictac.ui.theme.MyTicTacTheme
import com.mytictac.ui.theme.Padding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val STANDARD_ANIMATION_DURATION = 500

@Composable
fun TicTacScreen() {
    var color by remember { mutableStateOf(Color.White) }
    val animatedColor by animateColorAsState(
        targetValue = color,
        animationSpec = tween(durationMillis = 500),
        label = "ColorAnimation"
    )

    Column(
        modifier = Modifier.background(animatedColor).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val viewModel: TicTacViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val computerMove by viewModel.animateMove.collectAsStateWithLifecycle(-1)

        when (val result = state) {
            GameState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is GameState.CurrentGame -> {
                LaunchedEffect(result.gameEndResult, result.reset) {
                    if (result.gameEndResult == GameEndResult.Circle ||
                        result.gameEndResult == GameEndResult.Cross) {
                        repeat(6) {
                            color =
                                if (color == Color.White) Color(0xFFf5ebc4) else Color.White
                            delay(STANDARD_ANIMATION_DURATION.toLong())
                        }
                    }
                }
                LaunchedEffect(result.reset){
                    if (result.reset) {
                        color = Color.White
                    }
                }

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
                    val fields = rememberFieldCoordinatesController(centerOffset,lineLength)

                    TicTacToeField(
                        state = result,
                        fieldCoordinatesController = fields,
                        onTap = { id -> viewModel.fieldTapped(id, false) },
                        animateComputerMove = computerMove,
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
}

@Composable
fun TicTacToeField(
    state: GameState.CurrentGame,
    fieldCoordinatesController: FieldCoordinatesController,
    animateComputerMove: Int,
    onTap: (Int) -> Unit,
    setDefault: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var animations by remember { mutableStateOf(emptyAnimations()) }
    var winningLineAnimation = remember { Animatable(0F) }

    LaunchedEffect(state.reset) {
        if (state.reset) {
            animations = emptyAnimations()
            winningLineAnimation = Animatable(0F)
            setDefault()
        }
    }
    LaunchedEffect(state.winningSet) {
        if (state.winningSet.isNotEmpty()) {
            winningLineAnimation.animateTo(
                20F,
                tween(STANDARD_ANIMATION_DURATION, STANDARD_ANIMATION_DURATION)
            )
        }
    }
    LaunchedEffect(animateComputerMove) {
        if (animateComputerMove > 0) {
            scope.animateFloatToOne(animations[getAnimationIndex(animateComputerMove)])
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
        drawTicTacToeField(lineColor = Color.LightGray, field = fieldCoordinatesController.fieldPitch)
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

        if (state.winningSet.isNotEmpty()) {
            val winningLine = fieldCoordinatesController.getWinningLine(
                start = fieldCoordinatesController.getFieldXYFromId(state.winningSet.first()),
                end = fieldCoordinatesController.getFieldXYFromId(state.winningSet.last())
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
