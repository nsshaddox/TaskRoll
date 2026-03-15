package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksByPriorityUseCase
    @Inject
    constructor(
        private val repository: TaskRepository,
    ) {
        operator fun invoke(priority: Priority): Flow<List<Task>> {
            return repository.getTasksByPriority(priority)
        }
    }
