package com.mytictac.tictacgame

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mytictac.ui.debouncedFieldClick
import com.mytictac.ui.theme.MyTicTacTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

const val LINE_ANIMATION_DURATION = 500

@Composable
fun TicTacScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val viewModel: TicTacViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()

        Text(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            text = "Current player: ${state.currentPLayer} Winner: ${state.winner}",
            color = MyTicTacTheme.colours.contentPrimary,
            textAlign = TextAlign.Center
        )

        BoxWithConstraints(
            modifier = Modifier.weight(1F)
        ) {
            val centerOffset = Offset(x = constraints.maxWidth / 2F, y = constraints.maxHeight / 2F)
            val lineLength = constraints.maxWidth * 0.7F

            val fields = FieldController(centerOffset, lineLength)

            TicTacToeField(
                state = state,
                fieldController = fields,
                onTap = viewModel::onFieldTapped,
                setDefault = viewModel::setDefault
            )
        }

        Button(
            modifier = Modifier.padding(bottom = 10.dp),
            onClick = {
                viewModel.reset()
            },
        ) {
            Text("Reset")
        }
    }
}

@Composable
fun TicTacToeField(
    state: GameState,
    fieldController: FieldController,
    onTap: (Int) -> Unit,
    setDefault: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var animations by remember { mutableStateOf(emptyAnimations()) }
    var winningLineAnimation by remember { mutableStateOf(Animatable(0F)) }

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
                tween(LINE_ANIMATION_DURATION, LINE_ANIMATION_DURATION)
            )
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .debouncedFieldClick(
                pointerInputKey = true,
                onClick = { position ->
                    fieldController.getFieldXYFromOffset(position)?.let {
                        onTap(it.id)
                        scope.animateFloatToOne(animations[getIndex(it.id)])
                    }
                }
            )
    ) {
        drawTicTacToeField(lineColor = Color.LightGray, field = fieldController.fieldPitch)

        state.playerX.forEach { field ->
            drawCross(
                fieldXY = fieldController.getFieldXYFromId(field),
                animate = animations[getIndex(field.id)].value
            )
        }

        state.playerO.forEach { field ->
            drawTicCircle(
                fieldXY = fieldController.getFieldXYFromId(field),
                animate = animations[getIndex(field.id)].value
            )
        }

        if (state.winningSet.isNotEmpty()) {
            val winningLine = fieldController.getWinningLine(
                start = fieldController.getFieldXYFromId(state.winningSet.first()),
                end = fieldController.getFieldXYFromId(state.winningSet.last())
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
                durationMillis = LINE_ANIMATION_DURATION
            )
        )
    }
}

fun emptyAnimations(): List<Animatable<Float, AnimationVector1D>> {
    return List(9) { Animatable(1f) }
}


fun DrawScope.drawCross(fieldXY: FieldXY, animate: Float) {
    val path = Path().apply {
        val endOffset = 60F
        with(fieldXY) {
            moveTo(x = topLeft.x + endOffset, y = topLeft.y + endOffset)
            lineTo(x = bottomRight.x - endOffset, y = bottomRight.y - endOffset)
            moveTo(x = topLeft.x + endOffset, y = bottomRight.y - endOffset)
            lineTo(x = bottomRight.x - endOffset, y = topLeft.y + endOffset)
        }
    }
    drawPath(
        path = path,
        style = Stroke(width = animate, cap = StrokeCap.Round),
        color = Color.Blue,
    )
}

fun DrawScope.drawTicCircle(fieldXY: FieldXY, animate: Float) {
    val endOffset = 60F
    drawArc(
        useCenter = false,
        topLeft = Offset(fieldXY.topLeft.x + endOffset, fieldXY.topLeft.y + endOffset),
        startAngle = 0F,
        sweepAngle = 360F,
        size = Size(
            width = fieldXY.bottomRight.x - fieldXY.topLeft.x - endOffset * 2,
            height = fieldXY.bottomRight.y - fieldXY.topLeft.y - endOffset * 2
        ),
        style = Stroke(width = animate, cap = StrokeCap.Round),
        color = Color.Red,
    )
}

fun DrawScope.drawTicTacToeField(
    field: List<Pair<Offset, Offset>>,
    lineColor: Color
) {

    field.forEach {
        drawLine(
            color = lineColor,
            start = it.first,
            end = it.second,
            strokeWidth = 15F,
            cap = StrokeCap.Round
        )
    }
}

fun getIndex(id: Int) =
    when (id) {
        11 -> 0
        12 -> 1
        13 -> 2
        21 -> 3
        22 -> 4
        23 -> 5
        31 -> 6
        32 -> 7
        33 -> 8
        else -> -1
    }
