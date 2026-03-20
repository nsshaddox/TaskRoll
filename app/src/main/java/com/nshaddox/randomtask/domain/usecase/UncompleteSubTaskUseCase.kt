package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.SubTask
import com.nshaddox.randomtask.domain.repository.SubTaskRepository
import javax.inject.Inject

class UncompleteSubTaskUseCase
    @Inject
    constructor(
        private val repository: SubTaskRepository,
    ) {
        suspend operator fun invoke(subTask: SubTask): Result<Unit> {
            val uncompleted =
                subTask.copy(
                    isCompleted = false,
                    updatedAt = System.currentTimeMillis(),
                )
            return repository.updateSubTask(uncompleted)
        }
    }
