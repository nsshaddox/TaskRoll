package com.nshaddox.randomtask.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class AppThemeTest {
    @Test
    fun `AppTheme has three values`() {
        val values = AppTheme.entries
        assertEquals(3, values.size)
    }

    @Test
    fun `AppTheme values are in expected order`() {
        val values = AppTheme.entries
        assertEquals(AppTheme.LIGHT, values[0])
        assertEquals(AppTheme.DARK, values[1])
        assertEquals(AppTheme.SYSTEM, values[2])
    }

    @Test
    fun `valueOf LIGHT returns LIGHT`() {
        assertEquals(AppTheme.LIGHT, AppTheme.valueOf("LIGHT"))
    }

    @Test
    fun `valueOf DARK returns DARK`() {
        assertEquals(AppTheme.DARK, AppTheme.valueOf("DARK"))
    }

    @Test
    fun `valueOf SYSTEM returns SYSTEM`() {
        assertEquals(AppTheme.SYSTEM, AppTheme.valueOf("SYSTEM"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `valueOf invalid name throws IllegalArgumentException`() {
        AppTheme.valueOf("INVALID")
    }
}
