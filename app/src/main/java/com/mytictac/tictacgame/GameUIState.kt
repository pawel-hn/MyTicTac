package com.mytictac.tictacgame

import com.mytictac.data.Player
import com.mytictac.data.PlayerState
import com.mytictac.data.GameEndResult

sealed class GameUIState {
    data object Loading : GameUIState()
    data class CurrentCurrentGameUI(
        val currentPLayer: Player,
        val cross: PlayerState,
        val circle: PlayerState,
    ) : GameUIState()
}