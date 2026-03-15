package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeTaskRepository : TaskRepository {
    private val tasks = mutableListOf<Task>()
    private val tasksFlow = MutableStateFlow<List<Task>>(emptyList())
    private var nextId = 1L

    override fun getAllTasks(): Flow<List<Task>> = tasksFlow

    override fun getIncompleteTasks(): Flow<List<Task>> {
        return tasksFlow.map { list -> list.filter { !it.isCompleted } }
    }

    override fun getTaskById(id: Long): Flow<Task?> {
        return tasksFlow.map { list -> list.find { it.id == id } }
    }

    override fun getCompletedTasks(): Flow<List<Task>> = tasksFlow.map { list -> list.filter { it.isCompleted } }

    override fun getTasksByPriority(priority: Priority): Flow<List<Task>> =
        tasksFlow.map { list -> list.filter { it.priority == priority && !it.isCompleted } }

    override fun getTasksByCategory(category: String): Flow<List<Task>> =
        tasksFlow.map { list -> list.filter { it.category == category && !it.isCompleted } }

    override fun searchTasks(query: String): Flow<List<Task>> =
        tasksFlow.map { list ->
            list.filter {
                it.title.contains(query) ||
                    it.description?.contains(query) == true
            }
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

    override suspend fun deleteTask(task: Task): Result<Unit> {
        val removed = tasks.removeAll { it.id == task.id }
        if (!removed) return Result.failure(NoSuchElementException("Task not found"))
        tasksFlow.update { tasks.toList() }
        return Result.success(Unit)
    }

    fun getAllTasksSnapshot(): List<Task> = tasks.toList()
}
