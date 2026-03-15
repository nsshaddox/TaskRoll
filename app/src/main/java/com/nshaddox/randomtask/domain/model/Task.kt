package com.nshaddox.randomtask.domain.model

/**
 * Domain model representing a task.
 *
 * @property id Unique identifier for the task. Defaults to 0 for new tasks.
 * @property title Short name or summary of the task.
 * @property description Optional longer description of the task. Null when not provided.
 * @property isCompleted Whether the task has been marked as done.
 * @property createdAt Epoch milliseconds when the task was created.
 * @property updatedAt Epoch milliseconds when the task was last modified.
 * @property priority The priority level of the task. Defaults to [Priority.MEDIUM].
 * @property dueDate Optional due date stored as epoch days (days since 1970-01-01). Null when not set.
 * @property category Optional category label for grouping tasks. Null when not set.
 */
data class Task(
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long,
    val priority: Priority = Priority.MEDIUM,
    val dueDate: Long? = null,
    val category: String? = null,
)
