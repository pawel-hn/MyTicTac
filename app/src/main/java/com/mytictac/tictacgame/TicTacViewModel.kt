package com.mytictac.tictacgame

import androidx.lifecycle.ViewModel
import com.mytictac.data.Player
import com.mytictac.data.gameoptions.GameOptionsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class TicTacViewModel @Inject constructor(
    gameOptionsService: GameOptionsService
) : ViewModel() {
    private val tappedFields = mutableSetOf<Int>()

    private val _state = MutableStateFlow(startState)
    val state: StateFlow<GameState> = _state.asStateFlow()

    private var gameRunning: Boolean = true

    fun tapped(id: Int) {
        if (!tappedFields.contains(id) && gameRunning) {
            tappedFields.add(id)

            _state.update { gameState ->
                val tapsX = gameState.playerX.toMutableList()
                val tapsO = gameState.playerO.toMutableList()
                val field = Fields.entries.first { it.id == id }
                val currentPLayer: Player

                when (gameState.currentPLayer) {
                    Player.PlayerX -> {
                        tapsX.add(field)
                        checkIfWin(tapsX)?.let {
                            return@update GameState(
                                playerO = tapsO,
                                playerX = tapsX,
                                currentPLayer = Player.PlayerO,
                                winner = Player.PlayerX,
                                winningSet = it,
                                shouldResetAnimations = false
                            )
                        }
                        currentPLayer = Player.PlayerO
                    }

                    Player.PlayerO -> {
                        tapsO.add(field)
                        checkIfWin(tapsO)?.let {
                            return@update GameState(
                                playerO = tapsO,
                                playerX = tapsX,
                                currentPLayer = Player.PlayerX,
                                winner = Player.PlayerO,
                                winningSet = it,
                                shouldResetAnimations = false
                            )
                        }
                        currentPLayer = Player.PlayerX
                    }
                }
                GameState(
                    playerO = tapsO,
                    playerX = tapsX,
                    currentPLayer = currentPLayer,
                    winner = null,
                    shouldResetAnimations = false
                )
            }
        }
    }

    fun reset() {
        _state.update {
            it.copy(shouldResetAnimations = true)
        }
    }

    fun setDefault() {
        _state.update {
            gameRunning = true
            tappedFields.removeAll { true }
            startState
        }
    }

    private fun checkIfWin(fields: List<Fields>): List<Fields>? {
        if (fields.size < 3) return null

        val victory =  victories.find { fields.containsAll(it) }
        gameRunning = victory == null

        return victory
    }
}

data class GameState(
    val currentPLayer: Player,
    val playerX: List<Fields>,
    val playerO: List<Fields>,
    val winner: Player?,
    val winningSet: List<Fields>? = null,
    val shouldResetAnimations: Boolean,
)

val startState = GameState(
    currentPLayer = Player.PlayerO,
    playerX = emptyList(),
    playerO = emptyList(),
    winner = null,
    shouldResetAnimations = false,
)

enum class Fields(val id: Int) {
    One(11),
    Two(12),
    Three(13),
    Four(21),
    Five(22),
    Six(23),
    Seven(31),
    Eight(32),
    Nine(33)
}

val victories = listOf(
    listOf(Fields.One, Fields.Two, Fields.Three),
    listOf(Fields.Four, Fields.Five, Fields.Six),
    listOf(Fields.Seven, Fields.Eight, Fields.Nine),

    listOf(Fields.One, Fields.Four, Fields.Seven),
    listOf(Fields.Two, Fields.Five, Fields.Eight),
    listOf(Fields.Three, Fields.Six, Fields.Nine),

    listOf(Fields.One, Fields.Five, Fields.Nine),
    listOf(Fields.Three, Fields.Five, Fields.Seven)
)
