package com.mytictac.game.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import com.mytictac.data.Field

class FieldCoordinatesController(
    center: Offset,
    lineLength: Float
) {
    private val firstColumnTopX = center.x - lineLength / 2
    private val firstColumnBottomX = center.x - lineLength / 6
    private val secondColumnBottomX = center.x + lineLength / 6
    private val thirdColumnBottomX = center.x + lineLength / 2

    private val firstRowTopY = center.y - lineLength / 2
    private val firstRowBottomY = center.y - lineLength / 6
    private val secondRowBottomY = center.y + lineLength / 6
    private val thirdRowBottomY = center.y + lineLength / 2

    private val fieldOne = FieldXY(
        topLeft = Offset(
            x = firstColumnTopX,
            y = firstRowTopY
        ),
        bottomRight = Offset(
            x = firstColumnBottomX,
            y = firstRowBottomY
        ),
        id = Field.One.id
    )
    private val fieldTwo = FieldXY(
        topLeft = Offset(
            x = firstColumnBottomX,
            y = firstRowTopY
        ),
        bottomRight = Offset(
            x = secondColumnBottomX,
            y = firstRowBottomY
        ),
        id = Field.Two.id
    )
    private val fieldThree = FieldXY(
        topLeft = Offset(
            x = secondColumnBottomX,
            y = firstRowTopY
        ),
        bottomRight =
        Offset(
            x = thirdColumnBottomX,
            y = firstRowBottomY
        ),
        id = Field.Three.id
    )
    private val fieldFour = FieldXY(
        topLeft = Offset(
            x = firstColumnTopX,
            y = firstRowBottomY
        ),
        bottomRight = Offset(
            x = firstColumnBottomX,
            y = secondRowBottomY
        ),
        id = Field.Four.id
    )
    private val fieldFive = FieldXY(
        topLeft = Offset(
            x = firstColumnBottomX,
            y = firstRowBottomY
        ),
        bottomRight = Offset(
            x = secondColumnBottomX,
            y = secondRowBottomY
        ),
        id = Field.Five.id
    )
    private val fieldSix = FieldXY(
        topLeft = Offset(
            x = secondColumnBottomX,
            y = firstRowBottomY
        ),
        bottomRight = Offset(
            x = thirdColumnBottomX,
            y = secondRowBottomY
        ),
        id = Field.Six.id
    )
    private val fieldSeven = FieldXY(
        topLeft = Offset(
            x = firstColumnTopX,
            y = secondRowBottomY
        ),
        bottomRight = Offset(
            x = firstColumnBottomX,
            y = thirdRowBottomY
        ),
        id = Field.Seven.id
    )
    private val fieldEight = FieldXY(
        topLeft = Offset(
            x = firstColumnBottomX,
            y = secondRowBottomY
        ),
        bottomRight = Offset(
            x = secondColumnBottomX,
            y = thirdRowBottomY
        ),
        id = Field.Eight.id
    )
    private val fieldNine = FieldXY(
        topLeft = Offset(
            x = secondColumnBottomX,
            y = secondRowBottomY
        ),
        bottomRight = Offset(
            x = thirdColumnBottomX,
            y = thirdRowBottomY,
        ),
        id = Field.Nine.id
    )

    private val gameFields = listOf(
        fieldOne,
        fieldTwo,
        fieldThree,
        fieldFour,
        fieldFive,
        fieldSix,
        fieldSeven,
        fieldEight,
        fieldNine
    )

    private val firstHorizontalLine = Pair(
        Offset(
            x = center.x - lineLength / 2,
            y = center.y - lineLength / 6
        ),
        Offset(
            x = center.x + lineLength / 2,
            y = center.y - lineLength / 6
        )
    )

    private val secondHorizontalLine = Pair(
        Offset(
            x = center.x - lineLength / 2,
            y = center.y + lineLength / 6
        ),
        Offset(
            x = center.x + lineLength / 2,
            y = center.y + lineLength / 6
        )
    )

    private val firstVerticalLine = Pair(
        Offset(
            x = center.x - lineLength / 6,
            y = center.y - lineLength / 2
        ),
        Offset(
            x = center.x - lineLength / 6,
            y = center.y + lineLength / 2
        )
    )

    private val secondVerticalLine = Pair(
        Offset(
            x = center.x + lineLength / 6,
            y = center.y - lineLength / 2
        ),
        Offset(
            x = center.x + lineLength / 6,
            y = center.y + lineLength / 2
        )
    )

    val fieldPitch = listOf(
        firstHorizontalLine,
        secondHorizontalLine,
        firstVerticalLine,
        secondVerticalLine
    )

    fun getFieldXYFromId(field: Field) = gameFields.first { it.id == field.id }

    fun getFieldXYFromOffset(offset: Offset): FieldXY? {
        val column = when {
            (offset.x > firstColumnTopX) && (offset.x <= firstColumnBottomX) -> 1
            (offset.x > firstColumnBottomX) && (offset.x <= secondColumnBottomX) -> 2
            (offset.x > secondColumnBottomX) && (offset.x <= thirdColumnBottomX) -> 3
            else -> -1
        }
        val row = when {
            (offset.y > firstRowTopY) && (offset.y <= firstRowBottomY) -> 1
            (offset.y > firstRowBottomY) && (offset.y <= secondRowBottomY) -> 2
            (offset.y > secondRowBottomY) && (offset.y <= thirdRowBottomY) -> 3
            else -> -1
        }

        if (column > 0 && row > 0) {
            val id = "$row$column".toInt()
            return gameFields.first { it.id == id }
        }

        return null
    }

    fun getWinningLine(start: FieldXY, end: FieldXY): Pair<Offset, Offset> {
        return when {
            // winning line vertical
            start.topLeft.x == end.topLeft.x -> {
                val first = Offset(
                    x = (start.topLeft.x + start.bottomRight.x) / 2,
                    y = start.topLeft.y
                )
                val second = Offset(
                    x = (start.topLeft.x + start.bottomRight.x) / 2,
                    y = end.bottomRight.y
                )
                first to second
            }

            // winning line horizontal
            start.topLeft.y == end.topLeft.y -> {
                val first = Offset(
                    x = start.topLeft.x,
                    y = (start.topLeft.y + start.bottomRight.y) / 2
                )
                val second = Offset(
                    x = end.bottomRight.x,
                    y = (start.topLeft.y + start.bottomRight.y) / 2
                )
                first to second
            }

            // winning line ascending diagonal
            start.topLeft.x > end.topLeft.x -> {
                val first = Offset(
                    x = start.bottomRight.x,
                    y = start.topLeft.y
                )
                val second = Offset(
                    x = end.topLeft.x,
                    y = end.bottomRight.y
                )
                first to second
            }

            // winning line descending diagonal
            else -> {
                val first = start.topLeft
                val second = end.bottomRight
                first to second
            }
        }
    }

    data class FieldXY(val topLeft: Offset, val bottomRight: Offset, val id: Int)
}

@Composable
fun rememberFieldCoordinatesController(
    center: Offset,
    lineLength: Float
): FieldCoordinatesController = remember {
    FieldCoordinatesController(
        center = center,
        lineLength = lineLength
    )
}
