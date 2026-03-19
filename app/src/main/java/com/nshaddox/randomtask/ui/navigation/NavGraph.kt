package com.nshaddox.randomtask.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nshaddox.randomtask.ui.screens.completedtasks.CompletedTasksScreen
import com.nshaddox.randomtask.ui.screens.home.HomeScreen
import com.nshaddox.randomtask.ui.screens.randomtask.RandomTaskScreen
import com.nshaddox.randomtask.ui.screens.settings.SettingsScreen
import com.nshaddox.randomtask.ui.screens.taskeditor.EditTaskScreen
import com.nshaddox.randomtask.ui.screens.tasklist.TaskListScreen
import com.nshaddox.randomtask.ui.theme.NAV_TRANSITION_DURATION_MS

/**
 * Defines the navigation graph for the RandomTask app.
 *
 * Sets up the [NavHost] with all composable destinations and their routes.
 * The start destination is [Screen.Home].
 *
 * @param navController The [NavHostController] used to navigate between screens.
 */
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(NAV_TRANSITION_DURATION_MS),
            ) + fadeIn(animationSpec = tween(NAV_TRANSITION_DURATION_MS))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(NAV_TRANSITION_DURATION_MS),
            ) + fadeOut(animationSpec = tween(NAV_TRANSITION_DURATION_MS))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(NAV_TRANSITION_DURATION_MS),
            ) + fadeIn(animationSpec = tween(NAV_TRANSITION_DURATION_MS))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(NAV_TRANSITION_DURATION_MS),
            ) + fadeOut(animationSpec = tween(NAV_TRANSITION_DURATION_MS))
        },
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.TaskList.route) {
            TaskListScreen(navController = navController)
        }
        composable(Screen.RandomTask.route) {
            RandomTaskScreen(navController = navController)
        }
        composable(
            route = Screen.EditTask.route,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType }),
        ) {
            EditTaskScreen(navController = navController)
        }
        composable(Screen.CompletedTasks.route) {
            CompletedTasksScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}
