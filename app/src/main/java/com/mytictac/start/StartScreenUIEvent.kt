package com.mytictac.start

sealed interface StartScreenUIEvent {
    data object StartGame : StartScreenUIEvent
}

