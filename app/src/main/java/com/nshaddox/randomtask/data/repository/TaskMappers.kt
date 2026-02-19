package com.nshaddox.randomtask.data.repository

import com.nshaddox.randomtask.data.local.TaskEntity
import com.nshaddox.randomtask.domain.model.Task

fun TaskEntity.toDomain(): Task = Task(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
    createdAt = createdAt,
    updatedAt = updatedAt
)
