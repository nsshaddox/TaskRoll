package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.TaskWithSubTasks
import com.nshaddox.randomtask.domain.repository.SubTaskRepository
import com.nshaddox.randomtask.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetTaskWithSubTasksUseCase
    @Inject
    constructor(
        private val taskRepository: TaskRepository,
        private val subTaskRepository: SubTaskRepository,
    ) {
        operator fun invoke(taskId: Long): Flow<TaskWithSubTasks?> {
            return combine(
                taskRepository.getTaskById(taskId),
                subTaskRepository.getSubTasksForTask(taskId),
            ) { task, subTasks ->
                task?.let { TaskWithSubTasks(task = it, subTasks = subTasks) }
            }
        }
    }
