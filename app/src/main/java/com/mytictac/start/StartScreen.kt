package com.mytictac.start

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mytictac.ui.components.OptionButton
import com.mytictac.ui.components.StartButton
import com.mytictac.ui.theme.Padding
import com.mytictac.ui.theme.buttonEnabledPrimary
import com.mytictac.ui.theme.buttonEnabledSecondary


@Composable
fun StartScreen() {
    var playerCount by remember { mutableStateOf(2) }
    var difficulty by remember { mutableStateOf("Łatwy") }
    val difficultyLevels = listOf("Łatwy", "Niemożliwy")


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
                        text = if (it ==1) "1 gracz" else "2 graczy",
                        width = width * 0.4F,
                        isSelected = playerCount == it,
                        onClick = { playerCount = it },
                        enabledPrimaryColor = buttonEnabledPrimary,
                        enabledSecondaryColor = buttonEnabledSecondary,
                    )
                }
            }

            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth().padding(Padding.large),
                visible = playerCount == 1,
                enter = fadeIn(animationSpec = tween(700)),
                exit = fadeOut(animationSpec = tween(700))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Padding.small)
                ) {
                    Text(text = "Poziom trudności:")
                    difficultyLevels.forEach { level ->
                        OptionButton(
                            text = level,
                            width = width / 3F,
                            textSize = 12.sp,
                            height = 40.dp,
                            isSelected = difficulty == level,
                            onClick = { difficulty = level },
                            enabledPrimaryColor = Color(0xFF607D8B),
                            enabledSecondaryColor = Color(0xFFB0BEC5),
                        )
                    }

                }

            }
        }
        OptionButton(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = Padding.extraLarge),
            width = width / 2F,
            text = "Start",
            onClick = { /*TODO*/ },
            isSelected = true,
            enabledPrimaryColor = Color(0xFF1E88E5),
            enabledSecondaryColor = Color(0xFFB3E5FC),
        )
    }
}


@Preview
@Composable
fun MainScreenPreview() {
    MaterialTheme {
        StartScreen()
    }
}