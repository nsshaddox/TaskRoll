package com.nshaddox.randomtask.ui.screens.home

import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.domain.model.TaskMetrics

/**
 * UI state for the Home screen.
 *
 * @property currentTask The randomly selected task for the hero section. Null when none selected.
 * @property useWeightedRandom Whether to use priority-weighted random selection.
 * @property metrics Aggregated task metrics for the dashboard section.
 * @property isLoading Whether an operation is in progress.
 * @property error An error message to display. Null when there is no error.
 * @property noTasksAvailable Whether the task list is empty and no random selection can be made.
 * @property taskCompleted Whether a task was just successfully completed.
 */
data class HomeUiState(
    val currentTask: Task? = null,
    val useWeightedRandom: Boolean = false,
    val metrics: TaskMetrics = TaskMetrics(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val noTasksAvailable: Boolean = false,
    val taskCompleted: Boolean = false,
)
