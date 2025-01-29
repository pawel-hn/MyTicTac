package com.mytictac

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun SplashScreen() {
    var state by remember { mutableStateOf(true) }
    val transition = updateTransition(targetState = state, label = null)
    val animateAlpha by transition.animateFloat(
        label = "",
        transitionSpec = { tween(1000) }
    ) {
        when(it) {
            false -> 0F
            true -> 1F
        }
    }
    val animateSize by transition.animateFloat(
        label = "",
        transitionSpec = { tween(1000) }
    ) {
        when(it) {
            false -> 3F
            true -> 1F
        }
    }

    LaunchedEffect(Unit) {
        state = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        val fontSize = 30.sp * animateSize
        Column(
            modifier = Modifier.alpha(animateAlpha)
        ) {
            Text(text = "T I C", fontSize = fontSize)
            Text(text = "T A C", fontSize = fontSize)
            Text(text = "T O E", fontSize = fontSize)
        }
    }
}