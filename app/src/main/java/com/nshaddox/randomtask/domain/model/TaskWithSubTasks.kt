package com.nshaddox.randomtask.domain.model

/**
 * Composite domain model that pairs a [Task] with its [SubTask] list.
 *
 * @property task The parent task.
 * @property subTasks The subtasks belonging to this task, sorted incomplete-first.
 */
data class TaskWithSubTasks(
    val task: Task,
    val subTasks: List<SubTask>,
) {
    val totalSubTaskCount: Int get() = subTasks.size
    val completedSubTaskCount: Int get() = subTasks.count { it.isCompleted }
    val hasSubTasks: Boolean get() = subTasks.isNotEmpty()
}
