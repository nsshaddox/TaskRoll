@file:Suppress("MagicNumber")

package com.nshaddox.randomtask.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Custom theme extension tokens that go beyond Material3 ColorScheme.
 *
 * Provided via [LocalAppThemeTokens] CompositionLocal alongside MaterialTheme.
 * Contains priority colors, border widths, shadow offsets, and typography flags
 * that differ per theme variant.
 */
data class AppThemeTokens(
    val priorityHigh: Color,
    val priorityMedium: Color,
    val priorityLow: Color,
    val priorityHighBg: Color,
    val priorityMediumBg: Color,
    val priorityLowBg: Color,
    val borderWidth: Dp,
    val shadowOffset: Dp,
    val useSolidShadow: Boolean,
    val useUppercaseTitles: Boolean,
)

/** Custom tokens for the Obsidian theme. */
val ObsidianTokens =
    AppThemeTokens(
        priorityHigh = Color(0xFFFF5252),
        priorityMedium = Color(0xFFFFB74D),
        priorityLow = Color(0xFF69F0AE),
        priorityHighBg = Color(0xFF1A1A1A),
        priorityMediumBg = Color(0xFF1A1A1A),
        priorityLowBg = Color(0xFF1A1A1A),
        borderWidth = 0.dp,
        shadowOffset = 0.dp,
        useSolidShadow = false,
        useUppercaseTitles = false,
    )

/** Custom tokens for the Neo Brutalist theme. */
val NeoBrutalistTokens =
    AppThemeTokens(
        priorityHigh = Color(0xFFFF2D78),
        priorityMedium = Color(0xFFFFE500),
        priorityLow = Color(0xFF22C55E),
        priorityHighBg = Color(0xFFFF2D78),
        priorityMediumBg = Color(0xFFFFE500),
        priorityLowBg = Color(0xFF22C55E),
        borderWidth = 3.dp,
        shadowOffset = 4.dp,
        useSolidShadow = true,
        useUppercaseTitles = true,
    )

/** Custom tokens for the Vapor theme. */
val VaporTokens =
    AppThemeTokens(
        priorityHigh = Color(0xFFF9A8D4),
        priorityMedium = Color(0xFFA5B4FC),
        priorityLow = Color(0xFF5EEAD4),
        priorityHighBg = Color(0xFF1F1520),
        priorityMediumBg = Color(0xFF15182A),
        priorityLowBg = Color(0xFF101D1A),
        borderWidth = 0.dp,
        shadowOffset = 0.dp,
        useSolidShadow = false,
        useUppercaseTitles = false,
    )
