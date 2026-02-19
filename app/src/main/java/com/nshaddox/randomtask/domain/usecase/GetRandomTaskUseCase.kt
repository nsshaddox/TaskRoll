package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetRandomTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(): Task? {
        val tasks = repository.getTasks().first()
        val incompleteTasks = tasks.filter { !it.isCompleted }
        return if (incompleteTasks.isEmpty()) null else incompleteTasks.random()
    }
}
