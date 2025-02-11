package com.mytictac.tictacgame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytictac.gameengine.GameEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TicTacViewModel @Inject constructor(
    private val gameEngine: GameEngine
) : ViewModel() {

    private val _state = MutableStateFlow<GameState>(GameState.Loading)
    val state: StateFlow<GameState> = _state.asStateFlow()

    val animateMove: SharedFlow<Int> = gameEngine.computerMove
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(500))

    init {
        viewModelScope.launch {
            delay(500)
            gameEngine.state.collect {
                _state.value = GameState.CurrentGame(
                    currentPLayer = it.currentPLayer,
                    cross = it.cross,
                    circle = it.circle,
                    gameEndResult = it.endResult,
                    winningSet = it.winningSet,
                    reset = false
                )
            }
        }
    }

    fun fieldTapped(id: Int, computerMove: Boolean) {
        gameEngine.selectedField(id, computerMove)
    }

    fun reset() {
        _state.update {
            if (it !is GameState.CurrentGame) return@update it
            it.copy(reset = true)
        }
    }

    fun setDefault() {
        gameEngine.setDefault()
    }
}
