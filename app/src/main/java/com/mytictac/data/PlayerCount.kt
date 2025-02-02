package com.mytictac.data


enum class PlayerCount(val value: Int)  {
    ONE(1),
    TWO(2);

    companion object {
        fun fromInt(value: Int): PlayerCount = entries.find { it.value == value } ?: ONE
    }
}
