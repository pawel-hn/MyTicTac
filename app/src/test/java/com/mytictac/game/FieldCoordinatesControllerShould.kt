package com.mytictac.game

import androidx.compose.ui.geometry.Offset
import com.mytictac.data.Field
import com.mytictac.game.compose.FieldCoordinatesController
import org.junit.Assert.*
import org.junit.Test

class FieldCoordinatesControllerTest {
    private val center = Offset(50f, 50f)
    private val lineLength = 30f
    private val controller = FieldCoordinatesController(center, lineLength)

    @Test
    fun `getFieldXYFromId should return correct field`() {
        val field = controller.getFieldXYFromId(Field.Five)
        assertEquals(Field.Five.id, field.id)
    }

    @Test
    fun `getFieldXYFromOffset should return correct field`() {
        val offset = Offset(center.x, center.y)
        val field = controller.getFieldXYFromOffset(offset)
        assertNotNull(field)
        assertEquals(Field.Five.id, field?.id)
    }

    @Test
    fun `getFieldXYFromOffset should return null for out of bounds offset`() {
        val offset = Offset(1000f, 1000f)
        val field = controller.getFieldXYFromOffset(offset)
        assertNull(field)
    }

    @Test
    fun `getWinningLine should return correct horizontal line`() {
        val start = controller.getFieldXYFromId(Field.Four)
        val end = controller.getFieldXYFromId(Field.Six)
        val line = controller.getWinningLine(start, end)

        assertEquals((start.topLeft.y + start.bottomRight.y) / 2, line.first.y)
        assertEquals((start.topLeft.y + start.bottomRight.y) / 2, line.second.y)
    }

    @Test
    fun `getWinningLine should return correct vertical line`() {
        val start = controller.getFieldXYFromId(Field.One)
        val end = controller.getFieldXYFromId(Field.Seven)
        val line = controller.getWinningLine(start, end)

        assertEquals((start.topLeft.x + start.bottomRight.x) / 2, line.first.x)
        assertEquals((end.topLeft.x + end.bottomRight.x) / 2, line.second.x)
    }

    @Test
    fun `getWinningLine should return correct diagonal line ascending`() {
        val start = controller.getFieldXYFromId(Field.Three)
        val end = controller.getFieldXYFromId(Field.Seven)
        val line = controller.getWinningLine(start, end)

        assertEquals(start.bottomRight.x, line.first.x)
        assertEquals(end.topLeft.x, line.second.x)
    }

    @Test
    fun `getWinningLine should return correct diagonal line descending`() {
        val start = controller.getFieldXYFromId(Field.One)
        val end = controller.getFieldXYFromId(Field.Nine)
        val line = controller.getWinningLine(start, end)

        assertEquals(start.topLeft, line.first)
        assertEquals(end.bottomRight, line.second)
    }
}
