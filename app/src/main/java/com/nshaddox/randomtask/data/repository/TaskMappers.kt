package com.nshaddox.randomtask.data.repository

import com.nshaddox.randomtask.data.local.TaskEntity
import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.Task

fun TaskEntity.toDomain(): Task =
    Task(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted,
        createdAt = createdAt,
        updatedAt = updatedAt,
        priority = Priority.entries.find { it.name == priority } ?: Priority.MEDIUM,
        dueDate = dueDate,
        category = category,
    )

fun Task.toEntity(): TaskEntity =
    TaskEntity(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted,
        createdAt = createdAt,
        updatedAt = updatedAt,
        priority = priority.name,
        dueDate = dueDate,
        category = category,
    )
