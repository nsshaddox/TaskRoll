package com.nshaddox.randomtask

import com.nshaddox.randomtask.domain.model.AppTheme
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeResolutionTest {
    @Test
    fun `resolveTheme LIGHT returns false regardless of system dark mode`() {
        assertFalse(resolveTheme(AppTheme.LIGHT, isSystemDark = false))
        assertFalse(resolveTheme(AppTheme.LIGHT, isSystemDark = true))
    }

    @Test
    fun `resolveTheme DARK returns true regardless of system dark mode`() {
        assertTrue(resolveTheme(AppTheme.DARK, isSystemDark = false))
        assertTrue(resolveTheme(AppTheme.DARK, isSystemDark = true))
    }

    @Test
    fun `resolveTheme SYSTEM with isSystemDark true returns true`() {
        assertTrue(resolveTheme(AppTheme.SYSTEM, isSystemDark = true))
    }

    @Test
    fun `resolveTheme SYSTEM with isSystemDark false returns false`() {
        assertFalse(resolveTheme(AppTheme.SYSTEM, isSystemDark = false))
    }
}
