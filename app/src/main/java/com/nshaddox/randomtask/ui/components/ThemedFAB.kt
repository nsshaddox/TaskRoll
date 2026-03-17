@file:Suppress("MagicNumber")

package com.nshaddox.randomtask.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nshaddox.randomtask.domain.model.ThemeVariant
import com.nshaddox.randomtask.ui.theme.LocalThemeVariant
import com.nshaddox.randomtask.ui.theme.neoBrutalistAccentPink
import com.nshaddox.randomtask.ui.theme.neoBrutalistBorder
import com.nshaddox.randomtask.ui.theme.neoBrutalistOnAccentPink
import com.nshaddox.randomtask.ui.theme.obsidianOnPrimary
import com.nshaddox.randomtask.ui.theme.obsidianPrimary
import com.nshaddox.randomtask.ui.theme.vaporAccentPink
import com.nshaddox.randomtask.ui.theme.vaporBackground

/**
 * Returns the FAB container (background) color for the given [variant].
 *
 * Pure function -- testable without Compose runtime.
 */
fun fabContainerColor(variant: ThemeVariant): Color =
    when (variant) {
        ThemeVariant.OBSIDIAN -> obsidianPrimary
        ThemeVariant.NEO_BRUTALIST -> neoBrutalistAccentPink
        ThemeVariant.VAPOR -> vaporAccentPink
    }

/**
 * Returns the FAB icon (content) color for the given [variant].
 *
 * Pure function -- testable without Compose runtime.
 */
fun fabIconColor(variant: ThemeVariant): Color =
    when (variant) {
        ThemeVariant.OBSIDIAN -> obsidianOnPrimary
        ThemeVariant.NEO_BRUTALIST -> neoBrutalistOnAccentPink
        ThemeVariant.VAPOR -> vaporBackground
    }

/**
 * Returns the FAB corner radius for the given [variant].
 * Obsidian uses a large radius to approximate CircleShape.
 *
 * Pure function -- testable without Compose runtime.
 */
fun fabCornerRadius(variant: ThemeVariant): Dp =
    when (variant) {
        ThemeVariant.OBSIDIAN -> 50.dp
        ThemeVariant.NEO_BRUTALIST -> 12.dp
        ThemeVariant.VAPOR -> 20.dp
    }

/**
 * Returns whether the FAB has a border for the given [variant].
 *
 * Pure function -- testable without Compose runtime.
 */
fun fabHasBorder(variant: ThemeVariant): Boolean =
    when (variant) {
        ThemeVariant.OBSIDIAN -> false
        ThemeVariant.NEO_BRUTALIST -> true
        ThemeVariant.VAPOR -> false
    }

/**
 * Theme-aware FAB that renders differently per [ThemeVariant]:
 *
 * - **Obsidian**: CircleShape, filled primary, icon in onPrimary.
 * - **Neo Brutalist**: RoundedCornerShape(12dp), accentPink, 3dp border, offset shadow.
 * - **Vapor**: RoundedCornerShape(20dp), accentPink, icon in background.
 *
 * @param onClick Click callback.
 * @param icon The icon to display.
 * @param contentDescription Accessibility description for the icon.
 * @param modifier Modifier for layout.
 */
@Composable
fun ThemedFAB(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val variant = LocalThemeVariant.current
    val cornerRadius = fabCornerRadius(variant)
    val shape = RoundedCornerShape(cornerRadius)
    val containerColor = fabContainerColor(variant)
    val iconColor = fabIconColor(variant)

    when (variant) {
        ThemeVariant.NEO_BRUTALIST -> {
            Box(modifier = modifier.padding(end = 4.dp, bottom = 4.dp)) {
                // Shadow layer
                Box(
                    modifier =
                        Modifier
                            .matchParentSize()
                            .offset(x = 4.dp, y = 4.dp)
                            .clip(shape)
                            .background(neoBrutalistBorder),
                )
                // Content layer
                Box(
                    modifier =
                        Modifier
                            .size(56.dp)
                            .clip(shape)
                            .background(containerColor)
                            .border(3.dp, neoBrutalistBorder, shape)
                            .clickable(onClick = onClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = contentDescription,
                        tint = iconColor,
                    )
                }
            }
        }

        else -> {
            Box(
                modifier =
                    modifier
                        .size(56.dp)
                        .clip(shape)
                        .background(containerColor)
                        .clickable(onClick = onClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = iconColor,
                )
            }
        }
    }
}
