package com.nshaddox.randomtask.ui.screens.randomtask

import com.nshaddox.randomtask.domain.model.SubTask
import com.nshaddox.randomtask.domain.model.Task

/**
 * UI state for the random task screen.
 *
 * @property currentTask The task currently selected for display. Null when no task has been selected.
 * @property subTasks The subtasks belonging to the current task.
 * @property isLoading Whether a task selection operation is in progress.
 * @property error An error message to display. Null when there is no error.
 * @property noTasksAvailable Whether the task list is empty and no random selection can be made.
 * @property taskCompleted Whether a task was just successfully completed. Used as a one-shot event to trigger navigation back.
 * @property isAddingSubTask Whether the add-subtask inline form is visible.
 * @property newSubTaskTitle The current text in the add-subtask input field.
 */
data class RandomTaskUiState(
    val currentTask: Task? = null,
    val subTasks: List<SubTask> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val errorResId: Int? = null,
    val noTasksAvailable: Boolean = false,
    val taskCompleted: Boolean = false,
    val isAddingSubTask: Boolean = false,
    val newSubTaskTitle: String = "",
) {
    val totalSubTaskCount: Int get() = subTasks.size
    val completedSubTaskCount: Int get() = subTasks.count { it.isCompleted }
    val hasSubTasks: Boolean get() = subTasks.isNotEmpty()
}
