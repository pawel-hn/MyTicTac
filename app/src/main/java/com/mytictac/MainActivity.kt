package com.mytictac

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mytictac.navigation.AppNavGraph
import com.mytictac.ui.theme.MyTicTacTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Box(modifier = Modifier.fillMaxSize()) {
                val viewModel: MainViewModel = hiltViewModel()
                val splash by viewModel.isSplashVisible.collectAsStateWithLifecycle()

                MyTicTacTheme {
                    Crossfade(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = MyTicTacTheme.colours.backgroundScreen)
                            .statusBarsPadding()
                            .systemBarsPadding(),
                        targetState = splash,
                        animationSpec = tween(1000),
                        label = ""
                    ) { showSplash ->
                        if (showSplash) {
                            SplashScreen()
                        } else {
                            AppNavGraph()
                        }
                    }
                }
            }
        }
    }
}
