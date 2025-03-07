package com.mytictac.game

import androidx.lifecycle.SavedStateHandle
import com.mytictac.data.CurrentGame
import com.mytictac.data.Field
import com.mytictac.data.GameEndResult
import com.mytictac.data.Participant
import com.mytictac.data.Player
import com.mytictac.data.PlayerState
import com.mytictac.data.victories
import com.mytictac.gameengine.GameEngine
import com.mytictac.gameengine.GameEvent
import com.mytictac.ui.screenshot.ScreenShotViewController
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
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
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GameViewModelShould {
    private lateinit var sut: GameViewModel

    private val mockGameEngine: GameEngine = mockk(relaxed = true)
    private val mockScreenShotViewController: ScreenShotViewController = mockk(relaxed = true)

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val defaultSavedStateHandle =
        SavedStateHandle(mapOf(GameViewModelArguments.LOAD_GAME to false))

    private val gameState =
        CurrentGame(
            currentPLayer = Player.Cross(Participant.Human),
            cross = PlayerState(Player.Cross(Participant.Human), emptySet()),
            circle = PlayerState(Player.Cross(Participant.Human), emptySet()),
            isGameRunning = true
        )
    private val gameStateFlow = MutableStateFlow(gameState)

    private val gameEventFlow = MutableSharedFlow<GameEvent>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit correct UI state when gameEngine state changes`() =
        runTest {
            // given
            every { mockGameEngine.state } returns gameStateFlow
            sut = GameViewModel(mockGameEngine, mockScreenShotViewController, defaultSavedStateHandle)

            // then
            advanceUntilIdle()
            val state = sut.state.first()
            assertEquals(
                GameUIState.CurrentCurrentGameUI(
                    currentPLayer = gameState.currentPLayer,
                    cross = gameState.cross,
                    circle = gameState.circle
                ),
                state
            )
        }

    @Test
    fun `should emit ComputerMove event when gameEngine emits ComputerMove`() =
        runTest {
            // given
            every { mockGameEngine.gameEvent } returns gameEventFlow
            every { mockGameEngine.state } returns gameStateFlow

            sut = GameViewModel(mockGameEngine, mockScreenShotViewController, defaultSavedStateHandle)

            val events = mutableListOf<GameUIEvents>()
            val job =
                backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                    sut.event.toList(events)
                }

            // when
            gameEventFlow.emit(GameEvent.ComputerMove(fieldId = Field.One.id))

            // then
            assertEquals(GameUIEvents.ComputerMove(Field.One.id), events.first())
            assertEquals(1, events.size)

            job.cancel()
        }

    @Test
    fun `should emit VictoryLine event when gameEngine emits GameEnd with winner`() =
        runTest {
            // given
            every { mockGameEngine.gameEvent } returns gameEventFlow
            every { mockGameEngine.state } returns gameStateFlow

            sut = GameViewModel(mockGameEngine, mockScreenShotViewController, defaultSavedStateHandle)

            val events = mutableListOf<GameUIEvents>()
            val job =
                backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                    sut.event.toList(events)
                }

            // when
            gameEventFlow.emit(GameEvent.GameEnd(result = GameEndResult.Cross, victories.first()))

            // then
            assertEquals(GameUIEvents.VictoryLine(victories.first()), events.first())
            assertEquals(1, events.size)

            job.cancel()
        }

    @Test
    fun `should not emit VictoryLine event when game ends in a draw`() =
        runTest {
            // given
            every { mockGameEngine.gameEvent } returns gameEventFlow
            every { mockGameEngine.state } returns gameStateFlow

            sut = GameViewModel(mockGameEngine, mockScreenShotViewController, defaultSavedStateHandle)

            val events = mutableListOf<GameUIEvents>()
            val job =
                backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                    sut.event.toList(events)
                }

            // when
            gameEventFlow.emit(GameEvent.GameEnd(result = GameEndResult.Draw, emptySet()))

            // then
            assertEquals(0, events.size)

            job.cancel()
        }

    @Test
    fun `should emit showDialog event when on gesture back and game is running`() =
        runTest {
            // given
            every { mockGameEngine.gameEvent } returns gameEventFlow
            every { mockGameEngine.state } returns gameStateFlow

            sut = GameViewModel(mockGameEngine, mockScreenShotViewController, defaultSavedStateHandle)

            val events = mutableListOf<GameUIEvents>()
            val job =
                backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                    sut.event.toList(events)
                }

            // when
            sut.onGestureBack()

            // then
            assertEquals(GameUIEvents.ShowDialog(GameDialog.CancelGame), events.first())
            assertEquals(1, events.size)

            job.cancel()
        }

    @Test
    fun `should emit navigateToMainScreen event when on gesture back and game is not running`() =
        runTest {
            // given
            every { mockGameEngine.gameEvent } returns gameEventFlow
            every { mockGameEngine.state } returns gameStateFlow

            sut =
                GameViewModel(mockGameEngine, mockScreenShotViewController, defaultSavedStateHandle)

            val events = mutableListOf<GameUIEvents>()
            val job =
                backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                    sut.event.toList(events)
                }

            // when
            gameStateFlow.value = gameState.copy(isGameRunning = false)
            sut.onGestureBack()

            // then
            assertEquals(GameUIEvents.NavigateToMainScreen, events.first())
            assertEquals(1, events.size)

            job.cancel()
        }

    @Test
    fun `should emit ResetGame event when reset is clicked`() =
        runTest {
            // given
            every { mockGameEngine.gameEvent } returns gameEventFlow
            every { mockGameEngine.state } returns gameStateFlow

            sut = GameViewModel(mockGameEngine, mockScreenShotViewController, defaultSavedStateHandle)

            val events = mutableListOf<GameUIEvents>()
            val job =
                backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                    sut.event.toList(events)
                }
            advanceUntilIdle()

            // when
            sut.reset()

            // then
            assertEquals(GameUIEvents.ResetGame, events.first())
            assertEquals(1, events.size)

            job.cancel()
        }

    @Test
    fun `should load game when loadGame is true`() =
        runTest {
            // given
            val loadedFields = setOf(Field.One, Field.Five, Field.Six)
            every { mockGameEngine.gameEvent } returns gameEventFlow
            every { mockGameEngine.state } returns gameStateFlow

            val savedStateHandleLoadGame =
                SavedStateHandle(mapOf(GameViewModelArguments.LOAD_GAME to true))

            sut = GameViewModel(mockGameEngine, mockScreenShotViewController, savedStateHandleLoadGame)

            val events = mutableListOf<GameUIEvents>()
            val job =
                backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                    sut.event.toList(events)
                }

            // when
            gameEventFlow.emit(GameEvent.GameLoaded(loadedFields))

            // then
            coVerify(exactly = 1) { mockGameEngine.loadGame() }
            assertEquals(GameUIEvents.GameLoaded(loadedFields), events.first())
            assertEquals(1, events.size)

            job.cancel()
        }
}
