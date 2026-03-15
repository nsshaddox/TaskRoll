package com.nshaddox.randomtask.ui.screens.randomtask

import com.nshaddox.randomtask.domain.model.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RandomTaskUiStateTest {
    @Test
    fun `default state has null currentTask, isLoading false, null error, noTasksAvailable false`() {
        val state = RandomTaskUiState()

        assertNull(state.currentTask)
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertFalse(state.noTasksAvailable)
    }

    @Test
    fun `copy with isLoading true produces expected modified state`() {
        val original = RandomTaskUiState()
        val copied = original.copy(isLoading = true)

        assertEquals(true, copied.isLoading)
        assertNull(copied.currentTask)
        assertNull(copied.error)
        assertFalse(copied.noTasksAvailable)
    }

    @Test
    fun `copy with currentTask set produces expected state`() {
        val task =
            Task(
                id = 1L,
                title = "Test Task",
                description = "A test task",
                isCompleted = false,
                createdAt = 1000L,
                updatedAt = 2000L,
            )
        val state = RandomTaskUiState().copy(currentTask = task)

        assertEquals(task, state.currentTask)
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertFalse(state.noTasksAvailable)
    }

    @Test
    fun `two instances with same values are equal`() {
        val state1 =
            RandomTaskUiState(
                currentTask = null,
                isLoading = true,
                error = "Something went wrong",
                noTasksAvailable = false,
            )
        val state2 =
            RandomTaskUiState(
                currentTask = null,
                isLoading = true,
                error = "Something went wrong",
                noTasksAvailable = false,
            )

        assertEquals(state1, state2)
    }

    @Test
    fun `two instances with different isLoading are not equal`() {
        val state1 = RandomTaskUiState(isLoading = true)
        val state2 = RandomTaskUiState(isLoading = false)

        assertNotEquals(state1, state2)
    }

    @Test
    fun `two instances with different error are not equal`() {
        val state1 = RandomTaskUiState(error = "Error A")
        val state2 = RandomTaskUiState(error = "Error B")

        assertNotEquals(state1, state2)
    }

    @Test
    fun `two instances with different noTasksAvailable are not equal`() {
        val state1 = RandomTaskUiState(noTasksAvailable = true)
        val state2 = RandomTaskUiState(noTasksAvailable = false)

        assertNotEquals(state1, state2)
    }
}
