package com.mytictac.gameengine

import com.mytictac.data.DifficultyLevel
import com.mytictac.data.FirstPLayer
import com.mytictac.data.Participant
import com.mytictac.data.Player
import com.mytictac.data.gameoptions.GameOptions
import com.mytictac.data.gameoptions.GameOptionsService
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow

class GameEngineShould  {
    lateinit var sut: GameEngine

    private val mockGameOptionsService: GameOptionsService = mockk()

    private val gameOptionsFlow = MutableStateFlow<GameOptions>(
        GameOptions(
            singlePlayer = false,
            firstPlayer = FirstPLayer.Circle,
            difficultyLevel = DifficultyLevel.IMPOSSIBLE,
            cross = Player.Cross(participant = Participant.Human),
            circle = Player.Circle(participant = Participant.Human)
        )
    )

}