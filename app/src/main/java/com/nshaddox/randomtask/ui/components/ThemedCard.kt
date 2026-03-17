@file:Suppress("MagicNumber")

package com.nshaddox.randomtask.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.ThemeVariant
import com.nshaddox.randomtask.ui.theme.LocalAppThemeTokens
import com.nshaddox.randomtask.ui.theme.LocalThemeVariant
import com.nshaddox.randomtask.ui.theme.neoBrutalistBorder
import com.nshaddox.randomtask.ui.theme.neoBrutalistCard
import com.nshaddox.randomtask.ui.theme.obsidianCard
import com.nshaddox.randomtask.ui.theme.obsidianCardHighlight
import com.nshaddox.randomtask.ui.theme.vaporPriorityHighBg
import com.nshaddox.randomtask.ui.theme.vaporPriorityLowBg
import com.nshaddox.randomtask.ui.theme.vaporPriorityMediumBg

/**
 * Returns the card background color for the given [variant] and [priority].
 *
 * Pure function — testable without Compose runtime.
 */
fun cardBackgroundForVariant(
    variant: ThemeVariant,
    priority: Priority,
): Color =
    when (variant) {
        ThemeVariant.OBSIDIAN -> obsidianCard
        ThemeVariant.NEO_BRUTALIST -> neoBrutalistCard
        ThemeVariant.VAPOR ->
            when (priority) {
                Priority.HIGH -> vaporPriorityHighBg
                Priority.MEDIUM -> vaporPriorityMediumBg
                Priority.LOW -> vaporPriorityLowBg
            }
    }

/**
 * Returns the card border width for the given [variant].
 *
 * Pure function — testable without Compose runtime.
 */
fun cardBorderWidthForVariant(variant: ThemeVariant): Dp =
    when (variant) {
        ThemeVariant.OBSIDIAN -> 0.dp
        ThemeVariant.NEO_BRUTALIST -> 3.dp
        ThemeVariant.VAPOR -> 0.dp
    }

/**
 * Returns the card corner radius for the given [variant].
 *
 * Pure function — testable without Compose runtime.
 */
fun cardCornerRadiusForVariant(variant: ThemeVariant): Dp =
    when (variant) {
        ThemeVariant.OBSIDIAN -> 20.dp
        ThemeVariant.NEO_BRUTALIST -> 12.dp
        ThemeVariant.VAPOR -> 22.dp
    }

/**
 * Theme-aware card that renders differently per [ThemeVariant]:
 *
 * - **Obsidian**: Horizontal gradient background with a 4dp priority-colored left accent bar.
 * - **Neo Brutalist**: White fill, 3dp black border, solid offset shadow at +4dp x/y.
 * - **Vapor**: Priority-tinted background (no border, no shadow).
 *
 * @param priority The task priority, used for accent bar (Obsidian) or tinted bg (Vapor).
 * @param onClick Optional click handler.
 * @param modifier Modifier for layout.
 * @param content The card content composable.
 */
@Suppress("LongMethod")
@Composable
fun ThemedCard(
    priority: Priority,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val variant = LocalThemeVariant.current
    val tokens = LocalAppThemeTokens.current
    val cornerRadius = cardCornerRadiusForVariant(variant)
    val shape = RoundedCornerShape(cornerRadius)

    when (variant) {
        ThemeVariant.OBSIDIAN -> {
            val accentColor =
                when (priority) {
                    Priority.HIGH -> tokens.priorityHigh
                    Priority.MEDIUM -> tokens.priorityMedium
                    Priority.LOW -> tokens.priorityLow
                }
            val clickModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            Box(
                modifier =
                    modifier
                        .clip(shape)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(obsidianCard, obsidianCardHighlight),
                            ),
                        )
                        .drawBehind {
                            drawRect(
                                color = accentColor,
                                topLeft = Offset.Zero,
                                size = Size(4.dp.toPx(), size.height),
                            )
                        }
                        .then(clickModifier)
                        .padding(16.dp),
            ) {
                content()
            }
        }

        ThemeVariant.NEO_BRUTALIST -> {
            val clickModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
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
                            .clip(shape)
                            .background(neoBrutalistCard)
                            .border(3.dp, neoBrutalistBorder, shape)
                            .then(clickModifier)
                            .padding(16.dp),
                ) {
                    content()
                }
            }
        }

        ThemeVariant.VAPOR -> {
            val bg = cardBackgroundForVariant(variant, priority)
            val clickModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            Box(
                modifier =
                    modifier
                        .clip(shape)
                        .background(bg)
                        .then(clickModifier)
                        .padding(16.dp),
            ) {
                content()
            }
        }
    }
}
