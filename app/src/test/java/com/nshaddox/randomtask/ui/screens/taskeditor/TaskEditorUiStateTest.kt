package com.nshaddox.randomtask.ui.screens.taskeditor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskEditorUiStateTest {
    @Test
    fun `default state has empty title and loading true`() {
        val state = TaskEditorUiState()
        assertEquals("", state.taskTitle)
        assertTrue(state.isLoading)
    }

    @Test
    fun `default state has no error`() {
        val state = TaskEditorUiState()
        assertNull(state.errorMessage)
        assertNull(state.errorResId)
    }

    @Test
    fun `default state is not saved`() {
        val state = TaskEditorUiState()
        assertFalse(state.isSaved)
    }

    @Test
    fun `copy with taskTitle`() {
        val state = TaskEditorUiState()
        val updated = state.copy(taskTitle = "New Title")
        assertEquals("New Title", updated.taskTitle)
    }

    @Test
    fun `copy with errorResId`() {
        val state = TaskEditorUiState()
        val withError = state.copy(errorResId = 42)
        assertEquals(42, withError.errorResId)
    }

    @Test
    fun `copy preserves defaults when not overridden`() {
        val state = TaskEditorUiState()
        val copied = state.copy(isLoading = false)
        assertFalse(copied.isLoading)
        assertEquals("", copied.taskTitle)
        assertNull(copied.errorMessage)
        assertNull(copied.errorResId)
        assertFalse(copied.isSaved)
    }

    @Test
    fun `equality for identical states`() {
        val state1 = TaskEditorUiState()
        val state2 = TaskEditorUiState()
        assertEquals(state1, state2)
    }
}
