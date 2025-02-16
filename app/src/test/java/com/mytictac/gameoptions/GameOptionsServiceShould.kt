package com.mytictac.gameoptions

import com.mytictac.data.DifficultyLevel
import com.mytictac.data.FirstPLayer
import com.mytictac.data.Participant
import com.mytictac.data.gameoptions.AndroidGameOptionsService
import com.mytictac.data.gameoptions.GameOptionsService
import com.mytictac.data.gameoptions.defaultGameOptions
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GameOptionsServiceShould {
    lateinit var sut: GameOptionsService

    @Before
    fun setUp() {
        sut = AndroidGameOptionsService()
    }

    @Test
    fun `initial game options should be defaultGameOptions`() = runTest {

        // then
        assertEquals(defaultGameOptions, sut.gameOptions.first())
    }

    @Test
    fun `onPlayerCountChanged false should update gameOptions for multiPlayer`() = runTest {
        // Given
        val singlePlayer = false

        // when
        sut.setSinglePlayer(singlePlayer)

        val result = sut.gameOptions.value

        assertEquals(false, result.singlePlayer)
        assertEquals(Participant.Human, result.cross.participant)
        assertEquals(Participant.Human, result.circle.participant)
    }

    @Test
    fun `onPlayerCountChanged false should update gameOptions for singlePlayer`() = runTest {
        // Given
        val singlePlayer = true

        // when
        sut.setSinglePlayer(singlePlayer)

        val result = sut.gameOptions.value

        assertEquals(true, result.singlePlayer)
        assertEquals(Participant.Human, result.circle.participant)
        assertEquals(Participant.Computer, result.cross.participant)
    }

    @Test
    fun `onDifficultyChanged should update gameOptions difficultyLevel`() = runTest {
        // Given
        val difficultyLevel = DifficultyLevel.IMPOSSIBLE

        // When
        sut.onDifficultyChanged(difficultyLevel)

        // Then
        val result = sut.gameOptions.first()
        assertEquals(difficultyLevel, result.difficultyLevel)
    }

    @Test
    fun `onFirstPlayerChanged should update gameOptions firstPlayer`() = runTest {
        // Given
        val firstPlayer = FirstPLayer.Cross

        // When
        sut.onFirstPlayerChanged(firstPlayer)

        // Then
        val result = sut.gameOptions.first()
        assertEquals(firstPlayer, result.firstPlayer)
    }

    @Test
    fun `onFirstPlayerChanged should update players based on singlePlayer mode`() = runTest {
        // Given
        val firstPlayer = FirstPLayer.Cross
        val singlePlayer = true

        // When
        sut.setSinglePlayer(singlePlayer)
        sut.onFirstPlayerChanged(firstPlayer)

        // Then
        val result = sut.gameOptions.first()
        assertEquals(firstPlayer, result.firstPlayer)
        assertEquals(Participant.Computer, result.cross.participant)
        assertEquals(Participant.Human, result.circle.participant)

        // and then
        sut.setSinglePlayer(!singlePlayer)

        val result2 = sut.gameOptions.first()
        assertEquals(FirstPLayer.Circle, result2.firstPlayer)
        assertEquals(Participant.Human, result2.circle.participant)
        assertEquals(Participant.Human, result2.cross.participant)
    }
}
