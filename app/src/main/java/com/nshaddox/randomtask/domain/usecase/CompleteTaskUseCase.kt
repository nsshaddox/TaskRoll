package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.domain.repository.TaskRepository
import javax.inject.Inject

class CompleteTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Result<Unit> {
        val completed = task.copy(
            isCompleted = true,
            updatedAt = System.currentTimeMillis()
        )
        return repository.updateTask(completed)
    }
}
