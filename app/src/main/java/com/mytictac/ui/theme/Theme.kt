package com.mytictac.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

@Composable
fun MyTicTacTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalTicTacColors provides if (darkTheme) TicTacDarkColors else TicTacLightColors
    ) {
        content.invoke()
    }
}

object MyTicTacTheme {
    val colours: TicTacColors
        @Composable
        get() = LocalTicTacColors.current
}

val LocalTicTacColors =
    staticCompositionLocalOf {
        TicTacLightColors
    }
