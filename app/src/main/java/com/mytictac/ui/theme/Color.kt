package com.mytictac.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class TicTacColors(
    val backgroundScreen: Color,
    val contentPrimary: Color,
    val contentSecondary: Color,
    val interactivePrimary: Color,
    val interactivePrimaryContent: Color,
    val interactiveSecondary: Color,
    val interactiveSecondaryContent: Color,
    val interactiveTertiary: Color,
    val interactiveTertiaryContent: Color
)

val TicTacLightColors =
    TicTacColors(
        backgroundScreen = Color.White,
        contentPrimary = Color.Black,
        contentSecondary = Color.Gray,
        interactivePrimary = Color(0xFF1E88E5),
        interactivePrimaryContent = Color(0xFF99ffff),
        interactiveSecondary = Color(0xFF2E7D32),
        interactiveSecondaryContent = Color(0xFF7FFF00),
        interactiveTertiary = Color(0xFF607D8B),
        interactiveTertiaryContent = Color(0xFFB0BEC5)
    )

val TicTacDarkColors =
    TicTacColors(
        backgroundScreen = Color.Black,
        contentPrimary = Color.White,
        contentSecondary = Color.LightGray,
        interactivePrimary = Color(0xFF1E88E5),
        interactivePrimaryContent = Color(0xFF99ffff),
        interactiveSecondary = Color(0xFF2E7D32),
        interactiveSecondaryContent = Color(0xFF7FFF00),
        interactiveTertiary = Color(0xFF607D8B),
        interactiveTertiaryContent = Color(0xFFB0BEC5)
    )
