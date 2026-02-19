package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeTaskRepository : TaskRepository {

    private val tasks = mutableListOf<Task>()
    private val tasksFlow = MutableStateFlow<List<Task>>(emptyList())
    private var nextId = 1L

    override fun getTasks(): Flow<List<Task>> = tasksFlow

    override suspend fun getTaskById(id: Long): Task? {
        return tasks.find { it.id == id }
    }

    override suspend fun addTask(task: Task): Result<Long> {
        val id = nextId++
        val newTask = task.copy(id = id)
        tasks.add(newTask)
        tasksFlow.update { tasks.toList() }
        return Result.success(id)
    }

    override suspend fun updateTask(task: Task): Result<Unit> {
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index == -1) return Result.failure(NoSuchElementException("Task not found"))
        tasks[index] = task
        tasksFlow.update { tasks.toList() }
        return Result.success(Unit)
    }

    override suspend fun deleteTask(taskId: Long): Result<Unit> {
        val removed = tasks.removeAll { it.id == taskId }
        if (!removed) return Result.failure(NoSuchElementException("Task not found"))
        tasksFlow.update { tasks.toList() }
        return Result.success(Unit)
    }
}
