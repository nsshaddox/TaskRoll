@file:Suppress("MagicNumber")

package com.nshaddox.randomtask.ui.components

import androidx.compose.ui.graphics.Color
import com.nshaddox.randomtask.domain.model.ThemeVariant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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

    // ── wrapOnCheckedChange callback invocation ──

    @Test
    fun `wrapOnCheckedChange invokes original callback with true when haptic disabled`() {
        var callbackValue: Boolean? = null
        val wrapped =
            wrapOnCheckedChange(
                hapticEnabled = false,
                performHaptic = { },
                onCheckedChange = { callbackValue = it },
            )

        wrapped(true)

        assertEquals(true, callbackValue)
    }

    @Test
    fun `wrapOnCheckedChange invokes original callback with false when haptic disabled`() {
        var callbackValue: Boolean? = null
        val wrapped =
            wrapOnCheckedChange(
                hapticEnabled = false,
                performHaptic = { },
                onCheckedChange = { callbackValue = it },
            )

        wrapped(false)

        assertEquals(false, callbackValue)
    }

    @Test
    fun `wrapOnCheckedChange invokes original callback when haptic enabled`() {
        var callbackValue: Boolean? = null
        val wrapped =
            wrapOnCheckedChange(
                hapticEnabled = true,
                performHaptic = { },
                onCheckedChange = { callbackValue = it },
            )

        wrapped(true)

        assertEquals(true, callbackValue)
    }

    @Test
    fun `wrapOnCheckedChange does not perform haptic when haptic disabled`() {
        var hapticFired = false
        val wrapped =
            wrapOnCheckedChange(
                hapticEnabled = false,
                performHaptic = { hapticFired = true },
                onCheckedChange = { },
            )

        wrapped(true)

        assertFalse(hapticFired)
    }

    @Test
    fun `wrapOnCheckedChange performs haptic when haptic enabled`() {
        var hapticFired = false
        val wrapped =
            wrapOnCheckedChange(
                hapticEnabled = true,
                performHaptic = { hapticFired = true },
                onCheckedChange = { },
            )

        wrapped(true)

        assertTrue(hapticFired)
    }

    @Test
    fun `wrapOnCheckedChange does not invoke callback before being called`() {
        var callbackValue: Boolean? = null
        wrapOnCheckedChange(
            hapticEnabled = true,
            performHaptic = { },
            onCheckedChange = { callbackValue = it },
        )

        assertNull(callbackValue)
    }

    // ── Content description ──

    @Test
    fun `checkboxContentDescription with null returns null`() {
        assertNull(checkboxContentDescription(null))
    }

    @Test
    fun `checkboxContentDescription with non-null returns same string`() {
        assertEquals("Buy milk", checkboxContentDescription("Buy milk"))
    }
}
