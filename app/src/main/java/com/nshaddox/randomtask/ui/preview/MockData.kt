package com.nshaddox.randomtask.ui.preview

/**
 * Mock data class representing a task for preview purposes.
 * This is a temporary model used for wireframes until the actual data layer is implemented.
 */
data class Task(
    val id: Int,
    val title: String,
    val isCompleted: Boolean = false
)

/**
 * Sample task data for use in Compose previews
 */
object SampleData {
    val sampleTask = Task(
        id = 1,
        title = "Complete project wireframes",
        isCompleted = false
    )

    val sampleTasks = listOf(
        Task(1, "Setup development environment", isCompleted = true),
        Task(2, "Create project documentation", isCompleted = true),
        Task(3, "Design app wireframes", isCompleted = false),
        Task(4, "Implement Room database", isCompleted = false),
        Task(5, "Add dependency injection with Hilt", isCompleted = false),
        Task(6, "Write unit tests", isCompleted = false),
        Task(7, "Conduct code review", isCompleted = false)
    )

    val emptyTaskList = emptyList<Task>()

    val singleTask = Task(
        id = 99,
        title = "Review pull requests and merge approved changes",
        isCompleted = false
    )
}
