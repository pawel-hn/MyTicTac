package com.mytictac.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytictac.data.DifficultyLevel
import com.mytictac.data.FirstPLayer
import com.mytictac.data.gameoptions.GameOptionsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class StartScreenViewModel @Inject constructor(
    private val gameOptionsService: GameOptionsService
) : ViewModel() {

    private val _startScreenEvent = Channel<StartScreenUIEvent>()
    val startScreenEvent: Flow<StartScreenUIEvent> = _startScreenEvent.receiveAsFlow()

    val state: StateFlow<StartScreenUIState> = gameOptionsService.gameOptions.map {
        StartScreenUIState(
            singlePLayer = it.singlePlayer,
            startScreenFirstPlayerUI = when(it.firstPlayer) {
                FirstPLayer.Circle -> StartScreenFirstPlayerUI.Circle
                FirstPLayer.Cross -> StartScreenFirstPlayerUI.Cross
            },
            difficultyLevel = it.difficultyLevel
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(500),
        defaultStartScreenUIState
    )

    fun onPlayerCountChanged(isSinglePlayer: Boolean) =
        gameOptionsService.onPlayerCountChanged(isSinglePlayer)

    fun onDifficultyChanged(difficultyLevel: DifficultyLevel) =
        gameOptionsService.onDifficultyChanged(difficultyLevel)

    fun onFirstPlayerChanged(player: StartScreenFirstPlayerUI) =

        gameOptionsService.onFirstPlayerChanged( when(player) {
            StartScreenFirstPlayerUI.Circle -> FirstPLayer.Circle
            StartScreenFirstPlayerUI.Cross -> FirstPLayer.Cross
        })

    fun onStartGame() {
        viewModelScope.launch {
            _startScreenEvent.send(StartScreenUIEvent.StartGame)
        }
    }
}
