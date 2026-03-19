@file:Suppress("MagicNumber")

package com.nshaddox.randomtask.ui.theme

import com.nshaddox.randomtask.domain.model.ThemeVariant

/**
 * Returns the standard animation duration in milliseconds for the given [variant].
 *
 * - Obsidian: 200ms (snappy, focused)
 * - Neo Brutalist: 250ms (punchy, deliberate)
 * - Vapor: 300ms (soft, flowing)
 *
 * Pure function -- testable without Compose runtime.
 */
fun animationDurationMs(variant: ThemeVariant): Int =
    when (variant) {
        ThemeVariant.OBSIDIAN -> 200
        ThemeVariant.NEO_BRUTALIST -> 250
        ThemeVariant.VAPOR -> 300
    }

/**
 * Shared duration for screen navigation transitions.
 *
 * Uses a single constant (not per-theme) to avoid jarring asymmetric
 * enter/exit transitions when navigating between screens.
 */
const val NAV_TRANSITION_DURATION_MS = 300
