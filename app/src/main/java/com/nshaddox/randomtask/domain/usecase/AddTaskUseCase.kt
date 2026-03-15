package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.domain.repository.TaskRepository
import javax.inject.Inject

class AddTaskUseCase
    @Inject
    constructor(
        private val repository: TaskRepository,
    ) {
        suspend operator fun invoke(
            title: String,
            description: String? = null,
            priority: Priority = Priority.MEDIUM,
            dueDate: Long? = null,
            category: String? = null,
        ): Result<Long> {
            if (title.isBlank()) {
                return Result.failure(IllegalArgumentException("Task title cannot be blank"))
            }
            val now = System.currentTimeMillis()
            val task =
                Task(
                    title = title,
                    description = description,
                    priority = priority,
                    dueDate = dueDate,
                    category = category,
                    createdAt = now,
                    updatedAt = now,
                )
            return repository.addTask(task)
        }
    }
