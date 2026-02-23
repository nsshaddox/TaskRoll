package com.nshaddox.randomtask.ui.screens.tasklist

import com.nshaddox.randomtask.domain.model.Task

/**
 * Represents the UI state of the task list screen.
 *
 * @property tasks The current list of tasks to display.
 * @property isLoading Whether a loading operation is in progress.
 * @property errorMessage An optional error message to display. Null when there is no error.
 * @property isAddDialogVisible Whether the add-task dialog is currently visible.
 */
data class TaskListUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isAddDialogVisible: Boolean = false
)
