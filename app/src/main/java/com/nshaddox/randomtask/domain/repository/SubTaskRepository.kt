package com.nshaddox.randomtask.domain.repository

import com.nshaddox.randomtask.domain.model.SubTask
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing [SubTask] persistence.
 *
 * Provides methods to observe, create, update, and delete subtasks
 * belonging to a parent task.
 */
interface SubTaskRepository {
    /**
     * Observes subtasks for a given parent task, sorted with incomplete first
     * (by creation date ascending) then completed (by update date descending).
     */
    fun getSubTasksForTask(parentTaskId: Long): Flow<List<SubTask>>

    /**
     * Observes the total count of subtasks for a given parent task.
     */
    fun getSubTaskCount(parentTaskId: Long): Flow<Int>

    /**
     * Observes the count of completed subtasks for a given parent task.
     */
    fun getCompletedSubTaskCount(parentTaskId: Long): Flow<Int>

    /**
     * Adds a new subtask.
     *
     * @return a [Result] containing the generated id on success, or an exception on failure.
     */
    suspend fun addSubTask(subTask: SubTask): Result<Long>

    /**
     * Updates an existing subtask.
     *
     * @return a [Result] containing [Unit] on success, or an exception on failure.
     */
    suspend fun updateSubTask(subTask: SubTask): Result<Unit>

    /**
     * Deletes a subtask by its id.
     *
     * @return a [Result] containing [Unit] on success, or an exception on failure.
     */
    suspend fun deleteSubTask(subTaskId: Long): Result<Unit>
}
