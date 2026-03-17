@file:Suppress("MagicNumber")

package com.nshaddox.randomtask.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.ThemeVariant
import org.junit.Assert.assertEquals
import org.junit.Test

class ThemedPriorityBadgeTest {
    // ── Container color: Obsidian ──

    @Test
    fun `themedPriorityContainerColor Obsidian HIGH returns FF5252`() {
        assertEquals(
            Color(0xFFFF5252),
            themedPriorityContainerColor(Priority.HIGH, ThemeVariant.OBSIDIAN),
        )
    }

    @Test
    fun `themedPriorityContainerColor Obsidian MEDIUM returns FFB74D`() {
        assertEquals(
            Color(0xFFFFB74D),
            themedPriorityContainerColor(Priority.MEDIUM, ThemeVariant.OBSIDIAN),
        )
    }

    @Test
    fun `themedPriorityContainerColor Obsidian LOW returns 69F0AE`() {
        assertEquals(
            Color(0xFF69F0AE),
            themedPriorityContainerColor(Priority.LOW, ThemeVariant.OBSIDIAN),
        )
    }

    // ── Container color: Neo Brutalist ──

    @Test
    fun `themedPriorityContainerColor NeoBrutalist HIGH returns FF2D78`() {
        assertEquals(
            Color(0xFFFF2D78),
            themedPriorityContainerColor(Priority.HIGH, ThemeVariant.NEO_BRUTALIST),
        )
    }

    @Test
    fun `themedPriorityContainerColor NeoBrutalist MEDIUM returns FFE500`() {
        assertEquals(
            Color(0xFFFFE500),
            themedPriorityContainerColor(Priority.MEDIUM, ThemeVariant.NEO_BRUTALIST),
        )
    }

    @Test
    fun `themedPriorityContainerColor NeoBrutalist LOW returns 22C55E`() {
        assertEquals(
            Color(0xFF22C55E),
            themedPriorityContainerColor(Priority.LOW, ThemeVariant.NEO_BRUTALIST),
        )
    }

    // ── Container color: Vapor ──

    @Test
    fun `themedPriorityContainerColor Vapor HIGH returns pink at 10pct alpha`() {
        assertEquals(
            Color(0xFFF9A8D4).copy(alpha = 0.1f),
            themedPriorityContainerColor(Priority.HIGH, ThemeVariant.VAPOR),
        )
    }

    @Test
    fun `themedPriorityContainerColor Vapor MEDIUM returns indigo at 10pct alpha`() {
        assertEquals(
            Color(0xFFA5B4FC).copy(alpha = 0.1f),
            themedPriorityContainerColor(Priority.MEDIUM, ThemeVariant.VAPOR),
        )
    }

    @Test
    fun `themedPriorityContainerColor Vapor LOW returns teal at 10pct alpha`() {
        assertEquals(
            Color(0xFF5EEAD4).copy(alpha = 0.1f),
            themedPriorityContainerColor(Priority.LOW, ThemeVariant.VAPOR),
        )
    }

    // ── On-container color: Obsidian (not used for dot, but tested for completeness) ──

    @Test
    fun `themedPriorityOnContainerColor Obsidian HIGH returns FF5252`() {
        assertEquals(
            Color(0xFFFF5252),
            themedPriorityOnContainerColor(Priority.HIGH, ThemeVariant.OBSIDIAN),
        )
    }

    // ── On-container color: Neo Brutalist ──

    @Test
    fun `themedPriorityOnContainerColor NeoBrutalist HIGH returns white`() {
        assertEquals(
            Color(0xFFFFFFFF),
            themedPriorityOnContainerColor(Priority.HIGH, ThemeVariant.NEO_BRUTALIST),
        )
    }

    @Test
    fun `themedPriorityOnContainerColor NeoBrutalist MEDIUM returns dark`() {
        assertEquals(
            Color(0xFF1A1A1A),
            themedPriorityOnContainerColor(Priority.MEDIUM, ThemeVariant.NEO_BRUTALIST),
        )
    }

    @Test
    fun `themedPriorityOnContainerColor NeoBrutalist LOW returns white`() {
        assertEquals(
            Color(0xFFFFFFFF),
            themedPriorityOnContainerColor(Priority.LOW, ThemeVariant.NEO_BRUTALIST),
        )
    }

    // ── On-container color: Vapor ──

    @Test
    fun `themedPriorityOnContainerColor Vapor HIGH returns accentPink`() {
        assertEquals(
            Color(0xFFF9A8D4),
            themedPriorityOnContainerColor(Priority.HIGH, ThemeVariant.VAPOR),
        )
    }

    @Test
    fun `themedPriorityOnContainerColor Vapor MEDIUM returns accentIndigo`() {
        assertEquals(
            Color(0xFFA5B4FC),
            themedPriorityOnContainerColor(Priority.MEDIUM, ThemeVariant.VAPOR),
        )
    }

    @Test
    fun `themedPriorityOnContainerColor Vapor LOW returns accentTeal`() {
        assertEquals(
            Color(0xFF5EEAD4),
            themedPriorityOnContainerColor(Priority.LOW, ThemeVariant.VAPOR),
        )
    }

    // ── Badge border width ──

    @Test
    fun `themedBadgeBorderWidth NeoBrutalist returns 2dp`() {
        assertEquals(2.dp, themedBadgeBorderWidth(ThemeVariant.NEO_BRUTALIST))
    }

    @Test
    fun `themedBadgeBorderWidth Obsidian returns 0dp`() {
        assertEquals(0.dp, themedBadgeBorderWidth(ThemeVariant.OBSIDIAN))
    }

    @Test
    fun `themedBadgeBorderWidth Vapor returns 0dp`() {
        assertEquals(0.dp, themedBadgeBorderWidth(ThemeVariant.VAPOR))
    }
}
