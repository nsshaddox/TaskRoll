package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.repository.TaskRepository
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId: Long): Result<Unit> {
        return repository.deleteTask(taskId)
    }
}
