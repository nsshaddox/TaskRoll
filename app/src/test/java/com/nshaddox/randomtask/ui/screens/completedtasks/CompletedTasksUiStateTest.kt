package com.nshaddox.randomtask.ui.screens.completedtasks

import com.nshaddox.randomtask.domain.model.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CompletedTasksUiStateTest {
    // ── Default value tests ──

    @Test
    fun `default state has empty task list`() {
        val state = CompletedTasksUiState()
        assertTrue(state.tasks.isEmpty())
    }

    @Test
    fun `default state is loading`() {
        val state = CompletedTasksUiState()
        assertTrue(state.isLoading)
    }

    @Test
    fun `default state has no error`() {
        val state = CompletedTasksUiState()
        assertNull(state.errorMessage)
    }

    // ── Copy transition tests ──

    @Test
    fun `copy to loaded state with tasks`() {
        val tasks =
            listOf(
                Task(
                    id = 1,
                    title = "Done Task",
                    isCompleted = true,
                    createdAt = 1000L,
                    updatedAt = 2000L,
                ),
            )
        val state = CompletedTasksUiState()
        val loaded = state.copy(tasks = tasks, isLoading = false)

        assertEquals(1, loaded.tasks.size)
        assertEquals("Done Task", loaded.tasks[0].title)
        assertFalse(loaded.isLoading)
        assertNull(loaded.errorMessage)
    }

    @Test
    fun `copy to error state`() {
        val state = CompletedTasksUiState()
        val error = state.copy(isLoading = false, errorMessage = "Failed to load")

        assertFalse(error.isLoading)
        assertEquals("Failed to load", error.errorMessage)
        assertTrue(error.tasks.isEmpty())
    }

    @Test
    fun `copy with tasks preserves other defaults`() {
        val task =
            Task(
                id = 1,
                title = "Task",
                isCompleted = true,
                createdAt = 1000L,
                updatedAt = 2000L,
            )
        val state = CompletedTasksUiState(tasks = listOf(task))

        assertEquals(1, state.tasks.size)
        assertTrue(state.isLoading)
        assertNull(state.errorMessage)
    }

    // ── Equality tests ──

    @Test
    fun `equality for identical default states`() {
        val state1 = CompletedTasksUiState()
        val state2 = CompletedTasksUiState()
        assertEquals(state1, state2)
    }

    @Test
    fun `equality for states with same tasks`() {
        val task =
            Task(
                id = 1,
                title = "Task",
                isCompleted = true,
                createdAt = 1000L,
                updatedAt = 2000L,
            )
        val state1 = CompletedTasksUiState(tasks = listOf(task), isLoading = false)
        val state2 = CompletedTasksUiState(tasks = listOf(task), isLoading = false)
        assertEquals(state1, state2)
    }

    // ── toString test ──

    @Test
    fun `toString contains field values`() {
        val state = CompletedTasksUiState(isLoading = false, errorMessage = "oops")
        val str = state.toString()
        assertTrue(str.contains("isLoading=false"))
        assertTrue(str.contains("errorMessage=oops"))
    }
}
