package com.mytictac.data

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

val victories = listOf(
    listOf(Field.One, Field.Two, Field.Three),
    listOf(Field.Four, Field.Five, Field.Six),
    listOf(Field.Seven, Field.Eight, Field.Nine),

    listOf(Field.One, Field.Four, Field.Seven),
    listOf(Field.Two, Field.Five, Field.Eight),
    listOf(Field.Three, Field.Six, Field.Nine),

    listOf(Field.One, Field.Five, Field.Nine),
    listOf(Field.Three, Field.Five, Field.Seven)
)