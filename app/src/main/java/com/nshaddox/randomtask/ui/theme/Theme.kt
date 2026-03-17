@file:Suppress("MagicNumber")

package com.nshaddox.randomtask.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.nshaddox.randomtask.domain.model.ThemeVariant

private val ObsidianColorScheme =
    darkColorScheme(
        primary = obsidianPrimary,
        onPrimary = obsidianOnPrimary,
        background = obsidianBackground,
        surface = obsidianCard,
        onSurface = obsidianTextPrimary,
        onSurfaceVariant = obsidianTextSecondary,
        error = obsidianError,
        surfaceVariant = obsidianCardHighlight,
        outline = obsidianTextSecondary,
    )

private val NeoBrutalistColorScheme =
    lightColorScheme(
        primary = neoBrutalistAccentPink,
        onPrimary = neoBrutalistOnAccentPink,
        background = neoBrutalistBackground,
        surface = neoBrutalistCard,
        onSurface = neoBrutalistTextPrimary,
        onSurfaceVariant = neoBrutalistTextSecondary,
        error = neoBrutalistAccentPink,
        surfaceVariant = neoBrutalistBackground,
        outline = neoBrutalistBorder,
    )

private val VaporColorScheme =
    darkColorScheme(
        primary = vaporAccentPink,
        onPrimary = vaporOnAccentPink,
        background = vaporBackground,
        surface = vaporCard,
        onSurface = vaporTextPrimary,
        onSurfaceVariant = vaporTextSecondary,
        error = vaporError,
        surfaceVariant = vaporCard,
        outline = vaporTextSecondary,
    )

/**
 * Top-level theme composable for RandomTask.
 *
 * Wraps content in [MaterialTheme] with the correct [ColorScheme] for the
 * given [variant], and provides [LocalAppThemeTokens] and [LocalThemeVariant]
 * via [CompositionLocalProvider].
 *
 * @param variant The active theme variant selected by the user.
 * @param content The composable content tree.
 */
@Composable
fun RandomTaskTheme(
    variant: ThemeVariant = ThemeVariant.OBSIDIAN,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when (variant) {
            ThemeVariant.OBSIDIAN -> ObsidianColorScheme
            ThemeVariant.NEO_BRUTALIST -> NeoBrutalistColorScheme
            ThemeVariant.VAPOR -> VaporColorScheme
        }

    val tokens =
        when (variant) {
            ThemeVariant.OBSIDIAN -> ObsidianTokens
            ThemeVariant.NEO_BRUTALIST -> NeoBrutalistTokens
            ThemeVariant.VAPOR -> VaporTokens
        }

    CompositionLocalProvider(
        LocalAppThemeTokens provides tokens,
        LocalThemeVariant provides variant,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}
