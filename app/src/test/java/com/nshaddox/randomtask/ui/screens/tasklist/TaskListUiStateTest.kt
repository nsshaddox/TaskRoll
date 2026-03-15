package com.nshaddox.randomtask.ui.screens.tasklist

import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.SortOrder
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
    fun `default state is loading`() {
        val state = TaskListUiState()
        assertTrue(state.isLoading)
    }

    @Test
    fun `default state has no error`() {
        val state = TaskListUiState()
        assertNull(state.errorMessage)
    }

    @Test
    fun `default state has add dialog hidden`() {
        val state = TaskListUiState()
        assertFalse(state.isAddDialogVisible)
    }

    @Test
    fun `copy to loading state`() {
        val state = TaskListUiState()
        val loading = state.copy(isLoading = true)
        assertTrue(loading.isLoading)
        assertTrue(loading.tasks.isEmpty())
        assertNull(loading.errorMessage)
        assertFalse(loading.isAddDialogVisible)
    }

    @Test
    fun `copy to error state`() {
        val state = TaskListUiState(isLoading = true)
        val error = state.copy(isLoading = false, errorMessage = "Something went wrong")
        assertFalse(error.isLoading)
        assertEquals("Something went wrong", error.errorMessage)
    }

    @Test
    fun `copy to show add dialog`() {
        val state = TaskListUiState()
        val withDialog = state.copy(isAddDialogVisible = true)
        assertTrue(withDialog.isAddDialogVisible)
    }

    @Test
    fun `copy with tasks`() {
        val tasks =
            listOf(
                Task(id = 1, title = "Task 1", createdAt = 1000L, updatedAt = 1000L),
                Task(id = 2, title = "Task 2", createdAt = 2000L, updatedAt = 2000L),
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
        val state = TaskListUiState(isLoading = true, errorMessage = "fail")
        val str = state.toString()
        assertTrue(str.contains("isLoading=true"))
        assertTrue(str.contains("errorMessage=fail"))
    }

    // ── New field default tests ──

    @Test
    fun `default state has empty searchQuery`() {
        val state = TaskListUiState()
        assertEquals("", state.searchQuery)
    }

    @Test
    fun `default state has null filterPriority`() {
        val state = TaskListUiState()
        assertNull(state.filterPriority)
    }

    @Test
    fun `default state has null filterCategory`() {
        val state = TaskListUiState()
        assertNull(state.filterCategory)
    }

    @Test
    fun `default state has sortOrder CREATED_DATE_DESC`() {
        val state = TaskListUiState()
        assertEquals(SortOrder.CREATED_DATE_DESC, state.sortOrder)
    }

    @Test
    fun `default state has empty availableCategories`() {
        val state = TaskListUiState()
        assertTrue(state.availableCategories.isEmpty())
    }

    // ── New field copy-transition tests ──

    @Test
    fun `copy with searchQuery`() {
        val state = TaskListUiState()
        val withSearch = state.copy(searchQuery = "buy")
        assertEquals("buy", withSearch.searchQuery)
    }

    @Test
    fun `copy with filterPriority`() {
        val state = TaskListUiState()
        val filtered = state.copy(filterPriority = Priority.HIGH)
        assertEquals(Priority.HIGH, filtered.filterPriority)
    }

    @Test
    fun `copy with filterCategory`() {
        val state = TaskListUiState()
        val filtered = state.copy(filterCategory = "Work")
        assertEquals("Work", filtered.filterCategory)
    }

    @Test
    fun `copy with sortOrder`() {
        val state = TaskListUiState()
        val sorted = state.copy(sortOrder = SortOrder.TITLE_ASC)
        assertEquals(SortOrder.TITLE_ASC, sorted.sortOrder)
    }

    @Test
    fun `copy with availableCategories`() {
        val state = TaskListUiState()
        val categories = listOf("Work", "Personal", "Shopping")
        val withCategories = state.copy(availableCategories = categories)
        assertEquals(3, withCategories.availableCategories.size)
        assertEquals("Work", withCategories.availableCategories[0])
        assertEquals("Personal", withCategories.availableCategories[1])
        assertEquals("Shopping", withCategories.availableCategories[2])
    }

    @Test
    fun `copy preserves new field defaults when not overridden`() {
        val state = TaskListUiState()
        val copied = state.copy(isLoading = false)
        assertEquals("", copied.searchQuery)
        assertNull(copied.filterPriority)
        assertNull(copied.filterCategory)
        assertEquals(SortOrder.CREATED_DATE_DESC, copied.sortOrder)
        assertTrue(copied.availableCategories.isEmpty())
    }

    @Test
    fun `equality for states with same new fields`() {
        val state1 = TaskListUiState(searchQuery = "test", sortOrder = SortOrder.DUE_DATE_ASC)
        val state2 = TaskListUiState(searchQuery = "test", sortOrder = SortOrder.DUE_DATE_ASC)
        assertEquals(state1, state2)
    }

    @Test
    fun `toString contains new field values`() {
        val state = TaskListUiState(searchQuery = "groceries", sortOrder = SortOrder.PRIORITY_DESC)
        val str = state.toString()
        assertTrue(str.contains("searchQuery=groceries"))
        assertTrue(str.contains("sortOrder=PRIORITY_DESC"))
    }

    // ── Edit dialog state tests ──

    @Test
    fun `default state has edit dialog hidden`() {
        val state = TaskListUiState()
        assertFalse(state.isEditDialogVisible)
    }

    @Test
    fun `default state has null editingTask`() {
        val state = TaskListUiState()
        assertNull(state.editingTask)
    }

    @Test
    fun `copy with isEditDialogVisible true`() {
        val state = TaskListUiState()
        val withDialog = state.copy(isEditDialogVisible = true)
        assertTrue(withDialog.isEditDialogVisible)
    }

    @Test
    fun `copy with editingTask`() {
        val uiModel =
            TaskUiModel(
                id = 1L,
                title = "Test",
                description = null,
                isCompleted = false,
                createdAt = "Jan 1, 2025",
                updatedAt = "Jan 1, 2025",
            )
        val state = TaskListUiState()
        val withTask = state.copy(editingTask = uiModel)
        assertEquals(uiModel, withTask.editingTask)
    }

    @Test
    fun `copy preserves edit dialog defaults when not overridden`() {
        val state = TaskListUiState()
        val copied = state.copy(isLoading = false)
        assertFalse(copied.isEditDialogVisible)
        assertNull(copied.editingTask)
    }

    // ── Pending delete task tests ──

    @Test
    fun `default state has null pendingDeleteTask`() {
        val state = TaskListUiState()
        assertNull(state.pendingDeleteTask)
    }

    @Test
    fun `copy with pendingDeleteTask`() {
        val task = Task(id = 1, title = "Deleted", createdAt = 1000L, updatedAt = 1000L)
        val state = TaskListUiState()
        val withPending = state.copy(pendingDeleteTask = task)
        assertEquals(task, withPending.pendingDeleteTask)
    }

    @Test
    fun `copy preserves pendingDeleteTask default when not overridden`() {
        val state = TaskListUiState()
        val copied = state.copy(isLoading = false)
        assertNull(copied.pendingDeleteTask)
    }
}
