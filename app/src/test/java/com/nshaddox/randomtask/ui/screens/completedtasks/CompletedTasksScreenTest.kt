package com.nshaddox.randomtask.ui.screens.completedtasks

import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class CompletedTasksScreenTest {
    // --- formatCompletedDate tests ---

    @Test
    fun `formatCompletedDate formats epoch millis to readable date string`() {
        // 2023-11-14T22:13:20Z in epoch millis
        val epochMillis = 1_700_000_000_000L
        val formatted = formatCompletedDate(epochMillis, Locale.ENGLISH)
        // Should contain the date portion (month and day at minimum)
        assertTrue(
            "Expected formatted date to contain 'Nov' and '14', got: $formatted",
            formatted.contains("Nov") && formatted.contains("14"),
        )
    }

    @Test
    fun `formatCompletedDate formats different epoch millis correctly`() {
        // 2023-11-15T00:01:40Z
        val epochMillis = 1_700_000_100_000L
        val formatted = formatCompletedDate(epochMillis, Locale.ENGLISH)
        assertTrue(
            "Expected formatted date to contain 'Nov', got: $formatted",
            formatted.contains("Nov"),
        )
    }

    @Test
    fun `formatCompletedDate returns non-empty string for zero epoch`() {
        val formatted = formatCompletedDate(0L, Locale.ENGLISH)
        assertTrue("Expected non-empty formatted date", formatted.isNotEmpty())
    }

    @Test
    fun `formatCompletedDate returns consistent results for same input`() {
        val epochMillis = 1_700_000_000_000L
        val first = formatCompletedDate(epochMillis, Locale.ENGLISH)
        val second = formatCompletedDate(epochMillis, Locale.ENGLISH)
        assertEquals(first, second)
    }

    // --- resolveContentState tests (screen state-selection logic) ---

    @Test
    fun `resolveContentState returns Loading when isLoading is true`() {
        val result = resolveContentState(isLoading = true, tasks = emptyList())
        assertEquals(CompletedTasksContentState.Loading, result)
    }

    @Test
    fun `resolveContentState returns Loading even when tasks are present`() {
        val tasks = listOf(createTestTask(id = 1, title = "Task 1"))
        val result = resolveContentState(isLoading = true, tasks = tasks)
        assertEquals(CompletedTasksContentState.Loading, result)
    }

    @Test
    fun `resolveContentState returns Empty when not loading and tasks list is empty`() {
        val result = resolveContentState(isLoading = false, tasks = emptyList())
        assertEquals(CompletedTasksContentState.Empty, result)
    }

    @Test
    fun `resolveContentState returns Populated when not loading and tasks are present`() {
        val tasks =
            listOf(
                createTestTask(id = 1, title = "Task 1"),
                createTestTask(id = 2, title = "Task 2"),
            )
        val result = resolveContentState(isLoading = false, tasks = tasks)
        assertTrue(result is CompletedTasksContentState.Populated)
        assertEquals(tasks, (result as CompletedTasksContentState.Populated).tasks)
    }

    @Test
    fun `resolveContentState Populated contains correct task titles`() {
        val tasks =
            listOf(
                createTestTask(id = 1, title = "Buy groceries"),
                createTestTask(id = 2, title = "Walk the dog"),
                createTestTask(id = 3, title = "Read a book"),
            )
        val result = resolveContentState(isLoading = false, tasks = tasks)
        assertTrue(result is CompletedTasksContentState.Populated)
        val populated = result as CompletedTasksContentState.Populated
        assertEquals(
            listOf("Buy groceries", "Walk the dog", "Read a book"),
            populated.tasks.map { it.title },
        )
    }

    @Test
    fun `onDeleteTask callback receives correct task from populated state`() {
        val task1 = createTestTask(id = 1, title = "Task to delete")
        val task2 = createTestTask(id = 2, title = "Task to keep")
        val tasks = listOf(task1, task2)

        val result = resolveContentState(isLoading = false, tasks = tasks)
        assertTrue(result is CompletedTasksContentState.Populated)
        val populated = result as CompletedTasksContentState.Populated

        // Simulate onDeleteTask by verifying the task is accessible from the state
        var deletedTask: Task? = null
        val onDeleteTask: (Task) -> Unit = { deletedTask = it }
        onDeleteTask(populated.tasks[0])
        assertEquals(task1, deletedTask)
    }

    // --- Helper ---

    private fun createTestTask(
        id: Long = 0,
        title: String = "Test Task",
        priority: Priority = Priority.MEDIUM,
    ) = Task(
        id = id,
        title = title,
        isCompleted = true,
        createdAt = 1000L,
        updatedAt = 1000L,
        priority = priority,
    )
}
