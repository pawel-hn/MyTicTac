package com.mytictac.data

import com.mytictac.R

enum class Player(val label: Int, short: Char) {
    PlayerX(R.string.first_player_x, 'X'),
    PlayerO(R.string.first_player_o, 'O')
}
