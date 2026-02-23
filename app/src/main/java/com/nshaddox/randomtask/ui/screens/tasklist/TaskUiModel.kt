package com.nshaddox.randomtask.ui.screens.tasklist

/**
 * UI model representing a task with pre-formatted display values.
 *
 * @property id Unique identifier for the task.
 * @property title Short name or summary of the task.
 * @property description Optional longer description of the task. Null when not provided.
 * @property isCompleted Whether the task has been marked as done.
 * @property createdAt Formatted date string for when the task was created.
 * @property updatedAt Formatted date string for when the task was last modified.
 */
data class TaskUiModel(
    val id: Long,
    val title: String,
    val description: String?,
    val isCompleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)
