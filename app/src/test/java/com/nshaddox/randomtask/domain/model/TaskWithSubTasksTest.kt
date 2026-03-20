package com.nshaddox.randomtask.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskWithSubTasksTest {
    private fun createTask(id: Long = 1L) =
        Task(
            id = id,
            title = "Test Task",
            createdAt = 1000L,
            updatedAt = 2000L,
        )

    private fun createSubTask(
        id: Long = 1L,
        parentTaskId: Long = 1L,
        isCompleted: Boolean = false,
    ) = SubTask(
        id = id,
        parentTaskId = parentTaskId,
        title = "SubTask $id",
        isCompleted = isCompleted,
        createdAt = 1000L,
        updatedAt = 2000L,
    )

    @Test
    fun `totalSubTaskCount returns correct count`() {
        val taskWithSubTasks =
            TaskWithSubTasks(
                task = createTask(),
                subTasks = listOf(createSubTask(1), createSubTask(2), createSubTask(3)),
            )

        assertEquals(3, taskWithSubTasks.totalSubTaskCount)
    }

    @Test
    fun `totalSubTaskCount returns zero for empty list`() {
        val taskWithSubTasks =
            TaskWithSubTasks(
                task = createTask(),
                subTasks = emptyList(),
            )

        assertEquals(0, taskWithSubTasks.totalSubTaskCount)
    }

    @Test
    fun `completedSubTaskCount returns count of completed subtasks`() {
        val taskWithSubTasks =
            TaskWithSubTasks(
                task = createTask(),
                subTasks =
                    listOf(
                        createSubTask(1, isCompleted = true),
                        createSubTask(2, isCompleted = false),
                        createSubTask(3, isCompleted = true),
                        createSubTask(4, isCompleted = false),
                    ),
            )

        assertEquals(2, taskWithSubTasks.completedSubTaskCount)
    }

    @Test
    fun `completedSubTaskCount returns zero when none completed`() {
        val taskWithSubTasks =
            TaskWithSubTasks(
                task = createTask(),
                subTasks = listOf(createSubTask(1), createSubTask(2)),
            )

        assertEquals(0, taskWithSubTasks.completedSubTaskCount)
    }

    @Test
    fun `hasSubTasks returns true for non-empty list`() {
        val taskWithSubTasks =
            TaskWithSubTasks(
                task = createTask(),
                subTasks = listOf(createSubTask(1)),
            )

        assertTrue(taskWithSubTasks.hasSubTasks)
    }

    @Test
    fun `hasSubTasks returns false for empty list`() {
        val taskWithSubTasks =
            TaskWithSubTasks(
                task = createTask(),
                subTasks = emptyList(),
            )

        assertFalse(taskWithSubTasks.hasSubTasks)
    }
}
