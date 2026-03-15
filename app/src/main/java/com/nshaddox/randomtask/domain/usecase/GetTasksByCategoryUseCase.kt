package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksByCategoryUseCase
    @Inject
    constructor(
        private val repository: TaskRepository,
    ) {
        operator fun invoke(category: String): Flow<List<Task>> {
            return repository.getTasksByCategory(category)
        }
    }
