package com.mytictac.data

import kotlinx.serialization.Serializable

@Serializable
data class CurrentGame(
    val currentPLayer: Player,
    val cross: PlayerState,
    val circle: PlayerState,
    val isGameRunning: Boolean
)
