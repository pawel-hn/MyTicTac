package com.mytictac.data.gameoptions

import com.mytictac.data.DifficultyLevel
import com.mytictac.data.FirstPLayer
import com.mytictac.data.Participant
import com.mytictac.data.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface GameOptionsService {
    val gameOptions: StateFlow<GameOptions>

    fun setSinglePlayer(singlePlayer: Boolean)

    fun onDifficultyChanged(difficultyLevel: DifficultyLevel)

    fun onFirstPlayerChanged(firstPlayer: FirstPLayer)
}

const val PLAYER_CROSS = "cross"
const val PLAYER_CIRCLE = "circle"

class AndroidGameOptionsService : GameOptionsService {
    private val _gameOptions = MutableStateFlow(defaultGameOptions)
    override val gameOptions: StateFlow<GameOptions> = _gameOptions.asStateFlow()

    override fun setSinglePlayer(singlePlayer: Boolean) {
        _gameOptions.update { options ->
            val players = getPlayers(singlePlayer)
            options.copy(
                firstPlayer = FirstPLayer.Circle,
                singlePlayer = singlePlayer,
                cross = players[PLAYER_CROSS] as Player.Cross,
                circle = players[PLAYER_CIRCLE] as Player.Circle
            )
        }
    }

    override fun onDifficultyChanged(difficultyLevel: DifficultyLevel) {
        _gameOptions.update { it.copy(difficultyLevel = difficultyLevel) }
    }

    override fun onFirstPlayerChanged(firstPlayer: FirstPLayer) {
        _gameOptions.update { options ->
            val players = getPlayers(options.singlePlayer)
            options.copy(
                firstPlayer = firstPlayer,
                cross = players[PLAYER_CROSS] as Player.Cross,
                circle = players[PLAYER_CIRCLE] as Player.Circle
            )
        }
    }

    private fun getPlayers(singlePlayer: Boolean): Map<String, Player> {
        return if (singlePlayer) {
            mapOf(
                PLAYER_CROSS to Player.Cross(Participant.Computer),
                PLAYER_CIRCLE to Player.Circle(Participant.Human)
            )
        } else {
            mapOf(
                PLAYER_CROSS to Player.Cross(Participant.Human),
                PLAYER_CIRCLE to Player.Circle(Participant.Human)
            )
        }
    }
}

val defaultGameOptions =
    GameOptions(
        singlePlayer = true,
        firstPlayer = FirstPLayer.Circle,
        difficultyLevel = DifficultyLevel.EASY,
        cross = Player.Cross(Participant.Computer),
        circle = Player.Circle(Participant.Human)
    )
