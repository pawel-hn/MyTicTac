package com.mytictac.data

import kotlinx.serialization.Serializable

@Serializable
sealed interface Player {
    val participant: Participant

    @Serializable
    data class Cross(override val participant: Participant) : Player

    @Serializable
    data class Circle(override val participant: Participant) : Player
}

@Serializable
data class PlayerState(
    val player: Player,
    val moves: Set<Field>
)
