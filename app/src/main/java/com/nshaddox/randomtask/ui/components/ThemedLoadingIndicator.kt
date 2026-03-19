@file:Suppress("MagicNumber")

package com.nshaddox.randomtask.ui.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nshaddox.randomtask.domain.model.ThemeVariant
import com.nshaddox.randomtask.ui.theme.LocalThemeVariant
import com.nshaddox.randomtask.ui.theme.neoBrutalistAccentPink
import com.nshaddox.randomtask.ui.theme.obsidianPrimary
import com.nshaddox.randomtask.ui.theme.vaporAccentTeal

/**
 * Returns the loading indicator color for the given [variant].
 *
 * Pure function -- testable without Compose runtime.
 */
fun loadingIndicatorColorForVariant(variant: ThemeVariant): Color =
    when (variant) {
        ThemeVariant.OBSIDIAN -> obsidianPrimary
        ThemeVariant.NEO_BRUTALIST -> neoBrutalistAccentPink
        ThemeVariant.VAPOR -> vaporAccentTeal
    }

/**
 * Returns the loading indicator stroke width for the given [variant].
 *
 * Pure function -- testable without Compose runtime.
 */
fun loadingIndicatorStrokeWidthForVariant(variant: ThemeVariant): Dp =
    when (variant) {
        ThemeVariant.OBSIDIAN -> 4.dp
        ThemeVariant.NEO_BRUTALIST -> 4.dp
        ThemeVariant.VAPOR -> 2.dp
    }

/**
 * Theme-aware loading indicator that renders a [CircularProgressIndicator]
 * with per-theme color and stroke width as specified in DESIGN_SYSTEM.md SS5.8.
 *
 * - **Obsidian**: primary teal, 4dp stroke.
 * - **Neo Brutalist**: accentPink, 4dp stroke.
 * - **Vapor**: accentTeal, 2dp stroke.
 *
 * @param modifier Modifier for layout.
 */
@Composable
fun ThemedLoadingIndicator(modifier: Modifier = Modifier) {
    val variant = LocalThemeVariant.current
    CircularProgressIndicator(
        color = loadingIndicatorColorForVariant(variant),
        strokeWidth = loadingIndicatorStrokeWidthForVariant(variant),
        modifier = modifier,
    )
}
