@file:Suppress("MagicNumber")

package com.nshaddox.randomtask.ui.components

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.nshaddox.randomtask.domain.model.ThemeVariant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemedEmptyStateContentTest {
    // ── emptyStateTitleFontSize ──

    @Test
    fun `emptyStateTitleFontSize Obsidian returns 22sp`() {
        assertEquals(22.sp, emptyStateTitleFontSize(ThemeVariant.OBSIDIAN))
    }

    @Test
    fun `emptyStateTitleFontSize NeoBrutalist returns 28sp`() {
        assertEquals(28.sp, emptyStateTitleFontSize(ThemeVariant.NEO_BRUTALIST))
    }

    @Test
    fun `emptyStateTitleFontSize Vapor returns 24sp`() {
        assertEquals(24.sp, emptyStateTitleFontSize(ThemeVariant.VAPOR))
    }

    // ── emptyStateTitleFontWeight ──

    @Test
    fun `emptyStateTitleFontWeight Obsidian returns SemiBold`() {
        assertEquals(FontWeight.SemiBold, emptyStateTitleFontWeight(ThemeVariant.OBSIDIAN))
    }

    @Test
    fun `emptyStateTitleFontWeight NeoBrutalist returns Black`() {
        assertEquals(FontWeight.Black, emptyStateTitleFontWeight(ThemeVariant.NEO_BRUTALIST))
    }

    @Test
    fun `emptyStateTitleFontWeight Vapor returns Light`() {
        assertEquals(FontWeight.Light, emptyStateTitleFontWeight(ThemeVariant.VAPOR))
    }

    // ── emptyStateBodyAlpha ──

    @Test
    fun `emptyStateBodyAlpha Obsidian returns 0_6f`() {
        assertEquals(0.6f, emptyStateBodyAlpha(ThemeVariant.OBSIDIAN), 0.001f)
    }

    @Test
    fun `emptyStateBodyAlpha NeoBrutalist returns 1_0f`() {
        assertEquals(1.0f, emptyStateBodyAlpha(ThemeVariant.NEO_BRUTALIST), 0.001f)
    }

    @Test
    fun `emptyStateBodyAlpha Vapor returns 0_4f`() {
        assertEquals(0.4f, emptyStateBodyAlpha(ThemeVariant.VAPOR), 0.001f)
    }

    // ── emptyStateUsesYellowBadge ──

    @Test
    fun `emptyStateUsesYellowBadge NeoBrutalist returns true`() {
        assertTrue(emptyStateUsesYellowBadge(ThemeVariant.NEO_BRUTALIST))
    }

    @Test
    fun `emptyStateUsesYellowBadge Obsidian returns false`() {
        assertFalse(emptyStateUsesYellowBadge(ThemeVariant.OBSIDIAN))
    }

    @Test
    fun `emptyStateUsesYellowBadge Vapor returns false`() {
        assertFalse(emptyStateUsesYellowBadge(ThemeVariant.VAPOR))
    }
}
