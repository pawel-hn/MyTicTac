package com.mytictac.start

import com.mytictac.data.DifficultyLevel
import com.mytictac.data.Player
import com.mytictac.data.PlayerCount

data class StartScreenUIState(
    val playerCount: PlayerCount,
    val firstPlayer: Player,
    val difficultyLevel: DifficultyLevel
)

val defaultStartScreenUIState = StartScreenUIState(
    playerCount = PlayerCount.ONE,
    firstPlayer = Player.PlayerX,
    difficultyLevel = DifficultyLevel.IMPOSSIBLE
)
