package com.nshaddox.randomtask.ui.screens.tasklist

import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.SortOrder
import com.nshaddox.randomtask.domain.model.Task

/**
 * Represents the UI state of the task list screen.
 *
 * @property tasks The current list of tasks to display.
 * @property isLoading Whether a loading operation is in progress.
 * @property errorMessage An optional error message to display. Null when there is no error.
 * @property isAddDialogVisible Whether the add-task dialog is currently visible.
 * @property isEditDialogVisible Whether the edit-task dialog is currently visible.
 * @property editingTask The task currently being edited. Null when not editing.
 * @property searchQuery The current text entered in the search field. Empty string when inactive.
 * @property filterPriority The priority filter currently applied. Null when no priority filter is active.
 * @property filterCategory The category filter currently applied. Null when no category filter is active.
 * @property sortOrder The current sort ordering for the task list.
 * @property availableCategories The list of distinct category values present in the task list,
 *   used to populate filter UI.
 */
data class TaskListUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isAddDialogVisible: Boolean = false,
    val isEditDialogVisible: Boolean = false,
    val editingTask: TaskUiModel? = null,
    val searchQuery: String = "",
    val filterPriority: Priority? = null,
    val filterCategory: String? = null,
    val sortOrder: SortOrder = SortOrder.CREATED_DATE_DESC,
    val availableCategories: List<String> = emptyList(),
)
