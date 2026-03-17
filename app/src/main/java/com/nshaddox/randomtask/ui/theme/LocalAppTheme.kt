package com.nshaddox.randomtask.ui.theme

import androidx.compose.runtime.compositionLocalOf
import com.nshaddox.randomtask.domain.model.ThemeVariant

/**
 * CompositionLocal providing the current [AppThemeTokens] for the active theme variant.
 *
 * Access via `LocalAppThemeTokens.current` inside any composable.
 * Crashes at runtime if no value has been provided (intentional — forces
 * correct wiring through [RandomTaskTheme]).
 */
val LocalAppThemeTokens =
    compositionLocalOf<AppThemeTokens> {
        error("No AppThemeTokens provided. Wrap your content in RandomTaskTheme.")
    }

/**
 * CompositionLocal providing the current [ThemeVariant].
 *
 * Access via `LocalThemeVariant.current` inside any composable for
 * structural branching (e.g., Neo Brutalist shadow layout vs. Obsidian gradient).
 */
val LocalThemeVariant =
    compositionLocalOf<ThemeVariant> {
        error("No ThemeVariant provided. Wrap your content in RandomTaskTheme.")
    }
