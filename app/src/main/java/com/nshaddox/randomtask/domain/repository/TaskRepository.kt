package com.nshaddox.randomtask.domain.repository

import com.nshaddox.randomtask.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasks(): Flow<List<Task>>
    suspend fun getTaskById(id: Long): Task?
    suspend fun addTask(task: Task): Result<Long>
    suspend fun updateTask(task: Task): Result<Unit>
    suspend fun deleteTask(taskId: Long): Result<Unit>
}
