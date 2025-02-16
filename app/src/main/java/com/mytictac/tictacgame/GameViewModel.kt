package com.mytictac.tictacgame

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytictac.gameengine.GameEngine
import com.mytictac.gameengine.GameEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TicTacViewModel @Inject constructor(
    private val gameEngine: GameEngine
) : ViewModel() {

    private val _state = MutableStateFlow<GameUIState>(GameUIState.Loading)
    val state: StateFlow<GameUIState> = _state.asStateFlow()

    private val _event = MutableSharedFlow<GameUIEvents>()
    val event: SharedFlow<GameUIEvents> = _event.asSharedFlow()

    init {
        viewModelScope.launch {
            launch {
                gameEngine.state.collect {
                    _state.value = GameUIState.CurrentCurrentGameUI(
                        currentPLayer = it.currentPLayer,
                        cross = it.cross,
                        circle = it.circle,
                    )
                }
            }
            launch {
                gameEngine.gameEvent.collect {
                    Log.d("PHN", "vm collect: ${it}")
                    when (it) {
                        is GameEvent.ComputerMove -> {
                            _event.emit(GameUIEvents.ComputerMove(it.fieldId))
                        }
                        is GameEvent.GameEnd -> {
                            _event.emit(GameUIEvents.VictoryLine(it.winningSet))
                        }                        }
                }
            }
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

    fun setDefault() {
        gameEngine.setDefault()
    }
}
