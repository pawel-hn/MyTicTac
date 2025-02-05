package com.mytictac.gameengine

import com.mytictac.data.DifficultyLevel
import com.mytictac.data.Field
import com.mytictac.data.FirstPLayer
import com.mytictac.data.Participant
import com.mytictac.data.Player
import com.mytictac.data.center
import com.mytictac.data.corners
import com.mytictac.data.gameoptions.GameOptionsService
import com.mytictac.data.victories
import com.mytictac.tictacgame.PlayerState
import com.mytictac.tictacgame.Winner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface GameEngine {
    val state: StateFlow<CurrentGame>
    val computerMove: Flow<Int>
    fun selectedField(id: Int, computerMove: Boolean)
    fun setDefault()
}

class AndroidGameEngine(
    gameOptionsService: GameOptionsService,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : GameEngine {

    private val options = gameOptionsService.gameOptions.value

    private val _state = MutableStateFlow(setStartGame())
    override val state: StateFlow<CurrentGame> = _state.asStateFlow()

    private val _computerMove = Channel<Int>()
    override val computerMove = _computerMove.receiveAsFlow()

    private val initiateComputerMove = Channel<Unit>()

    private val tappedIds = mutableSetOf<Int>()
    private var gameRunning: Boolean = true

    private var isComputingMove = false

    init {
        if (options.singlePlayer) {
            coroutineScope.launch {
                initiateComputerMove.receiveAsFlow().collect {
                    if (getAvailableFields().isNotEmpty()) {
                        delay(1000)
                        val field = chooseComputerMove()
                        selectedField(field.id, computerMove = true)
                        _computerMove.send(field.id)
                    }
                    isComputingMove = false
                }
            }
        }
    }

    override fun selectedField(id: Int, computerMove: Boolean) {
        if (!tappedIds.contains(id) && gameRunning && (!isComputingMove || computerMove)) {
            tappedIds.add(id)
            _state.update { gameState ->
                makeMove(gameState, id)
            }

            coroutineScope.launch {
                if (options.singlePlayer && !computerMove) {
                    isComputingMove = true
                    initiateComputerMove.send(Unit)
                }
            }
        }
    }

    override fun setDefault() {
        gameRunning = true
        tappedIds.removeAll { true }
        _state.value = setStartGame()
        coroutineScope.launch {
            _computerMove.send(-1)
        }
    }

    private fun makeMove(currentGame: CurrentGame, id: Int): CurrentGame {
        val tapsX = currentGame.cross.moves.toMutableSet()
        val tapsO = currentGame.circle.moves.toMutableSet()
        val field = Field.entries.first { it.id == id }
        val currentPLayer: Player

        when (val current = currentGame.currentPLayer) {
            is Player.Cross -> {
                tapsX.add(field)
                checkIfGameEnd(current, tapsX)?.let {
                    return currentGame.copy(
                        cross = currentGame.cross.copy(moves = tapsX),
                        winner = it,
                        winningSet = getWinningFields(tapsX) ?: emptySet(),
                    )
                }

                currentPLayer = setCurrentPlayer(
                    currentGame.currentPLayer, options.singlePlayer
                )
            }

            is Player.Circle -> {
                tapsO.add(field)
                checkIfGameEnd(current, tapsO)?.let {
                    return currentGame.copy(
                        circle = currentGame.circle.copy(moves = tapsO),
                        winner = it,
                        winningSet = getWinningFields(tapsO) ?: emptySet(),
                    )
                }
                currentPLayer = setCurrentPlayer(
                    currentGame.currentPLayer, options.singlePlayer
                )
            }
        }

        val state = currentGame.copy(
            cross = currentGame.cross.copy(moves = tapsX),
            circle = currentGame.circle.copy(moves = tapsO),
            currentPLayer = currentPLayer,
        )
        return state
    }

    private fun chooseComputerMove(): Field {
        val gameState = _state.value

        val availableFields = getAvailableFields()

        val computer = when {
            gameState.cross.player.participant == Participant.Computer -> gameState.cross
            else -> gameState.circle
        }
        val human = when (computer) {
            gameState.cross -> gameState.circle
            else -> gameState.cross
        }

        when (options.difficultyLevel) {
            DifficultyLevel.EASY -> return availableFields.random()
            DifficultyLevel.IMPOSSIBLE -> {

                val winningMove = findWinningMove(computer.moves, availableFields)
                if (winningMove != null) return winningMove

                val blockingMove = findWinningMove(human.moves, availableFields)
                if (blockingMove != null) return blockingMove

                if (center in availableFields) return center

                val availableCorners = corners.intersect(availableFields)
                if (availableCorners.isNotEmpty()) return availableCorners.random()

                return availableFields.random()
            }
        }
    }

    private fun findWinningMove(currentMoves: Set<Field>, availableFields: Set<Field>): Field? {
        for (victory in victories) {
            val missingFields = victory - currentMoves
            if (missingFields.size == 1 && missingFields.first() in availableFields) {
                return missingFields.first()
            }
        }
        return null
    }

    private fun setStartGame() = CurrentGame(
            currentPLayer = when (options.firstPlayer) {
                FirstPLayer.Cross -> options.cross
                FirstPLayer.Circle -> options.circle
            },
            winner = null,
            winningSet = emptySet(),
            cross = PlayerState(options.cross, emptySet()),
            circle = PlayerState(options.circle, emptySet()),
        )

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

    private fun getAvailableFields(): Set<Field> {
        val currentGame = _state.value
        return Field.entries.toSet() - currentGame.cross.moves - currentGame.circle.moves
    }

    private fun checkIfGameEnd(
        currentPLayer: Player,
        currentPLayerMoves: Set<Field>
    ): Winner? {
        val result = if (getWinningFields(currentPLayerMoves) != null) {
            when (currentPLayer) {
                is Player.Cross -> Winner.Cross
                is Player.Circle -> Winner.Circle
            }
        } else {
            checkIfDraw()
        }
        gameRunning = result == null
        return result
    }


    private fun getWinningFields(fields: Set<Field>): Set<Field>? {
        if (fields.size < 3) return null

        val victory = victories.find { fields.containsAll(it) }

        return victory
    }

    private fun checkIfDraw(): Winner? {
        return if (tappedIds.size == Field.entries.size) {
            Winner.Draw
        } else {
            null
        }
    }
}

data class CurrentGame(
    val currentPLayer: Player,
    val cross: PlayerState,
    val circle: PlayerState,
    val winner: Winner?,
    val winningSet: Set<Field>,
)