package com.mytictac.data.gameoptions

import com.mytictac.data.DifficultyLevel
import com.mytictac.data.Player
import com.mytictac.data.PlayerCount

data class GameOptions(
    val playerCount: PlayerCount,
    val firstPlayer: Player,
    val difficultyLevel: DifficultyLevel
)

