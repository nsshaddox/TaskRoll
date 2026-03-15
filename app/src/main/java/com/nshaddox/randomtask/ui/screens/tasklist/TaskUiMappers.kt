package com.nshaddox.randomtask.ui.screens.tasklist

import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun formatTimestamp(epochMillis: Long): String =
    SimpleDateFormat("MMM d, yyyy h:mm a", Locale.US).format(Date(epochMillis))

private const val MILLIS_PER_DAY = 24L * 60L * 60L * 1000L

private fun formatEpochDays(epochDays: Long): String {
    val epochMillis = epochDays * MILLIS_PER_DAY
    return SimpleDateFormat("MMM d, yyyy", Locale.US).format(Date(epochMillis))
}

private fun priorityToLabel(priority: Priority): String =
    when (priority) {
        Priority.HIGH -> "High"
        Priority.MEDIUM -> "Medium"
        Priority.LOW -> "Low"
    }

/**
 * Maps a [Task] domain model to a [TaskUiModel] with pre-formatted display values.
 *
 * @param currentEpochDay The current date expressed as epoch days (days since 1970-01-01),
 *   used to determine whether the task is overdue. Callers should pass today's epoch-day value.
 */
fun Task.toUiModel(currentEpochDay: Long): TaskUiModel =
    TaskUiModel(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted,
        createdAt = formatTimestamp(createdAt),
        updatedAt = formatTimestamp(updatedAt),
        priority = priority,
        priorityLabel = priorityToLabel(priority),
        dueDateLabel = dueDate?.let { formatEpochDays(it) },
        isOverdue = dueDate != null && dueDate < currentEpochDay,
        category = category,
    )
