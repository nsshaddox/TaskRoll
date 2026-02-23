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
 */
data class Task(
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)
