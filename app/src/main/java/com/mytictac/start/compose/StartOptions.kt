package com.mytictac.start.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mytictac.data.DifficultyLevel
import com.mytictac.start.StartScreenFirstPlayerUI
import com.mytictac.ui.components.TicTacButton
import com.mytictac.ui.theme.MyTicTacTheme
import com.mytictac.ui.theme.Padding

@Composable
fun StartOptions(
    modifier: Modifier = Modifier,
    maxWidth: Dp,
    difficultyLevel: DifficultyLevel,
    singlePlayer: Boolean,
    startScreenFirstPlayerUI: StartScreenFirstPlayerUI,
    onDifficultyChanged: (DifficultyLevel) -> Unit,
    onPlayerChanged: (StartScreenFirstPlayerUI) -> Unit
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
                        MyTicTacTheme.colours.interactiveTertiaryContent
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
                        MyTicTacTheme.colours.interactiveTertiaryContent
                    )
                }
            }
        }
    }
}
