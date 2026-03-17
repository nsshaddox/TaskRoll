package com.nshaddox.randomtask.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeVariantTest {
    @Test
    fun `ThemeVariant has three values`() {
        val values = ThemeVariant.entries
        assertEquals(3, values.size)
    }

    @Test
    fun `ThemeVariant values are in expected order`() {
        val values = ThemeVariant.entries
        assertEquals(ThemeVariant.OBSIDIAN, values[0])
        assertEquals(ThemeVariant.NEO_BRUTALIST, values[1])
        assertEquals(ThemeVariant.VAPOR, values[2])
    }

    @Test
    fun `valueOf OBSIDIAN returns OBSIDIAN`() {
        assertEquals(ThemeVariant.OBSIDIAN, ThemeVariant.valueOf("OBSIDIAN"))
    }

    @Test
    fun `valueOf NEO_BRUTALIST returns NEO_BRUTALIST`() {
        assertEquals(ThemeVariant.NEO_BRUTALIST, ThemeVariant.valueOf("NEO_BRUTALIST"))
    }

    @Test
    fun `valueOf VAPOR returns VAPOR`() {
        assertEquals(ThemeVariant.VAPOR, ThemeVariant.valueOf("VAPOR"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `valueOf invalid name throws IllegalArgumentException`() {
        ThemeVariant.valueOf("INVALID")
    }
}
