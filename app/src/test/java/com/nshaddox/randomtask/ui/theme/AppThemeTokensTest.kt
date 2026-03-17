@file:Suppress("MagicNumber")

package com.nshaddox.randomtask.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppThemeTokensTest {
    // ── Obsidian tokens ──

    @Test
    fun `ObsidianTokens borderWidth is 0dp`() {
        assertEquals(0.dp, ObsidianTokens.borderWidth)
    }

    @Test
    fun `ObsidianTokens shadowOffset is 0dp`() {
        assertEquals(0.dp, ObsidianTokens.shadowOffset)
    }

    @Test
    fun `ObsidianTokens useUppercaseTitles is false`() {
        assertFalse(ObsidianTokens.useUppercaseTitles)
    }

    @Test
    fun `ObsidianTokens useSolidShadow is false`() {
        assertFalse(ObsidianTokens.useSolidShadow)
    }

    @Test
    fun `ObsidianTokens priorityHigh is FF5252`() {
        assertEquals(Color(0xFFFF5252), ObsidianTokens.priorityHigh)
    }

    @Test
    fun `ObsidianTokens priorityMedium is FFB74D`() {
        assertEquals(Color(0xFFFFB74D), ObsidianTokens.priorityMedium)
    }

    @Test
    fun `ObsidianTokens priorityLow is 69F0AE`() {
        assertEquals(Color(0xFF69F0AE), ObsidianTokens.priorityLow)
    }

    // ── Neo Brutalist tokens ──

    @Test
    fun `NeoBrutalistTokens borderWidth is 3dp`() {
        assertEquals(3.dp, NeoBrutalistTokens.borderWidth)
    }

    @Test
    fun `NeoBrutalistTokens shadowOffset is 4dp`() {
        assertEquals(4.dp, NeoBrutalistTokens.shadowOffset)
    }

    @Test
    fun `NeoBrutalistTokens useUppercaseTitles is true`() {
        assertTrue(NeoBrutalistTokens.useUppercaseTitles)
    }

    @Test
    fun `NeoBrutalistTokens useSolidShadow is true`() {
        assertTrue(NeoBrutalistTokens.useSolidShadow)
    }

    @Test
    fun `NeoBrutalistTokens priorityHigh is FF2D78`() {
        assertEquals(Color(0xFFFF2D78), NeoBrutalistTokens.priorityHigh)
    }

    @Test
    fun `NeoBrutalistTokens priorityMedium is FFE500`() {
        assertEquals(Color(0xFFFFE500), NeoBrutalistTokens.priorityMedium)
    }

    @Test
    fun `NeoBrutalistTokens priorityLow is 22C55E`() {
        assertEquals(Color(0xFF22C55E), NeoBrutalistTokens.priorityLow)
    }

    // ── Vapor tokens ──

    @Test
    fun `VaporTokens borderWidth is 0dp`() {
        assertEquals(0.dp, VaporTokens.borderWidth)
    }

    @Test
    fun `VaporTokens shadowOffset is 0dp`() {
        assertEquals(0.dp, VaporTokens.shadowOffset)
    }

    @Test
    fun `VaporTokens useUppercaseTitles is false`() {
        assertFalse(VaporTokens.useUppercaseTitles)
    }

    @Test
    fun `VaporTokens useSolidShadow is false`() {
        assertFalse(VaporTokens.useSolidShadow)
    }

    @Test
    fun `VaporTokens priorityHigh is F9A8D4`() {
        assertEquals(Color(0xFFF9A8D4), VaporTokens.priorityHigh)
    }

    @Test
    fun `VaporTokens priorityMedium is A5B4FC`() {
        assertEquals(Color(0xFFA5B4FC), VaporTokens.priorityMedium)
    }

    @Test
    fun `VaporTokens priorityLow is 5EEAD4`() {
        assertEquals(Color(0xFF5EEAD4), VaporTokens.priorityLow)
    }

    // ── Vapor priority background tokens ──

    @Test
    fun `VaporTokens priorityHighBg is 1F1520`() {
        assertEquals(Color(0xFF1F1520), VaporTokens.priorityHighBg)
    }

    @Test
    fun `VaporTokens priorityMediumBg is 15182A`() {
        assertEquals(Color(0xFF15182A), VaporTokens.priorityMediumBg)
    }

    @Test
    fun `VaporTokens priorityLowBg is 101D1A`() {
        assertEquals(Color(0xFF101D1A), VaporTokens.priorityLowBg)
    }

    // ── Cross-variant distinctness ──

    @Test
    fun `all three token instances have distinct priorityHigh colors`() {
        val colors =
            setOf(
                ObsidianTokens.priorityHigh,
                NeoBrutalistTokens.priorityHigh,
                VaporTokens.priorityHigh,
            )
        assertEquals(3, colors.size)
    }

    @Test
    fun `all three token instances have distinct priorityLow colors`() {
        val colors =
            setOf(
                ObsidianTokens.priorityLow,
                NeoBrutalistTokens.priorityLow,
                VaporTokens.priorityLow,
            )
        assertEquals(3, colors.size)
    }
}
