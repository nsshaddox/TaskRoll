package com.nshaddox.randomtask.data.repository

import com.nshaddox.randomtask.data.local.SubTaskEntity
import com.nshaddox.randomtask.domain.model.SubTask

fun SubTaskEntity.toDomain(): SubTask =
    SubTask(
        id = id,
        parentTaskId = parentTaskId,
        title = title,
        isCompleted = isCompleted,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun SubTask.toEntity(): SubTaskEntity =
    SubTaskEntity(
        id = id,
        parentTaskId = parentTaskId,
        title = title,
        isCompleted = isCompleted,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
