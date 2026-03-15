package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetWeightedRandomTaskUseCase
    @Inject
    constructor(
        private val repository: TaskRepository,
    ) {
        suspend operator fun invoke(): Task? {
            val incompleteTasks = repository.getIncompleteTasks().first()
            if (incompleteTasks.isEmpty()) return null
            val weightedPool = buildWeightedPool(incompleteTasks)
            return weightedPool.randomOrNull()
        }

        internal fun buildWeightedPool(tasks: List<Task>): List<Task> =
            tasks.flatMap { task -> List(weightFor(task)) { task } }

        private fun weightFor(task: Task): Int =
            when (task.priority) {
                Priority.HIGH -> WEIGHT_HIGH
                Priority.MEDIUM -> WEIGHT_MEDIUM
                Priority.LOW -> WEIGHT_LOW
            }

        companion object {
            private const val WEIGHT_HIGH = 3
            private const val WEIGHT_MEDIUM = 2
            private const val WEIGHT_LOW = 1
        }
    }
