package com.example.mytictac.tictacgame

import android.util.Log
import androidx.lifecycle.ViewModel
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
                val currentPLayer: Players

                when (gameState.currentPLayer) {
                    Players.PlayerX -> {
                        tapsX.add(field)
                        if (checkIfWin(tapsX)) {

                            return@update GameState(
                                playerO = tapsO,
                                playerX = tapsX,
                                currentPLayer = Players.PlayerO,
                                winner = Players.PlayerX,
                                shouldResetAnimations = false
                            )
                        }
                        currentPLayer = Players.PlayerO
                    }

                    Players.PlayerO -> {
                        tapsO.add(field)
                        if (checkIfWin(tapsO)) {
                            return@update GameState(
                                playerO = tapsO,
                                playerX = tapsX,
                                currentPLayer = Players.PlayerX,
                                winner = Players.PlayerO,
                                shouldResetAnimations = false
                            )

                        }
                        currentPLayer = Players.PlayerX
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
        Log.d("PHN", "setDefault")
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

data class ResetAnimations(val reset: Boolean)

data class GameState(
    val currentPLayer: Players,
    val playerX: List<Field>,
    val playerO: List<Field>,
    val winner: Players?,
    val shouldResetAnimations: Boolean
)

val startState = GameState(
    currentPLayer = Players.PlayerO,
    playerX = emptyList(),
    playerO = emptyList(),
    winner = null,
    shouldResetAnimations = false,
)

enum class Players {
    PlayerX,
    PlayerO
}

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