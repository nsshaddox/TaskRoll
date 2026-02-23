package com.nshaddox.randomtask.ui.preview

import com.nshaddox.randomtask.domain.model.Task

/**
 * Sample task data for use in Compose previews
 */
object SampleData {
    val sampleTask = Task(
        id = 1L,
        title = "Complete project wireframes",
        isCompleted = false,
        createdAt = 1_700_000_000_000L,
        updatedAt = 1_700_000_000_000L
    )

    val sampleTasks = listOf(
        Task(1L, "Setup development environment", isCompleted = true, createdAt = 1_700_000_000_000L, updatedAt = 1_700_000_000_000L),
        Task(2L, "Create project documentation", isCompleted = true, createdAt = 1_700_000_100_000L, updatedAt = 1_700_000_100_000L),
        Task(3L, "Design app wireframes", isCompleted = false, createdAt = 1_700_000_200_000L, updatedAt = 1_700_000_200_000L),
        Task(4L, "Implement Room database", isCompleted = false, createdAt = 1_700_000_300_000L, updatedAt = 1_700_000_300_000L),
        Task(5L, "Add dependency injection with Hilt", isCompleted = false, createdAt = 1_700_000_400_000L, updatedAt = 1_700_000_400_000L),
        Task(6L, "Write unit tests", isCompleted = false, createdAt = 1_700_000_500_000L, updatedAt = 1_700_000_500_000L),
        Task(7L, "Conduct code review", isCompleted = false, createdAt = 1_700_000_600_000L, updatedAt = 1_700_000_600_000L)
    )

    val emptyTaskList = emptyList<Task>()

    val singleTask = Task(
        id = 99L,
        title = "Review pull requests and merge approved changes",
        isCompleted = false,
        createdAt = 1_700_000_000_000L,
        updatedAt = 1_700_000_000_000L
    )
}
