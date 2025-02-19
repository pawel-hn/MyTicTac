package com.mytictac.start

import com.mytictac.R
import com.mytictac.data.DifficultyLevel


data class StartScreenUIState(
    val singlePLayer: Boolean,
    val startScreenFirstPlayerUI: StartScreenFirstPlayerUI,
    val difficultyLevel: DifficultyLevel,
    val loadGameButtonEnabled: Boolean
)

val defaultStartScreenUIState = StartScreenUIState(
    singlePLayer = true,
    startScreenFirstPlayerUI = StartScreenFirstPlayerUI.Circle,
    difficultyLevel = DifficultyLevel.EASY,
    loadGameButtonEnabled = false
)

enum class StartScreenFirstPlayerUI(val label: Int, short: Char) {
    Cross(R.string.first_player_x, 'X'),
    Circle(R.string.first_player_o, 'O')
}