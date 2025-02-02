package com.mytictac.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytictac.data.DifficultyLevel
import com.mytictac.data.Player
import com.mytictac.data.PlayerCount
import com.mytictac.data.gameoptions.GameOptionsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class StartScreenViewModel @Inject constructor(
    private val gameOptionsService: GameOptionsService
) : ViewModel() {

    val state: StateFlow<StartScreenUIState> = gameOptionsService.gameOptions.map {
        StartScreenUIState(
            playerCount = it.playerCount,
            firstPlayer = it.firstPlayer,
            difficultyLevel = it.difficultyLevel
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(500),
        defaultStartScreenUIState
    )

    fun onPlayerCountChanged(playerCount: PlayerCount) =
        gameOptionsService.onPlayerCountChanged(playerCount)

    fun onDifficultyChanged(difficultyLevel: DifficultyLevel) =
        gameOptionsService.onDifficultyChanged(difficultyLevel)

    fun onFirstPlayerChanged(player: Player) =
        gameOptionsService.onFirstPlayerChanged(player)
}
