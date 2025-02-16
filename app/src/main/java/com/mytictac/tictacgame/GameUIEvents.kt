package com.mytictac.tictacgame

import com.mytictac.data.Field

sealed class GameUIEvents {
    data object ResetGame : GameUIEvents()
    data class VictoryLine(val winningFields: Set<Field>) : GameUIEvents()
    data class ComputerMove(val fieldId: Int) : GameUIEvents()
}