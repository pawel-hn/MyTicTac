package com.mytictac.tictacgame

import com.mytictac.data.Field
import com.mytictac.data.Player
import com.mytictac.data.PlayerState
import com.mytictac.data.GameEndResult

sealed class GameState {
    data object Loading : GameState()
    data class CurrentGame(
        val currentPLayer: Player,
        val cross: PlayerState,
        val circle: PlayerState,
        val gameEndResult: GameEndResult?,
        val winningSet: Set<Field>,
        val reset: Boolean,
    ) : GameState()
}
