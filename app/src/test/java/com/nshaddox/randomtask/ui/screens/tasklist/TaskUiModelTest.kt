package com.nshaddox.randomtask.ui.screens.tasklist

import com.nshaddox.randomtask.domain.model.Priority
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskUiModelTest {
    @Suppress("LongParameterList")
    private fun createModel(
        id: Long = 1L,
        title: String = "Test",
        description: String? = null,
        isCompleted: Boolean = false,
        createdAt: String = "Jan 1, 2025 12:00 AM",
        updatedAt: String = "Jan 1, 2025 12:00 AM",
        priority: Priority = Priority.MEDIUM,
        priorityLabel: String = "Medium",
        dueDateLabel: String? = null,
        isOverdue: Boolean = false,
        category: String? = null,
    ): TaskUiModel =
        TaskUiModel(
            id = id,
            title = title,
            description = description,
            isCompleted = isCompleted,
            createdAt = createdAt,
            updatedAt = updatedAt,
            priority = priority,
            priorityLabel = priorityLabel,
            dueDateLabel = dueDateLabel,
            isOverdue = isOverdue,
            category = category,
        )

    // ── Default value tests ──

    @Test
    fun `default priority is MEDIUM`() {
        val model =
            TaskUiModel(
                id = 1L,
                title = "Test",
                description = null,
                isCompleted = false,
                createdAt = "Jan 1, 2025",
                updatedAt = "Jan 1, 2025",
            )
        assertEquals(Priority.MEDIUM, model.priority)
    }

    @Test
    fun `default priorityLabel is Medium`() {
        val model =
            TaskUiModel(
                id = 1L,
                title = "Test",
                description = null,
                isCompleted = false,
                createdAt = "Jan 1, 2025",
                updatedAt = "Jan 1, 2025",
            )
        assertEquals("Medium", model.priorityLabel)
    }

    @Test
    fun `default dueDateLabel is null`() {
        val model =
            TaskUiModel(
                id = 1L,
                title = "Test",
                description = null,
                isCompleted = false,
                createdAt = "Jan 1, 2025",
                updatedAt = "Jan 1, 2025",
            )
        assertNull(model.dueDateLabel)
    }

    @Test
    fun `default isOverdue is false`() {
        val model =
            TaskUiModel(
                id = 1L,
                title = "Test",
                description = null,
                isCompleted = false,
                createdAt = "Jan 1, 2025",
                updatedAt = "Jan 1, 2025",
            )
        assertFalse(model.isOverdue)
    }

    @Test
    fun `default category is null`() {
        val model =
            TaskUiModel(
                id = 1L,
                title = "Test",
                description = null,
                isCompleted = false,
                createdAt = "Jan 1, 2025",
                updatedAt = "Jan 1, 2025",
            )
        assertNull(model.category)
    }

    // ── Copy tests for new fields ──

    @Test
    fun `copy with priority`() {
        val model = createModel()
        val copied = model.copy(priority = Priority.HIGH)
        assertEquals(Priority.HIGH, copied.priority)
    }

    @Test
    fun `copy with priorityLabel`() {
        val model = createModel()
        val copied = model.copy(priorityLabel = "High")
        assertEquals("High", copied.priorityLabel)
    }

    @Test
    fun `copy with dueDateLabel`() {
        val model = createModel()
        val copied = model.copy(dueDateLabel = "Dec 31, 2025")
        assertEquals("Dec 31, 2025", copied.dueDateLabel)
    }

    @Test
    fun `copy with isOverdue`() {
        val model = createModel()
        val copied = model.copy(isOverdue = true)
        assertTrue(copied.isOverdue)
    }

    @Test
    fun `copy with category`() {
        val model = createModel()
        val copied = model.copy(category = "Work")
        assertEquals("Work", copied.category)
    }

    // ── Equality tests ──

    @Test
    fun `equality for identical models`() {
        val model1 = createModel()
        val model2 = createModel()
        assertEquals(model1, model2)
    }

    @Test
    fun `inequality when priority differs`() {
        val model1 = createModel(priority = Priority.HIGH)
        val model2 = createModel(priority = Priority.LOW)
        assertNotEquals(model1, model2)
    }

    @Test
    fun `inequality when isOverdue differs`() {
        val model1 = createModel(isOverdue = true)
        val model2 = createModel(isOverdue = false)
        assertNotEquals(model1, model2)
    }

    @Test
    fun `inequality when category differs`() {
        val model1 = createModel(category = "Work")
        val model2 = createModel(category = "Personal")
        assertNotEquals(model1, model2)
    }

    // ── toString test ──

    @Test
    fun `toString contains new field values`() {
        val model =
            createModel(
                priority = Priority.HIGH,
                priorityLabel = "High",
                dueDateLabel = "Jan 15, 2025",
                isOverdue = true,
                category = "Work",
            )
        val str = model.toString()
        assertTrue(str.contains("priority=HIGH"))
        assertTrue(str.contains("priorityLabel=High"))
        assertTrue(str.contains("dueDateLabel=Jan 15, 2025"))
        assertTrue(str.contains("isOverdue=true"))
        assertTrue(str.contains("category=Work"))
    }

    // ── hashCode test ──

    @Test
    fun `hashCode is same for equal models`() {
        val model1 = createModel(priority = Priority.HIGH, category = "Work")
        val model2 = createModel(priority = Priority.HIGH, category = "Work")
        assertEquals(model1.hashCode(), model2.hashCode())
    }

    // ── Full model test ──

    @Test
    fun `model with all new fields populated`() {
        val model =
            createModel(
                priority = Priority.LOW,
                priorityLabel = "Low",
                dueDateLabel = "Mar 14, 2026",
                isOverdue = false,
                category = "Personal",
            )
        assertEquals(Priority.LOW, model.priority)
        assertEquals("Low", model.priorityLabel)
        assertEquals("Mar 14, 2026", model.dueDateLabel)
        assertFalse(model.isOverdue)
        assertEquals("Personal", model.category)
    }
}
