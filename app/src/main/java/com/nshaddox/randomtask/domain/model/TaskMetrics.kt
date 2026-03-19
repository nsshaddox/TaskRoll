package com.nshaddox.randomtask.domain.model

/**
 * Aggregated metrics about the user's task activity.
 *
 * @property completedToday Number of tasks completed today.
 * @property completedThisWeek Number of tasks completed this week (Monday to now).
 * @property streak Consecutive calendar days ending today with at least one completed task.
 * @property totalRemaining Number of incomplete tasks.
 * @property completionRate Ratio of completed tasks to total tasks (0.0 to 1.0). 0.0 when no tasks exist.
 * @property overdueCount Number of incomplete tasks past their due date.
 */
data class TaskMetrics(
    val completedToday: Int = 0,
    val completedThisWeek: Int = 0,
    val streak: Int = 0,
    val totalRemaining: Int = 0,
    val completionRate: Float = 0f,
    val overdueCount: Int = 0,
)
