package com.nshaddox.randomtask.ui.screens.completedtasks

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CompletedTasksScreenTest {
    @Test
    fun `formatCompletedDate formats epoch millis to readable date string`() {
        // 2023-11-14T22:13:20Z in epoch millis
        val epochMillis = 1_700_000_000_000L
        val formatted = formatCompletedDate(epochMillis)
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
        val formatted = formatCompletedDate(epochMillis)
        assertTrue(
            "Expected formatted date to contain 'Nov', got: $formatted",
            formatted.contains("Nov"),
        )
    }

    @Test
    fun `formatCompletedDate returns non-empty string for zero epoch`() {
        val formatted = formatCompletedDate(0L)
        assertTrue("Expected non-empty formatted date", formatted.isNotEmpty())
    }

    @Test
    fun `formatCompletedDate returns consistent results for same input`() {
        val epochMillis = 1_700_000_000_000L
        val first = formatCompletedDate(epochMillis)
        val second = formatCompletedDate(epochMillis)
        assertEquals(first, second)
    }
}
