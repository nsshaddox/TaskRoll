package com.nshaddox.randomtask.ui.preview

import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.SubTask
import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.ui.screens.tasklist.TaskUiModel

/**
 * Sample task data for use in Compose previews
 */
@Suppress("MagicNumber")
object SampleData {
    val sampleTask =
        Task(
            id = 1L,
            title = "Complete project wireframes",
            isCompleted = false,
            createdAt = 1_700_000_000_000L,
            updatedAt = 1_700_000_000_000L,
            priority = Priority.HIGH,
            dueDate = 20200L,
            category = "Work",
        )

    val sampleTasks =
        listOf(
            Task(
                1L,
                "Setup development environment",
                isCompleted = true,
                createdAt = 1_700_000_000_000L,
                updatedAt = 1_700_000_000_000L,
            ),
            Task(
                2L,
                "Create project documentation",
                isCompleted = true,
                createdAt = 1_700_000_100_000L,
                updatedAt = 1_700_000_100_000L,
            ),
            Task(
                3L,
                "Design app wireframes",
                isCompleted = false,
                createdAt = 1_700_000_200_000L,
                updatedAt = 1_700_000_200_000L,
                priority = Priority.HIGH,
                dueDate = 20200L,
                category = "Work",
            ),
            Task(
                4L,
                "Implement Room database",
                isCompleted = false,
                createdAt = 1_700_000_300_000L,
                updatedAt = 1_700_000_300_000L,
                priority = Priority.MEDIUM,
                category = "Work",
            ),
            Task(
                5L,
                "Add dependency injection with Hilt",
                isCompleted = false,
                createdAt = 1_700_000_400_000L,
                updatedAt = 1_700_000_400_000L,
                priority = Priority.LOW,
            ),
            Task(
                6L,
                "Write unit tests",
                isCompleted = false,
                createdAt = 1_700_000_500_000L,
                updatedAt = 1_700_000_500_000L,
                priority = Priority.HIGH,
                dueDate = 19900L,
                category = "Personal",
            ),
            Task(
                7L,
                "Conduct code review",
                isCompleted = false,
                createdAt = 1_700_000_600_000L,
                updatedAt = 1_700_000_600_000L,
            ),
        )

    val emptyTaskList = emptyList<Task>()

    val singleTask =
        Task(
            id = 99L,
            title = "Review pull requests and merge approved changes",
            isCompleted = false,
            createdAt = 1_700_000_000_000L,
            updatedAt = 1_700_000_000_000L,
        )

    val sampleTaskWithDescription =
        Task(
            id = 100L,
            title = "Refactor authentication module",
            description = "Extract shared auth logic into reusable middleware and add unit tests for token validation",
            isCompleted = false,
            createdAt = 1_700_000_000_000L,
            updatedAt = 1_700_000_000_000L,
        )

    // ── TaskUiModel samples for Compose previews ──

    val sampleTaskUiModel =
        TaskUiModel(
            id = 1L,
            title = "Complete project wireframes",
            description = null,
            isCompleted = false,
            createdAt = "Nov 14, 2023 10:13 PM",
            updatedAt = "Nov 14, 2023 10:13 PM",
            priority = Priority.HIGH,
            priorityLabel = "High",
            dueDateLabel = "Apr 25, 2025",
            isOverdue = false,
            category = "Work",
        )

    val sampleTaskUiModels =
        listOf(
            TaskUiModel(
                id = 1L,
                title = "Setup development environment",
                description = null,
                isCompleted = true,
                createdAt = "Nov 14, 2023 10:13 PM",
                updatedAt = "Nov 14, 2023 10:13 PM",
                priority = Priority.MEDIUM,
                priorityLabel = "Medium",
            ),
            TaskUiModel(
                id = 2L,
                title = "Create project documentation",
                description = null,
                isCompleted = true,
                createdAt = "Nov 14, 2023 10:15 PM",
                updatedAt = "Nov 14, 2023 10:15 PM",
                priority = Priority.MEDIUM,
                priorityLabel = "Medium",
            ),
            TaskUiModel(
                id = 3L,
                title = "Design app wireframes",
                description = null,
                isCompleted = false,
                createdAt = "Nov 14, 2023 10:16 PM",
                updatedAt = "Nov 14, 2023 10:16 PM",
                priority = Priority.HIGH,
                priorityLabel = "High",
                dueDateLabel = "Apr 25, 2025",
                isOverdue = false,
                category = "Work",
            ),
            TaskUiModel(
                id = 4L,
                title = "Implement Room database",
                description = null,
                isCompleted = false,
                createdAt = "Nov 14, 2023 10:18 PM",
                updatedAt = "Nov 14, 2023 10:18 PM",
                priority = Priority.MEDIUM,
                priorityLabel = "Medium",
                category = "Work",
            ),
            TaskUiModel(
                id = 5L,
                title = "Add dependency injection with Hilt",
                description = null,
                isCompleted = false,
                createdAt = "Nov 14, 2023 10:20 PM",
                updatedAt = "Nov 14, 2023 10:20 PM",
                priority = Priority.LOW,
                priorityLabel = "Low",
            ),
            TaskUiModel(
                id = 6L,
                title = "Write unit tests",
                description = null,
                isCompleted = false,
                createdAt = "Nov 14, 2023 10:21 PM",
                updatedAt = "Nov 14, 2023 10:21 PM",
                priority = Priority.HIGH,
                priorityLabel = "High",
                dueDateLabel = "Jun 23, 2024",
                isOverdue = true,
                category = "Personal",
            ),
        )

    val sampleSubTasks =
        listOf(
            SubTask(
                id = 1L,
                parentTaskId = 1L,
                title = "Research design patterns",
                isCompleted = true,
                createdAt = 1_700_000_000_000L,
                updatedAt = 1_700_000_100_000L,
            ),
            SubTask(
                id = 2L,
                parentTaskId = 1L,
                title = "Create initial mockups",
                isCompleted = false,
                createdAt = 1_700_000_100_000L,
                updatedAt = 1_700_000_100_000L,
            ),
            SubTask(
                id = 3L,
                parentTaskId = 1L,
                title = "Review with team",
                isCompleted = false,
                createdAt = 1_700_000_200_000L,
                updatedAt = 1_700_000_200_000L,
            ),
        )

    val sampleOverdueTaskUiModel =
        TaskUiModel(
            id = 10L,
            title = "Submit overdue report",
            description = "Quarterly review document",
            isCompleted = false,
            createdAt = "Jan 1, 2025 9:00 AM",
            updatedAt = "Jan 1, 2025 9:00 AM",
            priority = Priority.HIGH,
            priorityLabel = "High",
            dueDateLabel = "Feb 1, 2025",
            isOverdue = true,
            category = "Work",
        )
}
