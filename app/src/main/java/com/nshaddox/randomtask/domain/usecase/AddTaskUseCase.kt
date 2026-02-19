package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.domain.repository.TaskRepository
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Result<Long> {
        if (task.title.isBlank()) {
            return Result.failure(IllegalArgumentException("Task title cannot be blank"))
        }
        return repository.addTask(task)
    }
}
