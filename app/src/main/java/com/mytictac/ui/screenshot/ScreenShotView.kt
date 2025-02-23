package com.mytictac.ui.screenshot

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext

@Composable
fun ScreenShotView(
    modifier: Modifier,
    screenShotViewController: ScreenShotViewController,
    contentToScreenShot: @Composable () -> Unit
) {
    val graphicsLayer = rememberGraphicsLayer()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        screenShotViewController.events.collect {
            when (it) {
                is ScreenShotViewUIEvents.GetCurrentGraphicsLayerAndContext -> {
                    it.onCurrentGraphicsLayerAndContext(graphicsLayer, context)
                }
            }
        }
    }

    Box(
        modifier =
        modifier
            .drawWithContent {
                graphicsLayer.record {
                    this@drawWithContent.drawContent()
                }
                drawLayer(graphicsLayer)
            }
    ) {
        contentToScreenShot()
    }
}
