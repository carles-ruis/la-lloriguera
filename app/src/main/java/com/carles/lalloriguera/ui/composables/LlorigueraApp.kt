package com.carles.lalloriguera.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.carles.lalloriguera.ui.screens.Screen

@Composable
fun LlorigueraApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val screen = screens.find { it.route == backStackEntry?.destination?.route } ?: Screen.Tasks
    val onUpClick: () -> Unit = { navController.popBackStack() }

    Scaffold(
        topBar = {
            LlorigueraTopBar(title = screen.title, showUpButton = screen.showUpButton, onUpClick = onUpClick)
        }) { paddingValues ->
        LlorigueraNavHost(navController = navController, modifier = Modifier.padding(paddingValues))
    }
}

private val screens = listOf(Screen.Tasks, Screen.Conill, Screen.NewTask, Screen.EditTask)