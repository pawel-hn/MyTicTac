package com.mytictac.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytictac.data.GameEndResult
import com.mytictac.gameengine.GameEngine
import com.mytictac.gameengine.GameEvent
import com.mytictac.ui.screenshot.ScreenShotViewController
import com.mytictac.ui.screenshot.TakeAndShareScreenshotResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

object GameViewModelArguments {
    const val LOAD_GAME = "loadGame"
}

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameEngine: GameEngine,
    val screenShotViewController: ScreenShotViewController,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val loadGame: Boolean = savedStateHandle[GameViewModelArguments.LOAD_GAME] ?: false

    private val _state = MutableStateFlow<GameUIState>(GameUIState.Loading)
    val state: StateFlow<GameUIState> = _state.asStateFlow()

    private val _event = MutableSharedFlow<GameUIEvents>(replay = 1)
    val event: SharedFlow<GameUIEvents> = _event.asSharedFlow()

    init {
        viewModelScope.launch {
            delay(1000)
            gameEngine.state.collect {
                _state.value = GameUIState.CurrentCurrentGameUI(
                    currentPLayer = it.currentPLayer,
                    cross = it.cross,
                    circle = it.circle,
                )
            }
        }

        viewModelScope.launch {
            gameEngine.gameEvent.collect {
                when (it) {
                    is GameEvent.ComputerMove -> {
                        _event.emit(GameUIEvents.ComputerMove(it.fieldId))
                    }

                    is GameEvent.GameEnd -> {
                        if (it.result != GameEndResult.Draw) {
                            _event.emit(GameUIEvents.VictoryLine(it.winningSet))
                        }
                    }

                    is GameEvent.GameLoaded -> {
                        _event.emit(GameUIEvents.GameLoaded(it.fields))
                    }
                }
            }
        }

        if (loadGame) {
            viewModelScope.launch { gameEngine.loadGame() }
        }
    }


    fun onGestureBack() {
        viewModelScope.launch {
            if (isGameRunning()) {
                _event.emit(GameUIEvents.ShowDialog(GameDialog.CancelGame))
            } else {
                _event.emit(GameUIEvents.NavigateToMainScreen)
            }
        }
    }

    fun dialogConfirmClick(dialog: GameDialog) {
        viewModelScope.launch {
           val event = when(dialog) {
                GameDialog.CancelGame -> GameUIEvents.NavigateToMainScreen
            }
            _event.emit(event)
        }
    }

    fun fieldTapped(id: Int, computerMove: Boolean) {
        gameEngine.onFieldSelected(id, computerMove)
    }

    fun reset() {
        if (_state.value !is GameUIState.CurrentCurrentGameUI) return

        viewModelScope.launch {
            _event.emit(GameUIEvents.ResetGame)
        }
    }

    fun saveGame() {
        if (isGameRunning()) {
            viewModelScope.launch {
                val gameSaved = gameEngine.saveGame()
                if (gameSaved.isSuccess) {
                    _event.emit(GameUIEvents.ShowToast(GameToast.GameSaved))
                } else if (gameSaved.isFailure) {
                    _event.emit(GameUIEvents.ShowToast(GameToast.GameSaveFail))
                }
            }
        }
    }

    fun setDefault() {
        gameEngine.setDefault()
    }

    fun onShareClick() {
        viewModelScope.launch {
            val time = dateToFormattedStringDayMonthYear(LocalDateTime.now())
            val shareGameResult = screenShotViewController.takeAndShareScreenShot(
                screenshotFileName = "TicTac $time"
            )
            if (shareGameResult == TakeAndShareScreenshotResult.ScreenShotShareFail) {
                _event.emit(GameUIEvents.ShowToast(GameToast.ScreenSharedFail))
            }
        }
    }

    private fun isGameRunning() = gameEngine.state.value.isGameRunning
}

fun dateToFormattedStringDayMonthYear(dateTime: LocalDateTime): String {
    val deviceLocale = Locale.UK
    return dateTime.dayOfMonth.toString() + " " + dateTime.month.getDisplayName(
        TextStyle.SHORT,
        deviceLocale
    ) + " " + dateTime.year.toString() + ", " + "HH:mm".format(dateTime)
}