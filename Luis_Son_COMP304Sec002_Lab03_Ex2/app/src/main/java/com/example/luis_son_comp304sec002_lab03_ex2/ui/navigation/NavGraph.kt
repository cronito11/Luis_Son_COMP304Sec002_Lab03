package com.example.luis_son_comp304sec002_lab03_ex2.ui.navigation

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.luis_son_comp304sec002_lab03_ex2.ui.screens.AddMovieScreen
import com.example.luis_son_comp304sec002_lab03_ex2.ui.screens.DeleteMovieScreen
import com.example.luis_son_comp304sec002_lab03_ex2.ui.screens.EditMovieScreen
import com.example.luis_son_comp304sec002_lab03_ex2.ui.screens.FavoriteMoviesScreen
import com.example.luis_son_comp304sec002_lab03_ex2.ui.screens.HomeScreen

@Composable
fun AppNavigation(navController: NavHostController, windowSizeClass: WindowSizeClass) {
    val isExpanded = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact
    val startDestination = if (isExpanded) "home_detail" else "home"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("home") {
            HomeScreen(navController, windowSizeClass = windowSizeClass)
        }
        composable("home_detail") {
            HomeScreen(navController, windowSizeClass = windowSizeClass)
        }
        composable("add") {
            AddMovieScreen(navController)
        }
        composable(
            route = "edit/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId")
            EditMovieScreen(navController = navController, movieId = movieId)
        }
        composable("delete") {
            DeleteMovieScreen(navController)
        }
        composable("favorites") {
            FavoriteMoviesScreen(navController)
        }
    }
}