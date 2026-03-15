package com.nshaddox.randomtask.ui.screens.tasklist

import com.nshaddox.randomtask.domain.model.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun formatTimestamp(epochMillis: Long): String =
    SimpleDateFormat("MMM d, yyyy h:mm a", Locale.US).format(Date(epochMillis))

fun Task.toUiModel(): TaskUiModel =
    TaskUiModel(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted,
        createdAt = formatTimestamp(createdAt),
        updatedAt = formatTimestamp(updatedAt),
    )
