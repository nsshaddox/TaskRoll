package com.nshaddox.randomtask.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TaskTest {
    @Test
    fun `task default priority is MEDIUM`() {
        val task =
            Task(
                title = "Test",
                createdAt = 1000L,
                updatedAt = 2000L,
            )

        assertEquals(Priority.MEDIUM, task.priority)
    }

    @Test
    fun `task default dueDate is null`() {
        val task =
            Task(
                title = "Test",
                createdAt = 1000L,
                updatedAt = 2000L,
            )

        assertNull(task.dueDate)
    }

    @Test
    fun `task default category is null`() {
        val task =
            Task(
                title = "Test",
                createdAt = 1000L,
                updatedAt = 2000L,
            )

        assertNull(task.category)
    }

    @Test
    fun `task with explicit priority stores correct value`() {
        val task =
            Task(
                title = "Test",
                createdAt = 1000L,
                updatedAt = 2000L,
                priority = Priority.HIGH,
            )

        assertEquals(Priority.HIGH, task.priority)
    }

    @Test
    fun `task with explicit dueDate stores correct value`() {
        val epochDays = 19800L
        val task =
            Task(
                title = "Test",
                createdAt = 1000L,
                updatedAt = 2000L,
                dueDate = epochDays,
            )

        assertEquals(epochDays, task.dueDate)
    }

    @Test
    fun `task with explicit category stores correct value`() {
        val task =
            Task(
                title = "Test",
                createdAt = 1000L,
                updatedAt = 2000L,
                category = "Work",
            )

        assertEquals("Work", task.category)
    }

    @Test
    fun `task with all new fields populated`() {
        val task =
            Task(
                title = "Full Task",
                createdAt = 1000L,
                updatedAt = 2000L,
                priority = Priority.LOW,
                dueDate = 19800L,
                category = "Personal",
            )

        assertEquals(Priority.LOW, task.priority)
        assertEquals(19800L, task.dueDate)
        assertEquals("Personal", task.category)
    }

    @Test
    fun `priority enum has three values`() {
        val values = Priority.entries
        assertEquals(3, values.size)
        assertEquals(Priority.LOW, values[0])
        assertEquals(Priority.MEDIUM, values[1])
        assertEquals(Priority.HIGH, values[2])
    }

    @Test
    fun `existing task construction without new fields still works`() {
        val task =
            Task(
                id = 1L,
                title = "Existing",
                description = "Desc",
                isCompleted = false,
                createdAt = 1000L,
                updatedAt = 2000L,
            )

        assertEquals(1L, task.id)
        assertEquals("Existing", task.title)
        assertEquals("Desc", task.description)
        assertEquals(false, task.isCompleted)
        assertEquals(1000L, task.createdAt)
        assertEquals(2000L, task.updatedAt)
        // New fields should have defaults
        assertEquals(Priority.MEDIUM, task.priority)
        assertNull(task.dueDate)
        assertNull(task.category)
    }
}
