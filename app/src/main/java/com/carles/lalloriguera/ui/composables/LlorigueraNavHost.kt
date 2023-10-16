package com.carles.lalloriguera.ui.composables

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.carles.lalloriguera.ui.screens.ConillScreen
import com.carles.lalloriguera.ui.screens.EditTaskScreen
import com.carles.lalloriguera.ui.screens.NewTaskScreen
import com.carles.lalloriguera.ui.screens.Screen
import com.carles.lalloriguera.ui.screens.SignInScreen
import com.carles.lalloriguera.ui.screens.TasksScreen

@Composable
fun LlorigueraNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val onNewTaskClick = { navController.toNewTask() }
    val onEditTaskClick: (String) -> Unit = { taskId -> navController.toEditTask(taskId) }
    val onNoPendingTasks = { navController.toConill() }
    val navigateUp: () -> Unit = { navController.popBackStack() }

    NavHost(
        navController = navController,
        startDestination = Screen.Tasks.route,
        modifier = modifier
    ) {
        signInDestination()
        tasksListDestination(onNewTaskClick, onEditTaskClick, onNoPendingTasks)
        newTaskDestination(navigateUp)
        editTaskDestination(navigateUp)
        conillDestination(navigateUp)
    }
}

private fun NavGraphBuilder.composableWithTransition(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
    ) { backStackEntry ->
        content(backStackEntry)
    }
}

private fun NavGraphBuilder.signInDestination(onSignedIn: () -> Unit = {}) {
    composableWithTransition(Screen.SignIn.route) {
        SignInScreen(hiltViewModel(), onSignedIn)
    }
}

private fun NavGraphBuilder.tasksListDestination(
    onNewTaskClick: () -> Unit,
    onEditTaskClick: (String) -> Unit,
    onNoPendingTasks: () -> Unit,
) {
    composableWithTransition(Screen.Tasks.route) {
        TasksScreen(hiltViewModel(), onNewTaskClick, onEditTaskClick, onNoPendingTasks)
    }
}

private fun NavGraphBuilder.newTaskDestination(navigateUp: () -> Unit) {
    composableWithTransition(Screen.NewTask.route) {
        NewTaskScreen(hiltViewModel(), navigateUp)
    }
}

private fun NavGraphBuilder.editTaskDestination(navigateUp: () -> Unit) {
    composableWithTransition(Screen.EditTask.route) {
        EditTaskScreen(hiltViewModel(), navigateUp)
    }
}

private fun NavGraphBuilder.conillDestination(navigateUp: () -> Unit) {
    composableWithTransition(Screen.Conill.route) {
        ConillScreen(navigateUp)
    }
}

private fun NavController.toNewTask() {
    navigate(Screen.NewTask.route)
}

private fun NavController.toEditTask(taskId: String) {
    navigate(Screen.EditTask.navigationRoute(taskId))
}

private fun NavController.toConill() {
    navigate(Screen.Conill.route)
}