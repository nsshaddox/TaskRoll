package com.nshaddox.randomtask.domain.model

/**
 * Represents the available sort orderings for task lists.
 *
 * Used by both [TaskListUiState][com.nshaddox.randomtask.ui.screens.tasklist.TaskListUiState]
 * and [SettingsUiState][com.nshaddox.randomtask.ui.screens.settings.SettingsUiState]
 * to control how tasks are displayed.
 */
enum class SortOrder {
    /** Sort by creation date, newest first. */
    CREATED_DATE_DESC,

    /** Sort by due date, earliest first. */
    DUE_DATE_ASC,

    /** Sort by priority, highest first. */
    PRIORITY_DESC,

    /** Sort alphabetically by title, A-Z. */
    TITLE_ASC,
}
