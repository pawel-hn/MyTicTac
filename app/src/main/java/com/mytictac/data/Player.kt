package com.mytictac.data

sealed interface Player {
    val participant: Participant

    data class Cross(override val participant: Participant) : Player
    data class Circle(override val participant: Participant) : Player
}

enum class Participant {
    Human,Computer
}

enum class FirstPLayer {
    Cross,Circle
}