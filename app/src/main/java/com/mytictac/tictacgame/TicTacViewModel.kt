package com.mytictac.tictacgame

import androidx.lifecycle.ViewModel
import com.mytictac.data.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class TicTacViewModel @Inject constructor() : ViewModel() {
    private val tappedFields = mutableSetOf<Int>()

    private val _state = MutableStateFlow(startState)
    val state: StateFlow<GameState> = _state.asStateFlow()


    fun tapped(id: Int) {
        if (!tappedFields.contains(id)) {
            tappedFields.add(id)

            _state.update { gameState ->
                val tapsX = gameState.playerX.toMutableList()
                val tapsO = gameState.playerO.toMutableList()
                val field = Field.entries.first { it.id == id }
                val currentPLayer: Player

                when (gameState.currentPLayer) {
                    Player.PlayerX -> {
                        tapsX.add(field)
                        if (checkIfWin(tapsX)) {

                            return@update GameState(
                                playerO = tapsO,
                                playerX = tapsX,
                                currentPLayer = Player.PlayerO,
                                winner = Player.PlayerX,
                                shouldResetAnimations = false
                            )
                        }
                        currentPLayer = Player.PlayerO
                    }

                    Player.PlayerO -> {
                        tapsO.add(field)
                        if (checkIfWin(tapsO)) {
                            return@update GameState(
                                playerO = tapsO,
                                playerX = tapsX,
                                currentPLayer = Player.PlayerX,
                                winner = Player.PlayerO,
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
            tappedFields.removeAll { true }
            startState
        }
    }

    private fun checkIfWin(fields: List<Field>): Boolean {
        if (fields.size < 3) return false

        return victories.any { fields.containsAll(it) }
    }
}

data class GameState(
    val currentPLayer: Player,
    val playerX: List<Field>,
    val playerO: List<Field>,
    val winner: Player?,
    val shouldResetAnimations: Boolean
)

val startState = GameState(
    currentPLayer = Player.PlayerO,
    playerX = emptyList(),
    playerO = emptyList(),
    winner = null,
    shouldResetAnimations = false,
)

enum class Field(val id: Int) {
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

val victories = setOf(
    // Wiersze
    setOf(Field.One, Field.Two, Field.Three),
    setOf(Field.Four, Field.Five, Field.Six),
    setOf(Field.Seven, Field.Eight, Field.Nine),

    // Kolumny
    setOf(Field.One, Field.Four, Field.Seven),
    setOf(Field.Two, Field.Five, Field.Eight),
    setOf(Field.Three, Field.Six, Field.Nine),

    // Przekątne
    setOf(Field.One, Field.Five, Field.Nine),
    setOf(Field.Three, Field.Five, Field.Seven)
)
