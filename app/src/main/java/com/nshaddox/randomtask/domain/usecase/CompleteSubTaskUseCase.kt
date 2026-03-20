package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.SubTask
import com.nshaddox.randomtask.domain.repository.SubTaskRepository
import javax.inject.Inject

class CompleteSubTaskUseCase
    @Inject
    constructor(
        private val repository: SubTaskRepository,
    ) {
        suspend operator fun invoke(subTask: SubTask): Result<Unit> {
            val completed =
                subTask.copy(
                    isCompleted = true,
                    updatedAt = System.currentTimeMillis(),
                )
            return repository.updateSubTask(completed)
        }
    }
