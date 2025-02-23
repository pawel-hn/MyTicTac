package com.mytictac.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.abs

enum class MinimumClickInterval(val value: Long) {
    DefaultInterval(300L)
}

class UIClickHelper(
    private val currentTimeProvider: () -> Long = { System.currentTimeMillis() },
    private val minimumClickInterval: MinimumClickInterval = MinimumClickInterval.DefaultInterval
) {
    private var lastEventTimeMs: AtomicLong = AtomicLong(0)

    fun debounceWithoutDelay(lifecycle: Lifecycle, event: () -> Unit) {
        synchronized(this) {
            val now = currentTimeProvider()
            if (abs(now - lastEventTimeMs.get()) >= minimumClickInterval.value &&
                lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
            ) {
                event.invoke()
                lastEventTimeMs.set(now)
            }
        }
    }
}

@Composable
fun rememberUIClickHelper(
    currentTimeProvider: () -> Long = { System.currentTimeMillis() },
    minimumClickInterval: MinimumClickInterval = MinimumClickInterval.DefaultInterval
): UIClickHelper = remember {
    UIClickHelper(
        currentTimeProvider = currentTimeProvider,
        minimumClickInterval = minimumClickInterval
    )
}

fun Modifier.debouncedFieldClick(pointerInputKey: Boolean, onClick: (position: Offset) -> Unit) =
    composed {
        val uiClickHelper = rememberUIClickHelper()
        val lifeCycle = LocalLifecycleOwner.current
        this.pointerInput(pointerInputKey) {
            detectTapGestures { position ->
                uiClickHelper.debounceWithoutDelay(
                    lifecycle = lifeCycle.lifecycle,
                    event = { onClick(position) }
                )
            }
        }
    }
