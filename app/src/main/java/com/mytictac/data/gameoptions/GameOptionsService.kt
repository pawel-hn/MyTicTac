package com.mytictac.data.gameoptions

import com.mytictac.data.DifficultyLevel
import com.mytictac.data.Participant
import com.mytictac.data.Player
import com.mytictac.start.FirstPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface GameOptionsService {

    val gameOptions: StateFlow<GameOptions>

    fun onPlayerCountChanged(singlePlayer: Boolean)
    fun onDifficultyChanged(difficultyLevel: DifficultyLevel)
    fun onFirstPlayerChanged(firstPlayer: FirstPlayer)
}

const val PLAYER_CROSS = "cross"
const val PLAYER_CIRCLE = "circle"

class AndroidGameOptionsService : GameOptionsService {

    private val _gameOptions = MutableStateFlow(defaultGameOptions)
    override val gameOptions: StateFlow<GameOptions> = _gameOptions.asStateFlow()

    override fun onPlayerCountChanged(singlePlayer: Boolean) {
        _gameOptions.update { options ->
            val players = getPlayers(options.firstPlayer, singlePlayer)
            options.copy(
                singlePlayer = singlePlayer,
                cross = players[PLAYER_CROSS] as Player.Cross,
                circle = players[PLAYER_CIRCLE] as Player.Circle
            )
        }
    }

    override fun onDifficultyChanged(difficultyLevel: DifficultyLevel) {
        _gameOptions.update { it.copy(difficultyLevel = difficultyLevel) }
    }

    override fun onFirstPlayerChanged(firstPlayer: FirstPlayer) {
        _gameOptions.update { options ->
            val players = getPlayers(firstPlayer, options.singlePlayer)
            options.copy(
                firstPlayer = firstPlayer,
                cross = players[PLAYER_CROSS] as Player.Cross,
                circle = players[PLAYER_CIRCLE] as Player.Circle
            )
        }
    }

    private fun getPlayers(first: FirstPlayer, singlePlayer: Boolean): Map<String, Player> {
        return when (first) {
            FirstPlayer.Cross -> {
                val cross = Player.Cross(Participant.Human)
                val circle =
                    Player.Circle(if (singlePlayer) Participant.Computer else Participant.Human)
                mapOf(PLAYER_CROSS to cross, PLAYER_CIRCLE to circle)
            }

            FirstPlayer.Circle -> {
                val circle = Player.Circle(Participant.Human)
                val cross =
                    Player.Cross(if (singlePlayer) Participant.Computer else Participant.Human)
                mapOf(PLAYER_CROSS to cross, PLAYER_CIRCLE to circle)
            }
        }
    }
}

val defaultGameOptions = GameOptions(
    singlePlayer = false,
    firstPlayer = FirstPlayer.Circle,
    difficultyLevel = DifficultyLevel.EASY,
    cross = Player.Cross(Participant.Human),
    circle = Player.Circle(Participant.Computer),
)