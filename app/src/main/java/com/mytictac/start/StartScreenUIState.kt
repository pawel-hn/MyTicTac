package com.mytictac.start

import com.mytictac.R
import com.mytictac.data.DifficultyLevel


data class StartScreenUIState(
    val singlePLayer: Boolean,
    val firstPlayer: FirstPlayer,
    val difficultyLevel: DifficultyLevel
)

val defaultStartScreenUIState = StartScreenUIState(
    singlePLayer = true,
    firstPlayer = FirstPlayer.Circle,
    difficultyLevel = DifficultyLevel.IMPOSSIBLE
)

enum class FirstPlayer(val label: Int, short: Char) {
    Cross(R.string.first_player_x, 'X'),
    Circle(R.string.first_player_o, 'O')
}