package com.mytictac.start

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mytictac.data.DifficultyLevel
import com.mytictac.data.Player
import com.mytictac.ui.components.OptionButton
import com.mytictac.ui.theme.MyTicTacTheme
import com.mytictac.ui.theme.Padding


@Composable
fun StartScreen() {
    var playerCount by remember { mutableIntStateOf(1) }
    var difficulty by remember { mutableStateOf(DifficultyLevel.EASY) }
    var firstPlayer by remember { mutableStateOf(Player.PlayerO) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(Padding.medium),
    ) {
        val height = maxHeight
        val width = maxWidth

        Text(
            modifier = Modifier.align(Alignment.TopCenter),
            text = "T I C T A C T O E", fontSize = 24.sp
        )

        Column(
            modifier = Modifier.offset { IntOffset(x = 0, y = (height.toPx() / 4).toInt()) },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Liczba graczy:")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Padding.large),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(2) {
                    OptionButton(
                        modifier = Modifier.weight(0.5F),
                        text = if (it == 0) "1 gracz" else "2 graczy",
                        width = width * 0.4F,
                        isSelected = playerCount == it + 1,
                        onClick = { playerCount = it + 1 },
                        enabledPrimaryColor = MyTicTacTheme.colours.interactiveSecondary,
                        enabledSecondaryColor = MyTicTacTheme.colours.interactiveSecondaryContent,
                    )
                }
            }

            StartOptions(
                modifier = Modifier.fillMaxWidth(),
                maxWidth = width,
                difficultyLevels = difficulty,
                players = playerCount,
                firstPlayer = firstPlayer,
                onDifficultyChanged = { difficulty = it },
                onPlayerChanged = { firstPlayer = it }
            )
        }

        OptionButton(
            modifier = Modifier.align(Alignment.BottomCenter)
                .padding(bottom = Padding.extraExtraLarge),
            width = width / 2F,
            text = "Start",
            onClick = { /*TODO*/ },
            isSelected = true,
            enabledPrimaryColor = MyTicTacTheme.colours.interactivePrimary,
            enabledSecondaryColor = MyTicTacTheme.colours.interactivePrimaryContent,
        )
    }
}

@Composable
fun StartOptions(
    modifier: Modifier = Modifier,
    maxWidth: Dp,
    difficultyLevels: DifficultyLevel,
    players: Int,
    firstPlayer: Player,
    onDifficultyChanged: (DifficultyLevel) -> Unit,
    onPlayerChanged: (Player) -> Unit,
) {
    AnimatedContent(
        modifier = modifier,
        targetState = players,
        transitionSpec = {
            if (targetState < initialState) {
                slideInHorizontally { width -> width } togetherWith
                        slideOutHorizontally { width -> -width }
            } else {
                slideInHorizontally { width -> -width } togetherWith
                        slideOutHorizontally { width -> width }
            }.using(
                SizeTransform(clip = false)
            )
        },
        label = ""
    ) {
        if (it == 1) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Padding.small)
            ) {
                Text(text = "Poziom trudności:")
                DifficultyLevel.entries.forEach { level ->
                    OptionButton(
                        text = stringResource(level.value),
                        width = maxWidth / 3F,
                        textSize = 12.sp,
                        height = 40.dp,
                        isSelected = difficultyLevels == level,
                        onClick = { onDifficultyChanged(level) },
                        enabledPrimaryColor = MyTicTacTheme.colours.interactiveTertiary,
                        enabledSecondaryColor =
                        MyTicTacTheme.colours.interactiveTertiaryContent,
                    )
                }
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Padding.small)
            ) {
                Text(text = "Kto zaczyna:")
                Player.entries.forEach { player ->
                    OptionButton(
                        text = stringResource(player.label),
                        width = maxWidth / 3F,
                        textSize = 12.sp,
                        height = 40.dp,
                        isSelected = player == firstPlayer,
                        onClick = { onPlayerChanged(player) },
                        enabledPrimaryColor = MyTicTacTheme.colours.interactiveTertiary,
                        enabledSecondaryColor =
                        MyTicTacTheme.colours.interactiveTertiaryContent,
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun MainScreenPreview() {
    MaterialTheme {
        StartScreen()
    }
}
