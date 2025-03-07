package com.mytictac.gameengine

import com.mytictac.data.CurrentGame
import com.mytictac.data.DifficultyLevel
import com.mytictac.data.Field
import com.mytictac.data.FirstPLayer
import com.mytictac.data.GameEndResult
import com.mytictac.data.Participant
import com.mytictac.data.Player
import com.mytictac.data.PlayerState
import com.mytictac.data.SaveGame
import com.mytictac.data.center
import com.mytictac.data.corners
import com.mytictac.data.edges
import com.mytictac.data.gameoptions.GameOptionsService
import com.mytictac.data.savegame.LoadGameUseCase
import com.mytictac.data.savegame.SaveGameUseCase
import com.mytictac.data.victories
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface GameEngine {
    val state: StateFlow<CurrentGame>
    val gameEvent: Flow<GameEvent>

    fun onFieldSelected(id: Int, computerMove: Boolean)

    fun setDefault()

    suspend fun saveGame(): Result<Unit>

    suspend fun loadGame()
}

class AndroidGameEngine(
    gameOptionsService: GameOptionsService,
    private val saveGameUseCase: SaveGameUseCase,
    private val loadGameUseCase: LoadGameUseCase,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : GameEngine {
    private var options = gameOptionsService.gameOptions.value

    private val isGameRunning = MutableStateFlow(true)
    private val currentGame = MutableStateFlow(lazy { setStartGame() }.value)

    override val state: StateFlow<CurrentGame> =
        combine(
            currentGame,
            isGameRunning
        ) { game, isRunning ->
            game.copy(isGameRunning = isRunning)
        }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(500), currentGame.value)

    private val _gameEvent = MutableSharedFlow<GameEvent>()
    override val gameEvent: SharedFlow<GameEvent> = _gameEvent.asSharedFlow()

    private val initiateComputerMove = Channel<Unit>()
    private val tappedIds = mutableSetOf<Int>()
    private var isComputingMove = false

    init {
        if (options.singlePlayer) {
            observeComputerMove()
        }
    }

    private fun observeComputerMove() {
        coroutineScope.launch {
            initiateComputerMove.receiveAsFlow().collect {
                if (getAvailableFields().isNotEmpty()) {
                    val field = chooseComputerMove()
                    if (canMakeMove(field.id, computerMove = true)) {
                        delay(1000)
                        _gameEvent.emit(GameEvent.ComputerMove(field.id))
                        onFieldSelected(field.id, computerMove = true)
                    }
                }
                isComputingMove = false
            }
        }
    }

    override fun onFieldSelected(id: Int, computerMove: Boolean) {
        if (
            canMakeMove(id, computerMove)
        ) {
            tappedIds.add(id)
            currentGame.update { gameState ->
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
        if (!isComputingMove) {
            tappedIds.removeAll { true }
            currentGame.value = setStartGame()
            isGameRunning.value = true
            coroutineScope.launch {
                _gameEvent.emit(GameEvent.ComputerMove(-1))
            }
        }
    }

    override suspend fun saveGame(): Result<Unit> {
        return saveGameUseCase.invoke(
            SaveGame(
                currentGame = currentGame.value,
                options = options
            )
        )
    }

    override suspend fun loadGame() {
        loadGameUseCase.invoke().onSuccess {
            options = it.options
            val loadedMoves = it.currentGame.cross.moves + it.currentGame.circle.moves
            tappedIds.addAll(loadedMoves.map { field -> field.id })
            _gameEvent.emit(GameEvent.GameLoaded(loadedMoves))
            currentGame.value = it.currentGame
        }
    }

    private fun makeMove(currentGame: CurrentGame, id: Int): CurrentGame {
        val tapsX = currentGame.cross.moves.toMutableSet()
        val tapsO = currentGame.circle.moves.toMutableSet()
        val field = Field.entries.first { it.id == id }

        when (val current = currentGame.currentPLayer) {
            is Player.Cross -> {
                tapsX.add(field)
                checkIfGameEnd(current, tapsX)?.let { result ->
                    sendEndGame(result, getWinningFields(tapsX))
                    return currentGame.copy(
                        cross = currentGame.cross.copy(moves = tapsX)
                    )
                }
            }

            is Player.Circle -> {
                tapsO.add(field)
                checkIfGameEnd(current, tapsO)?.let { result ->
                    sendEndGame(result, getWinningFields(tapsO))
                    return currentGame.copy(
                        circle = currentGame.circle.copy(moves = tapsO)
                    )
                }
            }
        }

        val state =
            currentGame.copy(
                cross = currentGame.cross.copy(moves = tapsX),
                circle = currentGame.circle.copy(moves = tapsO),
                currentPLayer =
                setCurrentPlayer(
                    currentGame.currentPLayer,
                    options.singlePlayer
                )
            )
        return state
    }

    private fun chooseComputerMove(): Field {
        val gameState = currentGame.value

        val availableFields = getAvailableFields()

        val (computer, human) =
            if (gameState.cross.player.participant == Participant.Computer) {
                gameState.cross to gameState.circle
            } else {
                gameState.circle to gameState.cross
            }

        when (val lvl = options.difficultyLevel) {
            DifficultyLevel.EASY -> return availableFields.random()
            else -> {
                // winning move
                findWinningMove(computer.moves, availableFields)?.let { return it }

                // blocking move
                findWinningMove(human.moves, availableFields)?.let { return it }

                if (lvl == DifficultyLevel.IMPOSSIBLE) {
                    if (human.moves.size == 2 &&
                        human.moves.first() in edges &&
                        human.moves.last() in corners
                    ) {
                        val oppositeCorner = findOppositeCorner(human.moves.last())
                        oppositeCorner?.let {
                            if (oppositeCorner in availableFields) return oppositeCorner
                        }
                    }

                    if (human.moves.size == 2 && human.moves.any { it in corners }) {
                        val safeMove = edges.firstOrNull { it in availableFields }
                        if (safeMove != null) return safeMove
                    }
                }

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

    private fun findOppositeCorner(corner: Field): Field? {
        return when (corner) {
            Field.One -> Field.Nine
            Field.Three -> Field.Seven
            Field.Seven -> Field.Three
            Field.Nine -> Field.One
            else -> null
        }
    }

    private fun canMakeMove(id: Int, computerMove: Boolean) = !tappedIds.contains(id) &&
        isGameRunning.value &&
        (!isComputingMove || computerMove)

    private fun setStartGame() = CurrentGame(
        currentPLayer =
        when (options.firstPlayer) {
            FirstPLayer.Cross -> options.cross
            FirstPLayer.Circle -> options.circle
        },
        cross = PlayerState(options.cross, emptySet()),
        circle = PlayerState(options.circle, emptySet()),
        isGameRunning = true
    )

    private fun setCurrentPlayer(player: Player, isSinglePlayer: Boolean): Player {
        val nextParticipant =
            if (isSinglePlayer) {
                if (player.participant == Participant.Computer) {
                    Participant.Human
                } else {
                    Participant.Computer
                }
            } else {
                Participant.Human
            }
        return when (player) {
            is Player.Cross -> Player.Circle(nextParticipant)
            is Player.Circle -> Player.Cross(nextParticipant)
        }
    }

    private fun getAvailableFields(): Set<Field> {
        val currentGame = currentGame.value
        return Field.entries.toSet() - currentGame.cross.moves - currentGame.circle.moves
    }

    private fun checkIfGameEnd(
        currentPLayer: Player,
        currentPLayerMoves: Set<Field>
    ): GameEndResult? {
        val result =
            if (getWinningFields(currentPLayerMoves).isNotEmpty()) {
                when (currentPLayer) {
                    is Player.Cross -> GameEndResult.Cross
                    is Player.Circle -> GameEndResult.Circle
                }
            } else {
                checkIfDraw()
            }
        isGameRunning.value = result == null
        return result
    }

    private fun getWinningFields(fields: Set<Field>): Set<Field> {
        if (fields.size < 3) return emptySet()

        val victory = victories.find { fields.containsAll(it) }

        return victory ?: emptySet()
    }

    private fun checkIfDraw(): GameEndResult? {
        return if (tappedIds.size == Field.entries.size) {
            GameEndResult.Draw
        } else {
            null
        }
    }

    private fun sendEndGame(result: GameEndResult, winningSet: Set<Field>) {
        coroutineScope.launch {
            if (isComputingMove) {
                delay(500)
            }
            _gameEvent.emit(GameEvent.GameEnd(result, winningSet))
        }
    }
}

sealed class GameEvent {
    data class GameEnd(
        val result: GameEndResult,
        val winningSet: Set<Field>
    ) : GameEvent()

    data class ComputerMove(val fieldId: Int) : GameEvent()

    data class GameLoaded(val fields: Set<Field>) : GameEvent()
}
