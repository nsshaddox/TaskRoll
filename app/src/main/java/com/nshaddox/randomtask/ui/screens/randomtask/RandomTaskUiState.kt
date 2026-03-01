package com.nshaddox.randomtask.ui.screens.randomtask

import com.nshaddox.randomtask.domain.model.Task

/**
 * UI state for the random task screen.
 *
 * @property currentTask The task currently selected for display. Null when no task has been selected.
 * @property isLoading Whether a task selection operation is in progress.
 * @property error An error message to display. Null when there is no error.
 * @property noTasksAvailable Whether the task list is empty and no random selection can be made.
 * @property taskCompleted Whether a task was just successfully completed. Used as a one-shot event to trigger navigation back.
 */
data class RandomTaskUiState(
    val currentTask: Task? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val noTasksAvailable: Boolean = false,
    val taskCompleted: Boolean = false
)
