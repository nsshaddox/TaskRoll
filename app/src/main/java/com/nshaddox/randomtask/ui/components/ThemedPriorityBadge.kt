@file:Suppress("MagicNumber")

package com.nshaddox.randomtask.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nshaddox.randomtask.R
import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.ThemeVariant
import com.nshaddox.randomtask.ui.theme.LocalThemeVariant
import com.nshaddox.randomtask.ui.theme.neoBrutalistBorder
import com.nshaddox.randomtask.ui.theme.vaporAccentIndigo
import com.nshaddox.randomtask.ui.theme.vaporAccentPink
import com.nshaddox.randomtask.ui.theme.vaporAccentTeal

/**
 * Returns the badge background/container color for the given [priority] and [variant].
 *
 * - Obsidian: solid priority color (used as dot fill).
 * - Neo Brutalist: solid priority color (used as block badge fill).
 * - Vapor: priority accent at 10% alpha (used as pill background).
 *
 * Pure function -- testable without Compose runtime.
 */
fun themedPriorityContainerColor(
    priority: Priority,
    variant: ThemeVariant,
): Color =
    when (variant) {
        ThemeVariant.OBSIDIAN ->
            when (priority) {
                Priority.HIGH -> Color(0xFFFF5252)
                Priority.MEDIUM -> Color(0xFFFFB74D)
                Priority.LOW -> Color(0xFF69F0AE)
            }
        ThemeVariant.NEO_BRUTALIST ->
            when (priority) {
                Priority.HIGH -> Color(0xFFFF2D78)
                Priority.MEDIUM -> Color(0xFFFFE500)
                Priority.LOW -> Color(0xFF22C55E)
            }
        ThemeVariant.VAPOR ->
            when (priority) {
                Priority.HIGH -> vaporAccentPink.copy(alpha = 0.1f)
                Priority.MEDIUM -> vaporAccentIndigo.copy(alpha = 0.1f)
                Priority.LOW -> vaporAccentTeal.copy(alpha = 0.1f)
            }
    }

/**
 * Returns the on-container (text/icon) color for the given [priority] and [variant].
 *
 * - Obsidian: same as container color (dot has no text; used for accent bar).
 * - Neo Brutalist: white on pink/green, black on yellow.
 * - Vapor: full-opacity priority accent color.
 *
 * Pure function -- testable without Compose runtime.
 */
fun themedPriorityOnContainerColor(
    priority: Priority,
    variant: ThemeVariant,
): Color =
    when (variant) {
        ThemeVariant.OBSIDIAN ->
            when (priority) {
                Priority.HIGH -> Color(0xFFFF5252)
                Priority.MEDIUM -> Color(0xFFFFB74D)
                Priority.LOW -> Color(0xFF69F0AE)
            }
        ThemeVariant.NEO_BRUTALIST ->
            when (priority) {
                Priority.HIGH -> Color(0xFFFFFFFF)
                Priority.MEDIUM -> Color(0xFF1A1A1A)
                Priority.LOW -> Color(0xFFFFFFFF)
            }
        ThemeVariant.VAPOR ->
            when (priority) {
                Priority.HIGH -> vaporAccentPink
                Priority.MEDIUM -> vaporAccentIndigo
                Priority.LOW -> vaporAccentTeal
            }
    }

/**
 * Returns the badge border width for the given [variant].
 *
 * Pure function -- testable without Compose runtime.
 */
fun themedBadgeBorderWidth(variant: ThemeVariant): Dp =
    when (variant) {
        ThemeVariant.OBSIDIAN -> 0.dp
        ThemeVariant.NEO_BRUTALIST -> 2.dp
        ThemeVariant.VAPOR -> 0.dp
    }

/**
 * Theme-aware priority badge that renders differently per [ThemeVariant]:
 *
 * - **Obsidian**: 10dp circle dot filled with priority color. No text.
 * - **Neo Brutalist**: Block badge (4dp radius), priority color fill, 2dp border,
 *   UPPERCASE text (10sp, Black weight).
 * - **Vapor**: Pill badge (10dp radius), priority accent text on 10% alpha background.
 *   10sp SemiBold.
 *
 * @param priority The [Priority] level to display.
 * @param modifier Optional [Modifier] for layout customization.
 */
@Composable
fun ThemedPriorityBadge(
    priority: Priority,
    modifier: Modifier = Modifier,
) {
    val variant = LocalThemeVariant.current
    val label = stringResource(priorityLabelResId(priority))
    val description = stringResource(R.string.cd_priority_badge, label)

    when (variant) {
        ThemeVariant.OBSIDIAN -> {
            Box(
                modifier =
                    modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(themedPriorityContainerColor(priority, variant))
                        .semantics { contentDescription = description },
            )
        }

        ThemeVariant.NEO_BRUTALIST -> {
            val shape = RoundedCornerShape(4.dp)
            Box(
                modifier =
                    modifier
                        .clip(shape)
                        .background(themedPriorityContainerColor(priority, variant))
                        .border(2.dp, neoBrutalistBorder, shape)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .semantics { contentDescription = description },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label.uppercase(),
                    color = themedPriorityOnContainerColor(priority, variant),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                )
            }
        }

        ThemeVariant.VAPOR -> {
            val shape = RoundedCornerShape(10.dp)
            Box(
                modifier =
                    modifier
                        .clip(shape)
                        .background(themedPriorityContainerColor(priority, variant))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .semantics { contentDescription = description },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = themedPriorityOnContainerColor(priority, variant),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}
