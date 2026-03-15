package com.nshaddox.randomtask.domain.repository

import com.nshaddox.randomtask.domain.model.Task
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing [Task] persistence.
 *
 * Provides methods to observe, retrieve, create, update, and delete tasks.
 * Implementations may use any backing store (e.g., Room, network, in-memory).
 */
interface TaskRepository {
    /**
     * Observes all tasks, ordered by creation date descending.
     *
     * @return a [Flow] emitting the full list of tasks whenever the data changes.
     */
    fun getAllTasks(): Flow<List<Task>>

    /**
     * Observes only incomplete tasks, ordered by creation date descending.
     *
     * @return a [Flow] emitting incomplete tasks whenever the data changes.
     */
    fun getIncompleteTasks(): Flow<List<Task>>

    /**
     * Observes a single task by its unique identifier.
     *
     * @param id the unique identifier of the task.
     * @return a [Flow] emitting the task if found, or null if no task matches the id.
     */
    fun getTaskById(id: Long): Flow<Task?>

    /**
     * Adds a new task to the repository.
     *
     * @param task the task to add. The [Task.id] field is ignored; a new id is generated.
     * @return a [Result] containing the generated id on success, or an exception on failure.
     */
    suspend fun addTask(task: Task): Result<Long>

    /**
     * Updates an existing task in the repository.
     *
     * @param task the task with updated fields. The [Task.id] must match an existing task.
     * @return a [Result] containing [Unit] on success, or an exception on failure.
     */
    suspend fun updateTask(task: Task): Result<Unit>

    /**
     * Deletes a task from the repository.
     *
     * @param task the task to delete. The [Task.id] is used to identify the record to remove.
     * @return a [Result] containing [Unit] on success, or an exception on failure.
     */
    suspend fun deleteTask(task: Task): Result<Unit>
}
