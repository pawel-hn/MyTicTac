package com.example.mytictac

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mytictac.tictacgame.TicTacScreen
import com.example.mytictac.ui.theme.MyTicTacTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTicTacTheme {
                   TicTacScreen()
            }
        }
    }
}

