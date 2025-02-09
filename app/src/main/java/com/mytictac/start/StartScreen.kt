package com.mytictac.start

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mytictac.data.DifficultyLevel
import com.mytictac.ui.components.TicTacButton
import com.mytictac.ui.theme.MyTicTacTheme
import com.mytictac.ui.theme.Padding

@Composable
fun StartScreen(
    viewModel: StartScreenViewModel,
    router: StartRouter
) {
    LaunchedEffect(Unit) {
        viewModel.startScreenEvent.collect {
            when (it) {
                StartScreenUIEvent.StartGame -> router.onStartGame()
            }
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(Padding.medium),
    ) {
        val height = maxHeight
        val width = maxWidth

        Text(
            modifier = Modifier.align(Alignment.TopCenter),
            text = "T I C T A C T O E", fontSize = 24.sp,
            color = MyTicTacTheme.colours.contentPrimary,
        )

        Column(
            modifier = Modifier.offset { IntOffset(x = 0, y = (height.toPx() / 4).toInt()) },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Liczba graczy:", color = MyTicTacTheme.colours.contentPrimary)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Padding.large),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(2) {
                    TicTacButton(
                        modifier = Modifier.weight(0.5F),
                        text = if (it == 0) "1 gracz" else "2 graczy",
                        width = width * 0.4F,
                        isSelected = state.singlePLayer == (it == 0),
                        onClick = {
                            viewModel.onPlayerCountChanged(it == 0)
                        },
                        enabledPrimaryColor = MyTicTacTheme.colours.interactiveSecondary,
                        enabledSecondaryColor = MyTicTacTheme.colours.interactiveSecondaryContent,
                    )
                }
            }

            StartOptions(
                modifier = Modifier.fillMaxWidth(),
                maxWidth = width,
                difficultyLevel = state.difficultyLevel,
                singlePlayer = state.singlePLayer,
                startScreenFirstPlayerUI = state.startScreenFirstPlayerUI,
                onDifficultyChanged = viewModel::onDifficultyChanged,
                onPlayerChanged = viewModel::onFirstPlayerChanged
            )
        }

        TicTacButton(
            modifier = Modifier.align(Alignment.BottomCenter)
                .padding(bottom = Padding.extraExtraLarge),
            width = width / 2F,
            text = "Start",
            onClick = viewModel::onStartGame,
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
    difficultyLevel: DifficultyLevel,
    singlePlayer: Boolean,
    startScreenFirstPlayerUI: StartScreenFirstPlayerUI,
    onDifficultyChanged: (DifficultyLevel) -> Unit,
    onPlayerChanged: (StartScreenFirstPlayerUI) -> Unit,
) {
    AnimatedContent(
        modifier = modifier,
        targetState = singlePlayer,
        transitionSpec = {
            if (targetState > initialState) {
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
    ) { isSingle ->
        if (isSingle) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Padding.small)
            ) {
                Text(text = "Poziom trudności:")
                DifficultyLevel.entries.forEach { level ->
                    TicTacButton(
                        text = stringResource(level.value),
                        width = maxWidth / 3F,
                        textSize = 12.sp,
                        height = 40.dp,
                        isSelected = difficultyLevel == level,
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

                StartScreenFirstPlayerUI.entries.forEach { player ->
                    TicTacButton(
                        text = stringResource(player.label),
                        width = maxWidth / 3F,
                        textSize = 12.sp,
                        height = 40.dp,
                        isSelected = player == startScreenFirstPlayerUI,
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




