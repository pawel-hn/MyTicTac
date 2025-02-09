package com.mytictac.tictacgame.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mytictac.data.Player
import com.mytictac.tictacgame.GameState
import com.mytictac.ui.theme.MyTicTacTheme

@Composable
fun GameCurrentPlayerHeader(
    modifier: Modifier,
    state: GameState.CurrentGame){
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.padding(10.dp),
            text = "Current player:",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MyTicTacTheme.colours.contentPrimary,
            textAlign = TextAlign.Center
        )
        AnimatedContent(
            targetState = state.currentPLayer,
            label = "current player",
            transitionSpec = {
                (slideInVertically(tween(200)) { height ->
                    height
                } togetherWith
                        slideOutVertically(tween(200)) { height ->
                            -height
                        }).using(
                    SizeTransform(true)
                )
            }
        ) { player ->
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .drawBehind {
                        when (player) {
                            is Player.Cross -> {
                                drawCross(
                                    fieldXY = FieldXY(
                                        topLeft = Offset.Zero,
                                        bottomRight = Offset(
                                            40.dp.toPx(),
                                            40.dp.toPx()
                                        ),
                                        id = 0
                                    ),
                                    width = 15F,
                                    endOffset = 10F
                                )
                            }

                            is Player.Circle -> {
                                drawTicCircle(
                                    fieldXY = FieldXY(
                                        topLeft = Offset.Zero,
                                        bottomRight = Offset(
                                            40.dp.toPx(),
                                            40.dp.toPx()
                                        ),
                                        id = 0
                                    ),
                                    width = 15F,
                                    endOffset = 10F
                                )
                            }
                        }
                    }
            )
        }
    }
}