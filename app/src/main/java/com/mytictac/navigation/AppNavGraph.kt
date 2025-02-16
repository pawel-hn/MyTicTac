package com.mytictac.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mytictac.start.StartRouter
import com.mytictac.start.StartScreen
import com.mytictac.start.StartScreenViewModel
import com.mytictac.tictacgame.GameRouter
import com.mytictac.tictacgame.GameViewModel
import com.mytictac.tictacgame.compose.TicTacScreen


enum class Screen {
    START, GAME
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.START.name
    ) {
        composable(route = Screen.START.name) {
            val viewModel: StartScreenViewModel = hiltViewModel()
            StartScreen(
                viewModel = viewModel,
                router = object : StartRouter {
                    override fun onStartGame() {
                        navController.navigate(Screen.GAME.name)
                    }
                }
            )
        }
        composable(route = Screen.GAME.name) {
            val viewModel: GameViewModel = hiltViewModel()
            TicTacScreen(
                viewModel = viewModel,
                router = object : GameRouter {
                    override fun backToMainScreen() {
                        navController.navigateUp()
                    }
                }
            )
        }
    }
}