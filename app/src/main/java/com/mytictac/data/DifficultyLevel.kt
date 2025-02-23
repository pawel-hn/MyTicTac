package com.mytictac.data

import androidx.annotation.StringRes
import com.mytictac.R

enum class DifficultyLevel(
    @StringRes val value: Int
) {
    EASY(R.string.difficulty_level_easy),
    NORMAL(R.string.difficulty_level_normal),
    IMPOSSIBLE(R.string.difficulty_level_impossible)
}
