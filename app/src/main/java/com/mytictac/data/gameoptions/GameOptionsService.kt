package com.mytictac.data.gameoptions

import com.mytictac.data.DifficultyLevel
import com.mytictac.data.Player
import com.mytictac.data.PlayerCount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface GameOptionsService {

    val gameOptions: StateFlow<GameOptions>

    fun onPlayerCountChanged(playerCount: PlayerCount)
    fun onDifficultyChanged(difficultyLevel: DifficultyLevel)
    fun onFirstPlayerChanged(player: Player)
}

class AndroidGameOptionsService : GameOptionsService {

    private val _gameOptions = MutableStateFlow(defaultGameOptions)
    override val gameOptions: StateFlow<GameOptions> = _gameOptions.asStateFlow()

    override fun onPlayerCountChanged(playerCount: PlayerCount) {
        _gameOptions.update { options ->
            options.copy(playerCount = playerCount,)
        }
    }

    override fun onDifficultyChanged(difficultyLevel: DifficultyLevel) {
        _gameOptions.update { it.copy(difficultyLevel = difficultyLevel) }
    }

    override fun onFirstPlayerChanged(player: Player) {
        _gameOptions.update { it.copy(firstPlayer = player) }
    }
}

val defaultGameOptions = GameOptions(
    playerCount = PlayerCount.ONE,
    firstPlayer = Player.PlayerO,
    difficultyLevel = DifficultyLevel.EASY
)