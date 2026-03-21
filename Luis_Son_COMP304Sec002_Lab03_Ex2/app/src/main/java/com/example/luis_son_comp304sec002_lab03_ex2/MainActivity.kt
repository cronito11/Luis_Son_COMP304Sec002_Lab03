package com.example.luis_son_comp304sec002_lab03_ex2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.luis_son_comp304sec002_lab03_ex2.ui.navigation.AppNavigation
import com.example.luis_son_comp304sec002_lab03_ex2.ui.theme.Luis_Son_COMP304Sec002_Lab03_Ex2Theme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Luis_Son_COMP304Sec002_Lab03_Ex2Theme {
                // Get window size class for responsive layouts
                val windowSizeClass = calculateWindowSizeClass(this)
                // Create navigation controller
                val navController = rememberNavController()

                // Main app content
                AppContent(
                    windowSizeClass = windowSizeClass,
                    navController = navController
                )
            }
        }
    }
}

@Composable
private fun AppContent(
    windowSizeClass: androidx.compose.material3.windowsizeclass.WindowSizeClass,
    navController: androidx.navigation.NavHostController
) {
    MaterialTheme {
        AppNavigation(
            windowSizeClass = windowSizeClass,
            navController = navController
        )
    }
}