package com.mytictac.game.compose

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.mytictac.data.Field
import com.mytictac.game.GameUIState
import com.mytictac.ui.debouncedFieldClick
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun GameField(
    modifier: Modifier,
    state: GameUIState.CurrentCurrentGameUI,
    animationEvent: AnimationEvent?,
    onTap: (Int) -> Unit,
    setDefault: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val animations by remember { mutableStateOf(emptyAnimations()) }
    val winningLineAnimation by remember { mutableStateOf(Animatable(0F)) }

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

            is AnimationEvent.GameLoaded -> {
                animationEvent.fields.forEach { field ->
                    animateFloatToOne(animations[getAnimationIndex(field.id)])
                }
            }

            else -> Unit
        }
    }

    BoxWithConstraints(
        modifier = modifier
    ) {
        val centerOffset =
            remember {
                Offset(x = constraints.maxWidth / 2F, y = constraints.maxHeight / 2F)
            }
        val lineLength = remember { constraints.maxWidth * 0.7F }
        val fieldCoordinatesController =
            rememberFieldCoordinatesController(centerOffset, lineLength)

        Canvas(
            modifier =
            Modifier
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
                val winningLine =
                    fieldCoordinatesController.getWinningLine(
                        start =
                        fieldCoordinatesController
                            .getFieldXYFromId(animationEvent.winningFields.first()),
                        end =
                        fieldCoordinatesController
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
}

fun CoroutineScope.animateFloatToOne(animatable: Animatable<Float, AnimationVector1D>) {
    launch {
        animatable.animateTo(
            targetValue = 20f,
            animationSpec =
            tween(
                durationMillis = STANDARD_ANIMATION_DURATION
            )
        )
    }
}

fun emptyAnimations(): List<Animatable<Float, AnimationVector1D>> {
    return List(9) { Animatable(1f) }
}

private val animationIndexMap =
    mapOf(
        11 to 0, 12 to 1, 13 to 2,
        21 to 3, 22 to 4, 23 to 5,
        31 to 6, 32 to 7, 33 to 8
    )

fun getAnimationIndex(id: Int) = animationIndexMap[id] ?: -1

sealed interface AnimationEvent {
    data class GameLoaded(val fields: Set<Field>) : AnimationEvent

    data object ResetAnimations : AnimationEvent

    data class AnimateWinningLine(val winningFields: Set<Field>) : AnimationEvent

    data class AnimateComputerMove(val fieldId: Int) : AnimationEvent
}
