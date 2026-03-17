@file:Suppress("MagicNumber")

package com.nshaddox.randomtask.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.ThemeVariant
import org.junit.Assert.assertEquals
import org.junit.Test

class ThemedCardTest {
    // ── Card background color per variant ──

    @Test
    fun `cardBackgroundForVariant Obsidian HIGH returns obsidianCard`() {
        assertEquals(Color(0xFF1A1A1A), cardBackgroundForVariant(ThemeVariant.OBSIDIAN, Priority.HIGH))
    }

    @Test
    fun `cardBackgroundForVariant NeoBrutalist returns white`() {
        assertEquals(Color(0xFFFFFFFF), cardBackgroundForVariant(ThemeVariant.NEO_BRUTALIST, Priority.HIGH))
    }

    @Test
    fun `cardBackgroundForVariant Vapor HIGH returns priorityHighBg`() {
        assertEquals(Color(0xFF1F1520), cardBackgroundForVariant(ThemeVariant.VAPOR, Priority.HIGH))
    }

    @Test
    fun `cardBackgroundForVariant Vapor MEDIUM returns priorityMediumBg`() {
        assertEquals(Color(0xFF15182A), cardBackgroundForVariant(ThemeVariant.VAPOR, Priority.MEDIUM))
    }

    @Test
    fun `cardBackgroundForVariant Vapor LOW returns priorityLowBg`() {
        assertEquals(Color(0xFF101D1A), cardBackgroundForVariant(ThemeVariant.VAPOR, Priority.LOW))
    }

    // ── Card border width per variant ──

    @Test
    fun `cardBorderWidthForVariant Obsidian returns 0dp`() {
        assertEquals(0.dp, cardBorderWidthForVariant(ThemeVariant.OBSIDIAN))
    }

    @Test
    fun `cardBorderWidthForVariant NeoBrutalist returns 3dp`() {
        assertEquals(3.dp, cardBorderWidthForVariant(ThemeVariant.NEO_BRUTALIST))
    }

    @Test
    fun `cardBorderWidthForVariant Vapor returns 0dp`() {
        assertEquals(0.dp, cardBorderWidthForVariant(ThemeVariant.VAPOR))
    }

    // ── Card corner radius per variant ──

    @Test
    fun `cardCornerRadiusForVariant Obsidian returns 20dp`() {
        assertEquals(20.dp, cardCornerRadiusForVariant(ThemeVariant.OBSIDIAN))
    }

    @Test
    fun `cardCornerRadiusForVariant NeoBrutalist returns 12dp`() {
        assertEquals(12.dp, cardCornerRadiusForVariant(ThemeVariant.NEO_BRUTALIST))
    }

    @Test
    fun `cardCornerRadiusForVariant Vapor returns 22dp`() {
        assertEquals(22.dp, cardCornerRadiusForVariant(ThemeVariant.VAPOR))
    }
}
