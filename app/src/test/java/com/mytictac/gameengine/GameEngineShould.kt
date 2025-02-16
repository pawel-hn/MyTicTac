package com.mytictac.gameengine

import com.mytictac.data.Field
import com.mytictac.data.GameEndResult
import com.mytictac.data.Participant
import com.mytictac.data.Player
import com.mytictac.data.gameoptions.GameOptionsService
import com.mytictac.data.gameoptions.defaultGameOptions
import com.mytictac.data.victories
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GameEngineShould {
    private lateinit var sut: GameEngine

    private val mockGameOptionsService: GameOptionsService = mockk()
    private val gameOptionsFlow = MutableStateFlow(defaultGameOptions)
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val fieldId = Field.One.id

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial game state is correct`() = runTest {
        // given
        every { mockGameOptionsService.gameOptions }.returns(gameOptionsFlow)
        sut = AndroidGameEngine(mockGameOptionsService, testScope)
        val initialState = sut.state.first()

        // then
        assertEquals(Player.Circle(Participant.Human), initialState.currentPLayer)
        assertTrue(initialState.isGameRunning)
        assertTrue(initialState.cross.moves.isEmpty())
        assertTrue(initialState.circle.moves.isEmpty())
    }

    @Test
    fun `onFieldSelected should trigger computer move on singlePLayer`() = runTest {
        // given
        every { mockGameOptionsService.gameOptions }.returns(gameOptionsFlow)
        sut = AndroidGameEngine(mockGameOptionsService, testScope)

        val events = mutableListOf<GameEvent>()
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            sut.gameEvent.toList(events)
        }

        // when
        sut.onFieldSelected(fieldId, computerMove = false)
        advanceUntilIdle()

        // Then
        val gameState = sut.state.first()

        assertEquals(1, gameState.circle.moves.size)
        assertEquals(1, gameState.cross.moves.size)
        assertTrue(gameState.currentPLayer is Player.Circle)
        assert(events.first() is GameEvent.ComputerMove)

        job.cancel()
    }

    @Test
    fun `onFieldSelected should not allow duplicate moves`() = runTest {
        // given
        every { mockGameOptionsService.gameOptions }.returns(gameOptionsFlow)
        sut = AndroidGameEngine(mockGameOptionsService, testScope)

        // When
        sut.onFieldSelected(fieldId, computerMove = false)
        advanceUntilIdle()
        sut.onFieldSelected(fieldId, computerMove = false)
        advanceUntilIdle()

        // Then
        val gameState = sut.state.first()
        assertEquals(1, gameState.cross.moves.size)
        assertEquals(1, gameState.circle.moves.size)
    }

    @Test
    fun `setDefault should reset game state`() = runTest {
        // given
        every { mockGameOptionsService.gameOptions }.returns(gameOptionsFlow)
        sut = AndroidGameEngine(mockGameOptionsService, testScope)

        // Given
        sut.onFieldSelected(fieldId, computerMove = false)
        advanceUntilIdle()

        // When
        sut.setDefault()

        // Then
        val gameState = sut.state.first()
        assertEquals(0, gameState.cross.moves.size)
        assertEquals(0, gameState.circle.moves.size)
        assertTrue(gameState.isGameRunning)
    }

    @Test
    fun `checkIfGameEnd should detect a win and sed event`() = runTest {
        // given
        every { mockGameOptionsService.gameOptions } returns
                MutableStateFlow(defaultGameOptions.copy(singlePlayer = false))

        sut = AndroidGameEngine(mockGameOptionsService, testScope)

        val events = mutableListOf<GameEvent>()
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            sut.gameEvent.toList(events)
        }

        // Given
        val winningMoves = victories.first().toList()
        val moves = setOf(
            winningMoves[0], Field.Four, winningMoves[1], Field.Five, winningMoves[2]
        )

        // When
        moves.forEach { field ->
            sut.onFieldSelected(field.id, computerMove = false)
        }

        // Then
        val gameState = sut.state.first()
        assertEquals(3, gameState.circle.moves.size)
        assertEquals(2, gameState.cross.moves.size)
        assertEquals(
            GameEvent.GameEnd(
                result = GameEndResult.Circle,
                winningSet = victories.first()
            ),
            events.first()
        )
        assertFalse(gameState.isGameRunning)

        job.cancel()
    }

    @Test
    fun `checkIfGameEnd should detect a draw`() = runTest {
        // Given
        every { mockGameOptionsService.gameOptions } returns
                MutableStateFlow(defaultGameOptions.copy(singlePlayer = false))

        sut = AndroidGameEngine(mockGameOptionsService, testScope)
        val allFields = Field.entries.map { it }

        val events = mutableListOf<GameEvent>()
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            sut.gameEvent.toList(events)
        }

        // When
        allFields.forEach { field ->
            // adjustment to simulate game with draw
            val tappedField = when (field) {
                Field.Six -> Field.Nine
                Field.Seven -> Field.Six
                Field.Eight -> Field.Seven
                Field.Nine -> Field.Eight
                else -> field
            }
            sut.onFieldSelected(tappedField.id, computerMove = false)
        }

        // Then
        val gameState = sut.state.first()
        assertFalse(gameState.isGameRunning)
        assertEquals(
            GameEvent.GameEnd(
                result = GameEndResult.Draw,
                winningSet = emptySet()
            ),
            events.first()
        )

        job.cancel()
    }
}
