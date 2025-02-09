package com.mytictac.tictacgame.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

fun DrawScope.drawCross(fieldXY: FieldXY, width: Float, endOffset: Float = 60F) {
    val path = Path().apply {
        with(fieldXY) {
            moveTo(x = topLeft.x + endOffset, y = topLeft.y + endOffset)
            lineTo(x = bottomRight.x - endOffset, y = bottomRight.y - endOffset)
            moveTo(x = topLeft.x + endOffset, y = bottomRight.y - endOffset)
            lineTo(x = bottomRight.x - endOffset, y = topLeft.y + endOffset)
        }
    }
    drawPath(
        path = path,
        style = Stroke(width = width, cap = StrokeCap.Round),
        color = Color.Red,
    )
}

fun DrawScope.drawTicCircle(fieldXY: FieldXY, width: Float, endOffset: Float = 60F) {
    drawArc(
        useCenter = false,
        topLeft = Offset(fieldXY.topLeft.x + endOffset, fieldXY.topLeft.y + endOffset),
        startAngle = 0F,
        sweepAngle = 360F,
        size = Size(
            width = fieldXY.bottomRight.x - fieldXY.topLeft.x - endOffset * 2,
            height = fieldXY.bottomRight.y - fieldXY.topLeft.y - endOffset * 2
        ),
        style = Stroke(width = width, cap = StrokeCap.Round),
        color = Color.Blue,
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