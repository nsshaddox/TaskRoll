package com.nshaddox.randomtask.ui.screens.tasklist

import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.Task
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.TimeZone

class TaskUiMappersTest {
    private lateinit var originalTimeZone: TimeZone

    /** A fixed epoch day value used as "today" in tests. */
    private val todayEpochDay = 20000L

    @Before
    fun setUp() {
        originalTimeZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @After
    fun tearDown() {
        TimeZone.setDefault(originalTimeZone)
    }

    // ── Existing field mapping tests (updated to pass currentEpochDay) ──

    @Test
    fun `toUiModel maps all fields correctly`() {
        val task =
            Task(
                id = 1L,
                title = "Test Task",
                description = "Test Description",
                isCompleted = true,
                createdAt = 1000L,
                updatedAt = 2000L,
            )

        val uiModel = task.toUiModel(currentEpochDay = todayEpochDay)

        assertEquals(1L, uiModel.id)
        assertEquals("Test Task", uiModel.title)
        assertEquals("Test Description", uiModel.description)
        assertEquals(true, uiModel.isCompleted)
        assertEquals("Jan 1, 1970 12:00 AM", uiModel.createdAt)
        assertEquals("Jan 1, 1970 12:00 AM", uiModel.updatedAt)
    }

    @Test
    fun `toUiModel maps null description correctly`() {
        val task =
            Task(
                id = 2L,
                title = "No Description",
                description = null,
                isCompleted = false,
                createdAt = 1000L,
                updatedAt = 2000L,
            )

        val uiModel = task.toUiModel(currentEpochDay = todayEpochDay)

        assertNull(uiModel.description)
    }

    @Test
    fun `toUiModel formats epoch 0 without error`() {
        val task =
            Task(
                id = 3L,
                title = "Epoch Zero",
                description = null,
                isCompleted = false,
                createdAt = 0L,
                updatedAt = 0L,
            )

        val uiModel = task.toUiModel(currentEpochDay = todayEpochDay)

        assertEquals("Jan 1, 1970 12:00 AM", uiModel.createdAt)
        assertEquals("Jan 1, 1970 12:00 AM", uiModel.updatedAt)
    }

    @Test
    fun `toUiModel formats known timestamp correctly`() {
        // 1737046200000L = Jan 16, 2025 4:50 PM UTC
        val task =
            Task(
                id = 4L,
                title = "Known Timestamp",
                description = "Testing known date",
                isCompleted = false,
                createdAt = 1737046200000L,
                updatedAt = 1737046200000L,
            )

        val uiModel = task.toUiModel(currentEpochDay = todayEpochDay)

        assertEquals("Jan 16, 2025 4:50 PM", uiModel.createdAt)
        assertEquals("Jan 16, 2025 4:50 PM", uiModel.updatedAt)
    }

    // ── Priority mapping tests ──

    @Test
    fun `toUiModel maps HIGH priority`() {
        val task = createTask(priority = Priority.HIGH)

        val uiModel = task.toUiModel(currentEpochDay = todayEpochDay)

        assertEquals(Priority.HIGH, uiModel.priority)
    }

    @Test
    fun `toUiModel maps MEDIUM priority`() {
        val task = createTask(priority = Priority.MEDIUM)

        val uiModel = task.toUiModel(currentEpochDay = todayEpochDay)

        assertEquals(Priority.MEDIUM, uiModel.priority)
    }

    @Test
    fun `toUiModel maps LOW priority`() {
        val task = createTask(priority = Priority.LOW)

        val uiModel = task.toUiModel(currentEpochDay = todayEpochDay)

        assertEquals(Priority.LOW, uiModel.priority)
    }

    // ── Priority label tests ──

    @Test
    fun `toUiModel maps HIGH priority to label High`() {
        val task = createTask(priority = Priority.HIGH)

        val uiModel = task.toUiModel(currentEpochDay = todayEpochDay)

        assertEquals("High", uiModel.priorityLabel)
    }

    @Test
    fun `toUiModel maps MEDIUM priority to label Medium`() {
        val task = createTask(priority = Priority.MEDIUM)

        val uiModel = task.toUiModel(currentEpochDay = todayEpochDay)

        assertEquals("Medium", uiModel.priorityLabel)
    }

    @Test
    fun `toUiModel maps LOW priority to label Low`() {
        val task = createTask(priority = Priority.LOW)

        val uiModel = task.toUiModel(currentEpochDay = todayEpochDay)

        assertEquals("Low", uiModel.priorityLabel)
    }

    // ── Due date label tests ──

    @Test
    fun `toUiModel maps null dueDate to null dueDateLabel`() {
        val task = createTask(dueDate = null)

        val uiModel = task.toUiModel(currentEpochDay = todayEpochDay)

        assertNull(uiModel.dueDateLabel)
    }

    @Test
    fun `toUiModel maps epoch day 0 to formatted date`() {
        // Epoch day 0 = Jan 1, 1970
        val task = createTask(dueDate = 0L)

        val uiModel = task.toUiModel(currentEpochDay = todayEpochDay)

        assertEquals("Jan 1, 1970", uiModel.dueDateLabel)
    }

    @Test
    fun `toUiModel maps known epoch day to formatted date`() {
        // Epoch day 20088 = Dec 31, 2024 (20088 days from Jan 1, 1970)
        val task = createTask(dueDate = 20088L)

        val uiModel = task.toUiModel(currentEpochDay = todayEpochDay)

        assertEquals("Dec 31, 2024", uiModel.dueDateLabel)
    }

    // ── isOverdue tests (three boundary cases) ──

    @Test
    fun `toUiModel isOverdue is true when dueDate is before today`() {
        val task = createTask(dueDate = todayEpochDay - 1)

        val uiModel = task.toUiModel(currentEpochDay = todayEpochDay)

        assertTrue(uiModel.isOverdue)
    }

    @Test
    fun `toUiModel isOverdue is false when dueDate equals today`() {
        val task = createTask(dueDate = todayEpochDay)

        val uiModel = task.toUiModel(currentEpochDay = todayEpochDay)

        assertFalse(uiModel.isOverdue)
    }

    @Test
    fun `toUiModel isOverdue is false when dueDate is null`() {
        val task = createTask(dueDate = null)

        val uiModel = task.toUiModel(currentEpochDay = todayEpochDay)

        assertFalse(uiModel.isOverdue)
    }

    @Test
    fun `toUiModel isOverdue is false when dueDate is after today`() {
        val task = createTask(dueDate = todayEpochDay + 1)

        val uiModel = task.toUiModel(currentEpochDay = todayEpochDay)

        assertFalse(uiModel.isOverdue)
    }

    // ── Category mapping tests ──

    @Test
    fun `toUiModel maps null category correctly`() {
        val task = createTask(category = null)

        val uiModel = task.toUiModel(currentEpochDay = todayEpochDay)

        assertNull(uiModel.category)
    }

    @Test
    fun `toUiModel maps non-null category correctly`() {
        val task = createTask(category = "Work")

        val uiModel = task.toUiModel(currentEpochDay = todayEpochDay)

        assertEquals("Work", uiModel.category)
    }

    // ── Combined new fields test ──

    @Test
    fun `toUiModel maps all new fields together`() {
        val task =
            Task(
                id = 10L,
                title = "Full Task",
                description = "Complete task",
                isCompleted = false,
                createdAt = 1000L,
                updatedAt = 2000L,
                priority = Priority.HIGH,
                dueDate = todayEpochDay - 1,
                category = "Personal",
            )

        val uiModel = task.toUiModel(currentEpochDay = todayEpochDay)

        assertEquals(Priority.HIGH, uiModel.priority)
        assertEquals("High", uiModel.priorityLabel)
        assertTrue(uiModel.isOverdue)
        assertEquals("Personal", uiModel.category)
        // dueDateLabel should be a non-null formatted string
        assertTrue(uiModel.dueDateLabel != null)
    }

    // ── Default new fields when task uses defaults ──

    @Test
    fun `toUiModel with default task fields maps priority to MEDIUM`() {
        val task = createTask()

        val uiModel = task.toUiModel(currentEpochDay = todayEpochDay)

        assertEquals(Priority.MEDIUM, uiModel.priority)
        assertEquals("Medium", uiModel.priorityLabel)
        assertNull(uiModel.dueDateLabel)
        assertFalse(uiModel.isOverdue)
        assertNull(uiModel.category)
    }

    /**
     * Helper to create a task with sensible defaults for mapper tests.
     */
    @Suppress("LongParameterList")
    private fun createTask(
        id: Long = 1L,
        title: String = "Test",
        description: String? = null,
        isCompleted: Boolean = false,
        createdAt: Long = 1000L,
        updatedAt: Long = 2000L,
        priority: Priority = Priority.MEDIUM,
        dueDate: Long? = null,
        category: String? = null,
    ): Task =
        Task(
            id = id,
            title = title,
            description = description,
            isCompleted = isCompleted,
            createdAt = createdAt,
            updatedAt = updatedAt,
            priority = priority,
            dueDate = dueDate,
            category = category,
        )
}
