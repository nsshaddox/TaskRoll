package com.nshaddox.randomtask.domain.model

/**
 * Domain model representing a subtask belonging to a parent [Task].
 *
 * @property id Unique identifier for the subtask. Defaults to 0 for new subtasks.
 * @property parentTaskId The id of the parent task this subtask belongs to.
 * @property title Short name or summary of the subtask.
 * @property isCompleted Whether the subtask has been marked as done.
 * @property createdAt Epoch milliseconds when the subtask was created.
 * @property updatedAt Epoch milliseconds when the subtask was last modified.
 */
data class SubTask(
    val id: Long = 0,
    val parentTaskId: Long,
    val title: String,
    val isCompleted: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long,
)
