package com.example.mytictac.tictacgame

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

    private val fields = listOf(
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

    fun getFieldXYFromId(field: Field) = fields.first { it.id == field.id }

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
            return fields.first { it.id == id }
        }

        return null
    }

}

data class FieldXY(val topLeft: Offset, val bottomRight: Offset, val  id: Int)