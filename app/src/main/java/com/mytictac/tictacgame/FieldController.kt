package com.mytictac.tictacgame

import androidx.compose.ui.geometry.Offset

class FieldController(
    center: Offset,
    lineLength: Float
) {
    private val firstColumnTopX = center.x - lineLength / 2F
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
        id = Fields.One.id
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
        id = Fields.Two.id
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
        id = Fields.Three.id
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
        id = Fields.Four.id
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
        id = Fields.Five.id
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
        id = Fields.Six.id
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
        id = Fields.Seven.id
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
        id = Fields.Eight.id
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
        id = Fields.Nine.id
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

    fun getFieldXYFromId(field: Fields) = gameFields.first { it.id == field.id }

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
        val type = when {
            start.topLeft.y == end.topLeft.y -> WinningLine.HORIZONTAL
            start.topLeft.x == end.topLeft.x -> WinningLine.VERTICAL
            start.topLeft.x > end.topLeft.x -> WinningLine.DIAGONAL_ASCENDING
            else -> WinningLine.DIAGONAL_DESCENDING
        }

        return when (type) {
            WinningLine.VERTICAL -> {
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

            WinningLine.HORIZONTAL -> {
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

            WinningLine.DIAGONAL_DESCENDING -> {
                val first = start.topLeft
                val second = end.bottomRight
                first to second
            }

            WinningLine.DIAGONAL_ASCENDING -> {
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
        }
    }

    enum class WinningLine {
        VERTICAL, HORIZONTAL, DIAGONAL_ASCENDING, DIAGONAL_DESCENDING
    }

}

data class FieldXY(val topLeft: Offset, val bottomRight: Offset, val  id: Int)