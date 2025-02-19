package com.mytictac.ui.screenshot

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.core.content.ContextCompat.startActivities
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume

enum class TakeAndShareScreenshotResult {
    ScreenShotShareSuccess,
    ScreenShotShareFail,
}

class ScreenShotViewController {

    private val _events = Channel<ScreenShotViewUIEvents>(
        capacity = Channel.UNLIMITED
    )
    internal val events: Flow<ScreenShotViewUIEvents> = _events.receiveAsFlow()

    suspend fun takeAndShareScreenShot(
        screenshotFileName: String
    ): TakeAndShareScreenshotResult {
        val graphicsLayerAndContext = getGraphicsLayerAndContext()
        return takeAndShareScreenshot(
            graphicsLayer = graphicsLayerAndContext.first,
            context = graphicsLayerAndContext.second,
            screenshotFileName = screenshotFileName
        )
    }

    private suspend fun getGraphicsLayerAndContext(): Pair<GraphicsLayer?, Context> {
        val graphicsLayerAndContext =
            suspendCancellableCoroutine<Pair<GraphicsLayer?, Context>> { continuation ->
                _events.trySend(
                    ScreenShotViewUIEvents
                        .GetCurrentGraphicsLayerAndContext { graphicsLayer, context ->
                            continuation.resume(graphicsLayer to context)
                        }
                )
            }
        return graphicsLayerAndContext
    }

    private suspend fun takeAndShareScreenshot(
        graphicsLayer: GraphicsLayer?,
        context: Context,
        screenshotFileName: String
    ): TakeAndShareScreenshotResult {
        return if (graphicsLayer == null) {
            return TakeAndShareScreenshotResult.ScreenShotShareFail
        } else {
            try {
                val bitmap = graphicsLayer.toImageBitmap()
                val uri = bitmap.asAndroidBitmap().saveToCacheAndGetUri(
                    context,
                    screenshotFileName
                )
                if (uri == null) {
                    TakeAndShareScreenshotResult.ScreenShotShareFail
                } else {
                    shareBitmap(context, uri)
                    TakeAndShareScreenshotResult.ScreenShotShareSuccess
                }
            } catch (exception: Throwable) {
                TakeAndShareScreenshotResult.ScreenShotShareFail
            }
        }
    }

    private suspend fun Bitmap.saveToCacheAndGetUri(
        context: Context,
        screenshotFileName: String
    ): Uri? = withContext(
        Dispatchers.IO
    ) {
        return@withContext try {

            val dir = File(context.cacheDir, "screenshots").apply { mkdirs() }
            val file = File(dir, "$screenshotFileName.png")

            file.outputStream().use { out ->
                this@saveToCacheAndGetUri.compress(Bitmap.CompressFormat.PNG, 50, out)
            }

            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (exception: Throwable) {
            null
        }
    }

    private fun shareBitmap(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivities(context, arrayOf(intent))
    }
}

sealed interface ScreenShotViewUIEvents {

    data class GetCurrentGraphicsLayerAndContext(
        val onCurrentGraphicsLayerAndContext: (GraphicsLayer?, Context) -> Unit
    ) : ScreenShotViewUIEvents
}