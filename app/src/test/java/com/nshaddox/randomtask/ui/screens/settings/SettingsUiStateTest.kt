package com.nshaddox.randomtask.ui.screens.settings

import com.nshaddox.randomtask.domain.model.AppTheme
import com.nshaddox.randomtask.domain.model.SortOrder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsUiStateTest {
    // ── Default value tests ──

    @Test
    fun `default state has appTheme SYSTEM`() {
        val state = SettingsUiState()
        assertEquals(AppTheme.SYSTEM, state.appTheme)
    }

    @Test
    fun `default state has sortOrder CREATED_DATE_DESC`() {
        val state = SettingsUiState()
        assertEquals(SortOrder.CREATED_DATE_DESC, state.sortOrder)
    }

    @Test
    fun `default state is not loading`() {
        val state = SettingsUiState()
        assertFalse(state.isLoading)
    }

    @Test
    fun `default state has no error`() {
        val state = SettingsUiState()
        assertNull(state.errorMessage)
    }

    // ── Copy transition tests ──

    @Test
    fun `copy with appTheme LIGHT`() {
        val state = SettingsUiState()
        val updated = state.copy(appTheme = AppTheme.LIGHT)
        assertEquals(AppTheme.LIGHT, updated.appTheme)
    }

    @Test
    fun `copy with appTheme DARK`() {
        val state = SettingsUiState()
        val updated = state.copy(appTheme = AppTheme.DARK)
        assertEquals(AppTheme.DARK, updated.appTheme)
    }

    @Test
    fun `copy with sortOrder DUE_DATE_ASC`() {
        val state = SettingsUiState()
        val updated = state.copy(sortOrder = SortOrder.DUE_DATE_ASC)
        assertEquals(SortOrder.DUE_DATE_ASC, updated.sortOrder)
    }

    @Test
    fun `copy with sortOrder PRIORITY_DESC`() {
        val state = SettingsUiState()
        val updated = state.copy(sortOrder = SortOrder.PRIORITY_DESC)
        assertEquals(SortOrder.PRIORITY_DESC, updated.sortOrder)
    }

    @Test
    fun `copy with sortOrder TITLE_ASC`() {
        val state = SettingsUiState()
        val updated = state.copy(sortOrder = SortOrder.TITLE_ASC)
        assertEquals(SortOrder.TITLE_ASC, updated.sortOrder)
    }

    @Test
    fun `copy with isLoading true`() {
        val state = SettingsUiState()
        val updated = state.copy(isLoading = true)
        assertTrue(updated.isLoading)
    }

    @Test
    fun `copy with errorMessage`() {
        val state = SettingsUiState()
        val updated = state.copy(errorMessage = "Save failed")
        assertEquals("Save failed", updated.errorMessage)
    }

    @Test
    fun `copy preserves other defaults`() {
        val state = SettingsUiState()
        val updated = state.copy(appTheme = AppTheme.DARK)
        assertEquals(AppTheme.DARK, updated.appTheme)
        assertEquals(SortOrder.CREATED_DATE_DESC, updated.sortOrder)
        assertFalse(updated.isLoading)
        assertNull(updated.errorMessage)
    }

    // ── Equality tests ──

    @Test
    fun `equality for identical default states`() {
        val state1 = SettingsUiState()
        val state2 = SettingsUiState()
        assertEquals(state1, state2)
    }

    @Test
    fun `equality for states with same values`() {
        val state1 = SettingsUiState(appTheme = AppTheme.DARK, sortOrder = SortOrder.TITLE_ASC)
        val state2 = SettingsUiState(appTheme = AppTheme.DARK, sortOrder = SortOrder.TITLE_ASC)
        assertEquals(state1, state2)
    }

    // ── toString test ──

    @Test
    fun `toString contains field values`() {
        val state = SettingsUiState(appTheme = AppTheme.LIGHT, sortOrder = SortOrder.PRIORITY_DESC)
        val str = state.toString()
        assertTrue(str.contains("appTheme=LIGHT"))
        assertTrue(str.contains("sortOrder=PRIORITY_DESC"))
    }
}
