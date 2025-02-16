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

val corners = setOf(Field.One, Field.Three, Field.Seven, Field.Nine)

val center = Field.Five

val victories = setOf(
    setOf(Field.One, Field.Two, Field.Three),
    setOf(Field.Four, Field.Five, Field.Six),
    setOf(Field.Seven, Field.Eight, Field.Nine),

    setOf(Field.One, Field.Four, Field.Seven),
    setOf(Field.Two, Field.Five, Field.Eight),
    setOf(Field.Three, Field.Six, Field.Nine),

    setOf(Field.One, Field.Five, Field.Nine),
    setOf(Field.Three, Field.Five, Field.Seven)
)