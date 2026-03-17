@file:Suppress("MagicNumber")

package com.nshaddox.randomtask.ui.components

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.nshaddox.randomtask.R
import com.nshaddox.randomtask.domain.model.Priority

/**
 * Tonal container color for LOW priority.
 * Uses a muted green tone similar to Material3 tertiaryContainer.
 */
val LowPriorityColor = Color(0xFFD0E8D0)

/**
 * Tonal container color for MEDIUM priority.
 * Uses an amber/orange tone for moderate urgency.
 */
val MediumPriorityColor = Color(0xFFFFE0B2)

/**
 * Tonal container color for HIGH priority.
 * Uses a red-tinted tone similar to Material3 errorContainer.
 */
val HighPriorityColor = Color(0xFFFFDAD6)

/** On-container text color for LOW priority. */
val OnLowPriorityColor = Color(0xFF1B5E20)

/** On-container text color for MEDIUM priority. */
val OnMediumPriorityColor = Color(0xFF7A4F01)

/** On-container text color for HIGH priority. */
val OnHighPriorityColor = Color(0xFF93000A)

/**
 * Returns the tonal container color for the given [priority].
 *
 * This is a pure function (no Compose context required) so it can be
 * tested in standard unit tests.
 *
 * @param priority The task priority level.
 * @return A [Color] representing the container background.
 */
fun priorityContainerColor(priority: Priority): Color =
    when (priority) {
        Priority.LOW -> LowPriorityColor
        Priority.MEDIUM -> MediumPriorityColor
        Priority.HIGH -> HighPriorityColor
    }

/**
 * Returns the on-container text color for the given [priority].
 *
 * @param priority The task priority level.
 * @return A [Color] for text/icon content on the priority container.
 */
fun priorityOnContainerColor(priority: Priority): Color =
    when (priority) {
        Priority.LOW -> OnLowPriorityColor
        Priority.MEDIUM -> OnMediumPriorityColor
        Priority.HIGH -> OnHighPriorityColor
    }

/**
 * Returns the string resource ID for the label of the given [priority].
 *
 * @param priority The task priority level.
 * @return A string resource ID for the priority display label.
 */
@StringRes
fun priorityLabelResId(priority: Priority): Int =
    when (priority) {
        Priority.LOW -> R.string.priority_low
        Priority.MEDIUM -> R.string.priority_medium
        Priority.HIGH -> R.string.priority_high
    }

/**
 * A theme-aware priority badge composable that delegates to [ThemedPriorityBadge].
 *
 * Renders a priority indicator appropriate to the current theme variant:
 * dot (Obsidian), block badge (Neo Brutalist), or pill (Vapor).
 *
 * @param priority The [Priority] level to display.
 * @param modifier Optional [Modifier] for layout customization.
 */
@Composable
fun PriorityBadge(
    priority: Priority,
    modifier: Modifier = Modifier,
) {
    ThemedPriorityBadge(
        priority = priority,
        modifier = modifier,
    )
}
