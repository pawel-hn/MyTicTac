package com.mytictac.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mytictac.start.StartRouter
import com.mytictac.start.compose.StartScreen
import com.mytictac.start.StartScreenViewModel
import com.mytictac.game.GameRouter
import com.mytictac.game.GameViewModel
import com.mytictac.game.GameViewModelArguments
import com.mytictac.game.compose.TicTacScreen


enum class Screen {
    START, GAME
}

object NavArguments {
    const val LOAD_GAME = GameViewModelArguments.LOAD_GAME
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

                    override fun onLoadGame() {
                        navController.navigate(
                            Screen.GAME.name +
                                    "?${NavArguments.LOAD_GAME}=true"
                        )
                    }
                }
            )
        }
        composable(
            route = Screen.GAME.name + "?${NavArguments.LOAD_GAME}={${NavArguments.LOAD_GAME}}",
            arguments = listOf(
                navArgument(name = NavArguments.LOAD_GAME) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) {
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