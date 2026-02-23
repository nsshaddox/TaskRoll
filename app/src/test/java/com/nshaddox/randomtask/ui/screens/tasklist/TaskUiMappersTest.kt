package com.nshaddox.randomtask.ui.screens.tasklist

import com.nshaddox.randomtask.domain.model.Task
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.util.TimeZone

class TaskUiMappersTest {

    private lateinit var originalTimeZone: TimeZone

    @Before
    fun setUp() {
        originalTimeZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @After
    fun tearDown() {
        TimeZone.setDefault(originalTimeZone)
    }

    @Test
    fun `toUiModel maps all fields correctly`() {
        val task = Task(
            id = 1L,
            title = "Test Task",
            description = "Test Description",
            isCompleted = true,
            createdAt = 1000L,
            updatedAt = 2000L
        )

        val uiModel = task.toUiModel()

        assertEquals(1L, uiModel.id)
        assertEquals("Test Task", uiModel.title)
        assertEquals("Test Description", uiModel.description)
        assertEquals(true, uiModel.isCompleted)
        assertEquals("Jan 1, 1970 12:00 AM", uiModel.createdAt)
        assertEquals("Jan 1, 1970 12:00 AM", uiModel.updatedAt)
    }

    @Test
    fun `toUiModel maps null description correctly`() {
        val task = Task(
            id = 2L,
            title = "No Description",
            description = null,
            isCompleted = false,
            createdAt = 1000L,
            updatedAt = 2000L
        )

        val uiModel = task.toUiModel()

        assertNull(uiModel.description)
    }

    @Test
    fun `toUiModel formats epoch 0 without error`() {
        val task = Task(
            id = 3L,
            title = "Epoch Zero",
            description = null,
            isCompleted = false,
            createdAt = 0L,
            updatedAt = 0L
        )

        val uiModel = task.toUiModel()

        assertEquals("Jan 1, 1970 12:00 AM", uiModel.createdAt)
        assertEquals("Jan 1, 1970 12:00 AM", uiModel.updatedAt)
    }

    @Test
    fun `toUiModel formats known timestamp correctly`() {
        // 1737046200000L = Jan 16, 2025 4:50 PM UTC
        val task = Task(
            id = 4L,
            title = "Known Timestamp",
            description = "Testing known date",
            isCompleted = false,
            createdAt = 1737046200000L,
            updatedAt = 1737046200000L
        )

        val uiModel = task.toUiModel()

        assertEquals("Jan 16, 2025 4:50 PM", uiModel.createdAt)
        assertEquals("Jan 16, 2025 4:50 PM", uiModel.updatedAt)
    }
}
