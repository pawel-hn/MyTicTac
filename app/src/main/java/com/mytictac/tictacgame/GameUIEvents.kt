package com.mytictac.tictacgame

import com.mytictac.data.Field

sealed interface GameUIEvents {
    data object ResetGame : GameUIEvents
    data class VictoryLine(val winningFields: Set<Field>) : GameUIEvents
    data class ComputerMove(val fieldId: Int) : GameUIEvents
    data class ShowDialog(val dialog: GameDialog) : GameUIEvents
    data object NavigateToMainScreen : GameUIEvents
}

sealed interface GameDialog {
    val title: String
    val message: String
    val confirmButtonText: String
    val cancelButtonText: String

    data object CancelGame : GameDialog {
        override val title: String = "Cancel Game"
        override val message: String = "Are you sure you want to cancel the game?"
        override val confirmButtonText: String = "Yes"
        override val cancelButtonText: String = "No"
    }
}