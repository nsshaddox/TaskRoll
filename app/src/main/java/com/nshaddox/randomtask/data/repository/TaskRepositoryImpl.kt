package com.nshaddox.randomtask.data.repository

import com.nshaddox.randomtask.data.local.TaskDao
import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl
    @Inject
    constructor(
        private val taskDao: TaskDao,
    ) : TaskRepository {
        override fun getAllTasks(): Flow<List<Task>> {
            return taskDao.getAllTasks().map { entities ->
                entities.map { it.toDomain() }
            }
        }

        override fun getIncompleteTasks(): Flow<List<Task>> {
            return taskDao.getIncompleteTasks().map { entities ->
                entities.map { it.toDomain() }
            }
        }

        override fun getTaskById(id: Long): Flow<Task?> {
            return taskDao.getTaskByIdFlow(id).map { it?.toDomain() }
        }

        override fun getCompletedTasks(): Flow<List<Task>> =
            taskDao.getCompletedTasks().map { entities ->
                entities.map { it.toDomain() }
            }

        override fun getTasksByPriority(priority: Priority): Flow<List<Task>> =
            taskDao.getTasksByPriority(priority.name).map { entities ->
                entities.map { it.toDomain() }
            }

        override fun getTasksByCategory(category: String): Flow<List<Task>> =
            taskDao.getTasksByCategory(category).map { entities ->
                entities.map { it.toDomain() }
            }

        override fun searchTasks(query: String): Flow<List<Task>> =
            taskDao.searchTasks(query).map { entities ->
                entities.map { it.toDomain() }
            }

        override suspend fun addTask(task: Task): Result<Long> {
            return try {
                val id = taskDao.insertTask(task.toEntity())
                Result.success(id)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        override suspend fun updateTask(task: Task): Result<Unit> {
            return try {
                taskDao.updateTask(task.toEntity())
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        override suspend fun deleteTask(task: Task): Result<Unit> {
            return try {
                taskDao.deleteTaskById(task.id)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
