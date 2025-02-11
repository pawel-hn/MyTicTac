package com.mytictac.data

sealed interface Player {
    val participant: Participant

    data class Cross(override val participant: Participant) : Player
    data class Circle(override val participant: Participant) : Player
}


data class PlayerState(
    val player: Player,
    val moves: Set<Field>
)