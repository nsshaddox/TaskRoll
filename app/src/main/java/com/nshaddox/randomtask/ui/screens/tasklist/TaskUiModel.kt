package com.nshaddox.randomtask.ui.screens.tasklist

import com.nshaddox.randomtask.domain.model.Priority

/**
 * UI model representing a task with pre-formatted display values.
 *
 * @property id Unique identifier for the task.
 * @property title Short name or summary of the task.
 * @property description Optional longer description of the task. Null when not provided.
 * @property isCompleted Whether the task has been marked as done.
 * @property createdAt Formatted date string for when the task was created.
 * @property updatedAt Formatted date string for when the task was last modified.
 * @property priority The priority level of the task.
 * @property priorityLabel Human-readable label for the priority (e.g., "High", "Medium", "Low").
 * @property dueDateLabel Formatted due date string (e.g., "Jan 15, 2025"). Null when no due date is set.
 * @property isOverdue Whether the task is past its due date.
 * @property category Optional category label for the task. Null when not set.
 */
data class TaskUiModel(
    val id: Long,
    val title: String,
    val description: String?,
    val isCompleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val priority: Priority = Priority.MEDIUM,
    val priorityLabel: String = "Medium",
    val dueDateLabel: String? = null,
    val isOverdue: Boolean = false,
    val category: String? = null,
)
