@file:Suppress("MagicNumber")

package com.nshaddox.randomtask.ui.components

import androidx.compose.ui.graphics.Color
import com.nshaddox.randomtask.domain.model.ThemeVariant
import org.junit.Assert.assertEquals
import org.junit.Test

class ThemedCheckboxTest {
    // ── Checked fill color ──

    @Test
    fun `checkboxCheckedColor Obsidian returns teal primary`() {
        assertEquals(Color(0xFF00BFA5), checkboxCheckedColor(ThemeVariant.OBSIDIAN))
    }

    @Test
    fun `checkboxCheckedColor NeoBrutalist returns textPrimary dark`() {
        assertEquals(Color(0xFF1A1A1A), checkboxCheckedColor(ThemeVariant.NEO_BRUTALIST))
    }

    @Test
    fun `checkboxCheckedColor Vapor returns accentPink`() {
        assertEquals(Color(0xFFF9A8D4), checkboxCheckedColor(ThemeVariant.VAPOR))
    }

    // ── Unchecked stroke color ──

    @Test
    fun `checkboxUncheckedColor Obsidian returns textSecondary`() {
        assertEquals(Color(0xFF8A8A8A), checkboxUncheckedColor(ThemeVariant.OBSIDIAN))
    }

    @Test
    fun `checkboxUncheckedColor NeoBrutalist returns border color`() {
        assertEquals(Color(0xFF1A1A1A), checkboxUncheckedColor(ThemeVariant.NEO_BRUTALIST))
    }

    @Test
    fun `checkboxUncheckedColor Vapor returns accentPink dim`() {
        assertEquals(Color(0xFF9D4E7C), checkboxUncheckedColor(ThemeVariant.VAPOR))
    }

    // ── Checkmark color ──

    @Test
    fun `checkboxCheckmarkColor Obsidian returns onPrimary black`() {
        assertEquals(Color(0xFF000000), checkboxCheckmarkColor(ThemeVariant.OBSIDIAN))
    }

    @Test
    fun `checkboxCheckmarkColor NeoBrutalist returns white`() {
        assertEquals(Color(0xFFFFFFFF), checkboxCheckmarkColor(ThemeVariant.NEO_BRUTALIST))
    }

    @Test
    fun `checkboxCheckmarkColor Vapor returns background`() {
        assertEquals(Color(0xFF0E1118), checkboxCheckmarkColor(ThemeVariant.VAPOR))
    }
}
