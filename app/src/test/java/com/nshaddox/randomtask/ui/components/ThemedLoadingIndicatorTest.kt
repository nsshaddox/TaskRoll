@file:Suppress("MagicNumber")

package com.nshaddox.randomtask.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nshaddox.randomtask.domain.model.ThemeVariant
import org.junit.Assert.assertEquals
import org.junit.Test

class ThemedLoadingIndicatorTest {
    // ── Loading indicator color ──

    @Test
    fun `loadingIndicatorColorForVariant Obsidian returns teal primary`() {
        assertEquals(Color(0xFF00BFA5), loadingIndicatorColorForVariant(ThemeVariant.OBSIDIAN))
    }

    @Test
    fun `loadingIndicatorColorForVariant NeoBrutalist returns accentPink`() {
        assertEquals(Color(0xFFFF2D78), loadingIndicatorColorForVariant(ThemeVariant.NEO_BRUTALIST))
    }

    @Test
    fun `loadingIndicatorColorForVariant Vapor returns accentTeal`() {
        assertEquals(Color(0xFF5EEAD4), loadingIndicatorColorForVariant(ThemeVariant.VAPOR))
    }

    // ── Loading indicator stroke width ──

    @Test
    fun `loadingIndicatorStrokeWidthForVariant Obsidian returns 4dp`() {
        assertEquals(4.dp, loadingIndicatorStrokeWidthForVariant(ThemeVariant.OBSIDIAN))
    }

    @Test
    fun `loadingIndicatorStrokeWidthForVariant NeoBrutalist returns 4dp`() {
        assertEquals(4.dp, loadingIndicatorStrokeWidthForVariant(ThemeVariant.NEO_BRUTALIST))
    }

    @Test
    fun `loadingIndicatorStrokeWidthForVariant Vapor returns 2dp`() {
        assertEquals(2.dp, loadingIndicatorStrokeWidthForVariant(ThemeVariant.VAPOR))
    }
}
