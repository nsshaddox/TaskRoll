@file:Suppress("MagicNumber")

package com.nshaddox.randomtask.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nshaddox.randomtask.domain.model.ThemeVariant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemedFABTest {
    // ── FAB container color ──

    @Test
    fun `fabContainerColor Obsidian returns teal primary`() {
        assertEquals(Color(0xFF00BFA5), fabContainerColor(ThemeVariant.OBSIDIAN))
    }

    @Test
    fun `fabContainerColor NeoBrutalist returns accentPink`() {
        assertEquals(Color(0xFFFF2D78), fabContainerColor(ThemeVariant.NEO_BRUTALIST))
    }

    @Test
    fun `fabContainerColor Vapor returns accentPink`() {
        assertEquals(Color(0xFFF9A8D4), fabContainerColor(ThemeVariant.VAPOR))
    }

    // ── FAB icon color ──

    @Test
    fun `fabIconColor Obsidian returns onPrimary black`() {
        assertEquals(Color(0xFF000000), fabIconColor(ThemeVariant.OBSIDIAN))
    }

    @Test
    fun `fabIconColor NeoBrutalist returns white`() {
        assertEquals(Color(0xFFFFFFFF), fabIconColor(ThemeVariant.NEO_BRUTALIST))
    }

    @Test
    fun `fabIconColor Vapor returns background`() {
        assertEquals(Color(0xFF0E1118), fabIconColor(ThemeVariant.VAPOR))
    }

    // ── FAB corner radius ──

    @Test
    fun `fabCornerRadius Obsidian returns large value for circle`() {
        assertEquals(50.dp, fabCornerRadius(ThemeVariant.OBSIDIAN))
    }

    @Test
    fun `fabCornerRadius NeoBrutalist returns 12dp`() {
        assertEquals(12.dp, fabCornerRadius(ThemeVariant.NEO_BRUTALIST))
    }

    @Test
    fun `fabCornerRadius Vapor returns 20dp`() {
        assertEquals(20.dp, fabCornerRadius(ThemeVariant.VAPOR))
    }

    // ── FAB border presence ──

    @Test
    fun `fabHasBorder NeoBrutalist returns true`() {
        assertTrue(fabHasBorder(ThemeVariant.NEO_BRUTALIST))
    }

    @Test
    fun `fabHasBorder Obsidian returns false`() {
        assertFalse(fabHasBorder(ThemeVariant.OBSIDIAN))
    }

    @Test
    fun `fabHasBorder Vapor returns false`() {
        assertFalse(fabHasBorder(ThemeVariant.VAPOR))
    }

    // ── wrapOnClick callback invocation ──

    @Test
    fun `wrapOnClick invokes original callback when haptic disabled`() {
        var callbackInvoked = false
        val wrapped =
            wrapOnClick(
                hapticEnabled = false,
                performHaptic = { },
                onClick = { callbackInvoked = true },
            )

        wrapped()

        assertTrue(callbackInvoked)
    }

    @Test
    fun `wrapOnClick invokes original callback when haptic enabled`() {
        var callbackInvoked = false
        val wrapped =
            wrapOnClick(
                hapticEnabled = true,
                performHaptic = { },
                onClick = { callbackInvoked = true },
            )

        wrapped()

        assertTrue(callbackInvoked)
    }

    @Test
    fun `wrapOnClick does not perform haptic when haptic disabled`() {
        var hapticFired = false
        val wrapped =
            wrapOnClick(
                hapticEnabled = false,
                performHaptic = { hapticFired = true },
                onClick = { },
            )

        wrapped()

        assertFalse(hapticFired)
    }

    @Test
    fun `wrapOnClick performs haptic when haptic enabled`() {
        var hapticFired = false
        val wrapped =
            wrapOnClick(
                hapticEnabled = true,
                performHaptic = { hapticFired = true },
                onClick = { },
            )

        wrapped()

        assertTrue(hapticFired)
    }

    @Test
    fun `wrapOnClick does not invoke callback before being called`() {
        var callbackInvoked = false
        wrapOnClick(
            hapticEnabled = true,
            performHaptic = { },
            onClick = { callbackInvoked = true },
        )

        assertFalse(callbackInvoked)
    }
}
