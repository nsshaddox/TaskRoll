package com.nshaddox.randomtask.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nshaddox.randomtask.ui.screens.randomtask.RandomTaskScreen
import com.nshaddox.randomtask.ui.screens.tasklist.TaskListScreen

/**
 * Defines the navigation graph for the RandomTask app.
 *
 * Sets up the [NavHost] with all composable destinations and their routes.
 * The start destination is [Screen.TaskList].
 *
 * @param navController The [NavHostController] used to navigate between screens.
 */
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.TaskList.route
    ) {
        composable(Screen.TaskList.route) {
            TaskListScreen(navController = navController)
        }
        composable(Screen.RandomTask.route) {
            RandomTaskScreen(navController = navController)
        }
    }
}
