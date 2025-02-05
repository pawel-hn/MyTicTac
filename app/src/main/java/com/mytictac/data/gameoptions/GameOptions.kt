package com.mytictac.data.gameoptions

import com.mytictac.data.DifficultyLevel
import com.mytictac.data.Player
import com.mytictac.start.FirstPlayer

data class GameOptions(
    val singlePlayer: Boolean,
    val firstPlayer: FirstPlayer,
    val difficultyLevel: DifficultyLevel,
    val cross: Player.Cross,
    val circle: Player.Circle,
)
