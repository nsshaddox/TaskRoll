package com.nshaddox.randomtask.ui.screens.completedtasks

import com.nshaddox.randomtask.domain.model.Task

/**
 * UI state for the completed tasks screen.
 *
 * @property tasks The list of completed tasks to display.
 * @property isLoading Whether a loading operation is in progress.
 * @property errorMessage An optional error message to display. Null when there is no error.
 */
data class CompletedTasksUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)
