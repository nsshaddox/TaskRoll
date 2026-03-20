package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.SubTask
import com.nshaddox.randomtask.domain.repository.SubTaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeSubTaskRepository : SubTaskRepository {
    private val subTasks = mutableListOf<SubTask>()
    private val subTasksFlow = MutableStateFlow<List<SubTask>>(emptyList())
    private var nextId = 1L

    var shouldFailMutations: Boolean = false
    var shouldFailQueries: Boolean = false

    override fun getSubTasksForTask(parentTaskId: Long): Flow<List<SubTask>> {
        if (shouldFailQueries) return MutableStateFlow(emptyList())
        return subTasksFlow.map { list ->
            list.filter { it.parentTaskId == parentTaskId }
                .sortedWith(
                    compareBy<SubTask> { it.isCompleted }
                        .thenBy { if (!it.isCompleted) it.createdAt else null }
                        .thenByDescending { if (it.isCompleted) it.updatedAt else null },
                )
        }
    }

    override fun getSubTaskCount(parentTaskId: Long): Flow<Int> {
        if (shouldFailQueries) return MutableStateFlow(0)
        return subTasksFlow.map { list -> list.count { it.parentTaskId == parentTaskId } }
    }

    override fun getCompletedSubTaskCount(parentTaskId: Long): Flow<Int> {
        if (shouldFailQueries) return MutableStateFlow(0)
        return subTasksFlow.map { list ->
            list.count { it.parentTaskId == parentTaskId && it.isCompleted }
        }
    }

    override suspend fun addSubTask(subTask: SubTask): Result<Long> {
        if (shouldFailMutations) return Result.failure(RuntimeException("Simulated failure"))
        val id = nextId++
        val newSubTask = subTask.copy(id = id)
        subTasks.add(newSubTask)
        subTasksFlow.update { subTasks.toList() }
        return Result.success(id)
    }

    @Suppress("ReturnCount")
    override suspend fun updateSubTask(subTask: SubTask): Result<Unit> {
        if (shouldFailMutations) return Result.failure(RuntimeException("Simulated failure"))
        val index = subTasks.indexOfFirst { it.id == subTask.id }
        if (index == -1) return Result.failure(NoSuchElementException("SubTask not found"))
        subTasks[index] = subTask
        subTasksFlow.update { subTasks.toList() }
        return Result.success(Unit)
    }

    @Suppress("ReturnCount")
    override suspend fun deleteSubTask(subTaskId: Long): Result<Unit> {
        if (shouldFailMutations) return Result.failure(RuntimeException("Simulated failure"))
        val removed = subTasks.removeAll { it.id == subTaskId }
        if (!removed) return Result.failure(NoSuchElementException("SubTask not found"))
        subTasksFlow.update { subTasks.toList() }
        return Result.success(Unit)
    }

    fun getAllSubTasksSnapshot(): List<SubTask> = subTasks.toList()
}
