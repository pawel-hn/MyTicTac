package com.mytictac.data

import com.mytictac.data.gameoptions.GameOptions
import kotlinx.serialization.Serializable

@Serializable
data class SaveGame(
    val currentGame: CurrentGame,
    val options: GameOptions
)