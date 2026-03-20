package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.repository.SubTaskRepository
import javax.inject.Inject

class DeleteSubTaskUseCase
    @Inject
    constructor(
        private val repository: SubTaskRepository,
    ) {
        suspend operator fun invoke(subTaskId: Long): Result<Unit> {
            return repository.deleteSubTask(subTaskId)
        }
    }
