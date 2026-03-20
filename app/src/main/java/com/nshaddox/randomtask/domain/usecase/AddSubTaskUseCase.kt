package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.SubTask
import com.nshaddox.randomtask.domain.repository.SubTaskRepository
import javax.inject.Inject

class AddSubTaskUseCase
    @Inject
    constructor(
        private val repository: SubTaskRepository,
    ) {
        suspend operator fun invoke(
            parentTaskId: Long,
            title: String,
        ): Result<Long> {
            if (title.isBlank()) {
                return Result.failure(IllegalArgumentException("Subtask title cannot be blank"))
            }
            val now = System.currentTimeMillis()
            val subTask =
                SubTask(
                    parentTaskId = parentTaskId,
                    title = title,
                    createdAt = now,
                    updatedAt = now,
                )
            return repository.addSubTask(subTask)
        }
    }
