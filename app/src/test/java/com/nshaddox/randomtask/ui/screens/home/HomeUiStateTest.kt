package com.nshaddox.randomtask.ui.screens.home

import com.nshaddox.randomtask.domain.model.TaskMetrics
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class HomeUiStateTest {
    @Test
    fun `default state has expected values`() {
        val state = HomeUiState()
        assertNull(state.currentTask)
        assertFalse(state.useWeightedRandom)
        assertEquals(TaskMetrics(), state.metrics)
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertFalse(state.noTasksAvailable)
        assertFalse(state.taskCompleted)
    }

    @Test
    fun `copy updates fields correctly`() {
        val state = HomeUiState()
        val updated = state.copy(isLoading = true, useWeightedRandom = true)
        assertEquals(true, updated.isLoading)
        assertEquals(true, updated.useWeightedRandom)
        assertNull(updated.currentTask)
    }
}
