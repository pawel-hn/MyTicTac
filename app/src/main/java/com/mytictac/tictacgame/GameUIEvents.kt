package com.mytictac.tictacgame

sealed class GameUIEvents {
    data object ResetGame : GameUIEvents()
    data object WinningSet : GameUIEvents()
    data object ComputerMove : GameUIEvents()
}