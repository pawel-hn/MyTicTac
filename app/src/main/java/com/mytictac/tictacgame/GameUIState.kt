package com.mytictac.tictacgame

import com.mytictac.data.Field
import com.mytictac.data.Participant
import com.mytictac.data.Player

sealed class GameState {
    data object Loading : GameState()
    data class CurrentGame(
        val currentPLayer: Player,
        val cross: PlayerState,
        val circle: PlayerState,
        val winner: Winner?,
        val winningSet: Set<Field>,
        val reset: Boolean,
    ) : GameState()
}

enum class Winner {
    Cross,
    Circle,
    Draw
}

data class PlayerState(
    val player: Player,
    val moves: Set<Field>
)
