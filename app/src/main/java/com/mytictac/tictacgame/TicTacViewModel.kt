package com.mytictac.tictacgame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytictac.data.Field
import com.mytictac.data.Participant
import com.mytictac.data.Player
import com.mytictac.data.gameoptions.GameOptionsService
import com.mytictac.data.victories
import com.mytictac.start.FirstPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TicTacViewModel @Inject constructor(
    gameOptionsService: GameOptionsService
) : ViewModel() {
    private val tappedFields = mutableSetOf<Int>()

    private val currentGame = MutableStateFlow(startGame)
    val state: StateFlow<GameState> = currentGame.asStateFlow()

    private var gameRunning: Boolean = true
    private val options = gameOptionsService.gameOptions.value

    private var canComputerMove = options.singlePlayer
    private var blockTapping = false

    init {
        viewModelScope.launch {
            gameOptionsService.gameOptions.collect { options ->
                currentGame.update {
                    it.copy(
                        currentPLayer = when (options.firstPlayer) {
                            FirstPlayer.Cross -> it.cross.player
                            FirstPlayer.Circle -> it.circle.player
                        },
                        cross = PlayerState(options.cross, emptyList()),
                        circle = PlayerState(options.circle, emptyList())
                    )
                }
            }
        }
    }

    fun makeMove(id: Int, computerMove: Boolean) {

        if (!tappedFields.contains(id) && gameRunning) {
            tappedFields.add(id)

            currentGame.update { gameState ->
                val tapsX = gameState.cross.moves.toMutableList()
                val tapsO = gameState.circle.moves.toMutableList()
                val field = Field.entries.first { it.id == id }
                val currentPLayer: Player

                when (gameState.currentPLayer) {
                    is Player.Cross -> {
                        tapsX.add(field)
                        getWinningFields(tapsX)?.let {
                            return@update gameState.copy(
                                cross = gameState.cross.copy(moves = tapsX),
                                winner = Player.Cross(gameState.currentPLayer.participant),
                                winningSet = it,
                            )
                        }
                        currentPLayer = setCurrentPlayer(
                            gameState.currentPLayer, options.singlePlayer
                        )
                    }

                    is Player.Circle -> {
                        tapsO.add(field)
                        getWinningFields(tapsO)?.let {
                            return@update gameState.copy(
                                circle = gameState.circle.copy(moves = tapsO),
                                winner = Player.Circle(gameState.currentPLayer.participant),
                                winningSet = it,
                            )
                        }
                        currentPLayer = setCurrentPlayer(
                            gameState.currentPLayer, options.singlePlayer
                        )
                    }
                }

                if (options.singlePlayer) {
                    canComputerMove = true
                }
                gameState.copy(
                    cross = gameState.cross.copy(moves = tapsX),
                    circle = gameState.circle.copy(moves = tapsO),
                    currentPLayer = currentPLayer,
                )
            }
        }
    }

//    fun chooseComputerMove(): Field {
//        val gameState = currentGame.value
//        val availableFields = Field.entries - gameState.cross.moves - gameState.circle.moves
//
//        when (options.difficultyLevel) {
//            DifficultyLevel.EASY -> return availableFields.random()
//
//
//            DifficultyLevel.IMPOSSIBLE -> {
//
//            }
//        }
//
//        // 1. Sprawdź, czy komputer może wygrać
//        val winningMove = findWinningMove(gameState., availableFields)
//        if (winningMove != null) return winningMove
//
//        // 2. Sprawdź, czy gracz może wygrać, aby zablokować
//        val blockingMove = findWinningMove(playerMoves, availableFields)
//        if (blockingMove != null) return blockingMove
//
//        // 3. Wybierz ruch strategiczny (np. centrum lub losowe dostępne pole)
//        val center = Field.Five
//        if (center in allMoves) return center
//
//        // 4. Wybierz losowe dostępne pole
//        return allMoves.randomOrNull()
//    }

    private fun findWinningMove(currentMoves: List<Field>, allMoves: List<Field>): Field? {
        for (victory in victories) {
            val missingFields = victory - currentMoves
            if (missingFields.size == 1 && missingFields.first() in allMoves) {
                return missingFields.first() // Zwraca brakujące pole do wygranej
            }
        }
        return null
    }

    fun reset() {
        currentGame.update {
            it.copy(reset = true)
        }
    }

    fun setDefault() {
        currentGame.update {
            gameRunning = true
            tappedFields.removeAll { true }
            startGame.copy(
                currentPLayer = when (options.firstPlayer) {
                    FirstPlayer.Circle -> it.cross.player
                    FirstPlayer.Cross -> it.circle.player
                }
            )
        }
    }

    private fun setCurrentPlayer(
        player: Player,
        isSinglePlayer: Boolean,
    ): Player {
        val nextParticipant = if (isSinglePlayer) {
            if (player.participant == Participant.Computer) Participant.Human else Participant.Computer
        } else {
            Participant.Human
        }
        return when (player) {
            is Player.Cross -> Player.Circle(nextParticipant)
            is Player.Circle -> Player.Cross(nextParticipant)
        }
    }

    private fun getWinningFields(fields: List<Field>): List<Field>? {
        if (fields.size < 3) return null

        val victory = victories.find { fields.containsAll(it) }
        gameRunning = victory == null

        return victory
    }
}

data class GameState(
    val currentPLayer: Player,
    val cross: PlayerState,
    val circle: PlayerState,
    val winner: Player?,
    val winningSet: List<Field>,
    val reset: Boolean,
)

data class PlayerState(
    val player: Player,
    val moves: List<Field>
)

val startGame = GameState(
    currentPLayer = Player.Cross(Participant.Human),
    winner = null,
    reset = false,
    winningSet = emptyList(),
    cross = PlayerState(Player.Cross(Participant.Human), emptyList()),
    circle = PlayerState(Player.Circle(Participant.Computer), emptyList()),
)


