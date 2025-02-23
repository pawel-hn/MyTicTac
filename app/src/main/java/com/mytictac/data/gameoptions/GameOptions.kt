package com.mytictac.data.gameoptions

import com.mytictac.data.DifficultyLevel
import com.mytictac.data.FirstPLayer
import com.mytictac.data.Player
import kotlinx.serialization.Serializable

@Serializable
data class GameOptions(
    val singlePlayer: Boolean,
    val firstPlayer: FirstPLayer,
    val difficultyLevel: DifficultyLevel,
    val cross: Player.Cross,
    val circle: Player.Circle
)
