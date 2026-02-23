package com.nshaddox.randomtask.ui.screens.tasklist

import com.nshaddox.randomtask.domain.model.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskListUiStateTest {

    @Test
    fun `default state has empty task list`() {
        val state = TaskListUiState()
        assertTrue(state.tasks.isEmpty())
    }

    @Test
    fun `default state is not loading`() {
        val state = TaskListUiState()
        assertFalse(state.isLoading)
    }

    @Test
    fun `default state has no error`() {
        val state = TaskListUiState()
        assertNull(state.error)
    }

    @Test
    fun `default state has add dialog hidden`() {
        val state = TaskListUiState()
        assertFalse(state.showAddDialog)
    }

    @Test
    fun `copy to loading state`() {
        val state = TaskListUiState()
        val loading = state.copy(isLoading = true)
        assertTrue(loading.isLoading)
        assertTrue(loading.tasks.isEmpty())
        assertNull(loading.error)
        assertFalse(loading.showAddDialog)
    }

    @Test
    fun `copy to error state`() {
        val state = TaskListUiState(isLoading = true)
        val error = state.copy(isLoading = false, error = "Something went wrong")
        assertFalse(error.isLoading)
        assertEquals("Something went wrong", error.error)
    }

    @Test
    fun `copy to show add dialog`() {
        val state = TaskListUiState()
        val withDialog = state.copy(showAddDialog = true)
        assertTrue(withDialog.showAddDialog)
    }

    @Test
    fun `copy with tasks`() {
        val tasks = listOf(
            Task(id = 1, title = "Task 1", createdAt = 1000L, updatedAt = 1000L),
            Task(id = 2, title = "Task 2", createdAt = 2000L, updatedAt = 2000L)
        )
        val state = TaskListUiState().copy(tasks = tasks)
        assertEquals(2, state.tasks.size)
        assertEquals("Task 1", state.tasks[0].title)
        assertEquals("Task 2", state.tasks[1].title)
    }

    @Test
    fun `equality for identical states`() {
        val state1 = TaskListUiState()
        val state2 = TaskListUiState()
        assertEquals(state1, state2)
    }

    @Test
    fun `equality for states with same tasks`() {
        val task = Task(id = 1, title = "Task", createdAt = 1000L, updatedAt = 1000L)
        val state1 = TaskListUiState(tasks = listOf(task), isLoading = true)
        val state2 = TaskListUiState(tasks = listOf(task), isLoading = true)
        assertEquals(state1, state2)
    }

    @Test
    fun `toString contains field values`() {
        val state = TaskListUiState(isLoading = true, error = "fail")
        val str = state.toString()
        assertTrue(str.contains("isLoading=true"))
        assertTrue(str.contains("error=fail"))
    }
}
