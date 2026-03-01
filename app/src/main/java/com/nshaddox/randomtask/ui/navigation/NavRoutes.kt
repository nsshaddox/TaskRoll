package com.nshaddox.randomtask.ui.navigation

/**
 * Defines the navigation routes for the app.
 *
 * Each subclass represents a distinct screen destination in the navigation graph.
 *
 * @property route The unique string identifier used by the navigation framework.
 */
sealed class Screen(val route: String) {

    /** Route to the task list screen where users view and manage their tasks. */
    data object TaskList : Screen("task_list")

    /** Route to the random task selection screen where a task is chosen at random. */
    data object RandomTask : Screen("random_task")

    /** Route to the task editor screen for editing an existing task. */
    data object EditTask : Screen("edit_task/{taskId}") {
        fun createRoute(taskId: Long): String = "edit_task/$taskId"
    }
}
