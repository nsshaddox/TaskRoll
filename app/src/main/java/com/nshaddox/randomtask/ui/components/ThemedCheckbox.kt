@file:Suppress("MagicNumber")

package com.nshaddox.randomtask.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nshaddox.randomtask.domain.model.ThemeVariant
import com.nshaddox.randomtask.ui.theme.LocalThemeVariant
import com.nshaddox.randomtask.ui.theme.animationDurationMs
import com.nshaddox.randomtask.ui.theme.neoBrutalistBorder
import com.nshaddox.randomtask.ui.theme.neoBrutalistTextPrimary
import com.nshaddox.randomtask.ui.theme.obsidianOnPrimary
import com.nshaddox.randomtask.ui.theme.obsidianPrimary
import com.nshaddox.randomtask.ui.theme.obsidianTextSecondary
import com.nshaddox.randomtask.ui.theme.vaporAccentPink
import com.nshaddox.randomtask.ui.theme.vaporAccentPinkDim
import com.nshaddox.randomtask.ui.theme.vaporBackground

/**
 * Returns the fill color used when the checkbox is checked.
 *
 * Pure function — testable without Compose runtime.
 */
fun checkboxCheckedColor(variant: ThemeVariant): Color =
    when (variant) {
        ThemeVariant.OBSIDIAN -> obsidianPrimary
        ThemeVariant.NEO_BRUTALIST -> neoBrutalistTextPrimary
        ThemeVariant.VAPOR -> vaporAccentPink
    }

/**
 * Returns the stroke/border color used when the checkbox is unchecked.
 *
 * Pure function — testable without Compose runtime.
 */
fun checkboxUncheckedColor(variant: ThemeVariant): Color =
    when (variant) {
        ThemeVariant.OBSIDIAN -> obsidianTextSecondary
        ThemeVariant.NEO_BRUTALIST -> neoBrutalistBorder
        ThemeVariant.VAPOR -> vaporAccentPinkDim
    }

/**
 * Returns the checkmark color (icon/text on checked surface).
 *
 * Pure function — testable without Compose runtime.
 */
fun checkboxCheckmarkColor(variant: ThemeVariant): Color =
    when (variant) {
        ThemeVariant.OBSIDIAN -> obsidianOnPrimary
        ThemeVariant.NEO_BRUTALIST -> Color.White
        ThemeVariant.VAPOR -> vaporBackground
    }

/**
 * Theme-aware checkbox that renders differently per [ThemeVariant]:
 *
 * - **Obsidian**: 24dp circle. Unchecked: transparent with 2dp textSecondary stroke.
 *   Checked: filled primary with checkmark in onPrimary.
 * - **Neo Brutalist**: 28dp rounded rect (6dp radius). Unchecked: transparent with 3dp border stroke.
 *   Checked: filled textPrimary with checkmark in white, Black weight.
 * - **Vapor**: 24dp rounded rect (8dp radius). Unchecked: accentPink at 15% alpha.
 *   Checked: accentPink fill with checkmark in background color.
 *
 * Includes a scale animation on toggle and optional haptic feedback.
 *
 * @param checked Whether the checkbox is currently checked.
 * @param onCheckedChange Callback when the checkbox is toggled.
 * @param modifier Modifier for layout.
 * @param hapticEnabled Whether haptic feedback fires on toggle.
 */
@Suppress("LongMethod")
@Composable
fun ThemedCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    hapticEnabled: Boolean = true,
) {
    val variant = LocalThemeVariant.current
    val hapticFeedback = LocalHapticFeedback.current

    val scale by animateFloatAsState(
        targetValue = if (checked) 1f else 0.85f,
        animationSpec = tween(durationMillis = animationDurationMs(variant)),
        label = "checkboxScale",
    )

    val wrappedOnCheckedChange: (Boolean) -> Unit = { newValue ->
        if (hapticEnabled) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }
        onCheckedChange(newValue)
    }

    val scaleModifier =
        Modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }

    when (variant) {
        ThemeVariant.OBSIDIAN -> {
            Box(
                modifier =
                    modifier
                        .size(24.dp)
                        .then(scaleModifier)
                        .clip(CircleShape)
                        .then(
                            if (checked) {
                                Modifier.background(checkboxCheckedColor(variant), CircleShape)
                            } else {
                                Modifier.border(2.dp, checkboxUncheckedColor(variant), CircleShape)
                            },
                        )
                        .toggleable(
                            value = checked,
                            role = Role.Checkbox,
                            onValueChange = wrappedOnCheckedChange,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                if (checked) {
                    Text(
                        text = "\u2713",
                        color = checkboxCheckmarkColor(variant),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        ThemeVariant.NEO_BRUTALIST -> {
            val shape = RoundedCornerShape(6.dp)
            Box(
                modifier =
                    modifier
                        .size(28.dp)
                        .then(scaleModifier)
                        .clip(shape)
                        .then(
                            if (checked) {
                                Modifier.background(checkboxCheckedColor(variant), shape)
                            } else {
                                Modifier.border(3.dp, checkboxUncheckedColor(variant), shape)
                            },
                        )
                        .toggleable(
                            value = checked,
                            role = Role.Checkbox,
                            onValueChange = wrappedOnCheckedChange,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                if (checked) {
                    Text(
                        text = "\u2713",
                        color = checkboxCheckmarkColor(variant),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                    )
                }
            }
        }

        ThemeVariant.VAPOR -> {
            val shape = RoundedCornerShape(8.dp)
            Box(
                modifier =
                    modifier
                        .size(24.dp)
                        .then(scaleModifier)
                        .clip(shape)
                        .then(
                            if (checked) {
                                Modifier.background(checkboxCheckedColor(variant), shape)
                            } else {
                                Modifier.background(vaporAccentPink.copy(alpha = 0.15f), shape)
                            },
                        )
                        .toggleable(
                            value = checked,
                            role = Role.Checkbox,
                            onValueChange = wrappedOnCheckedChange,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                if (checked) {
                    Text(
                        text = "\u2713",
                        color = checkboxCheckmarkColor(variant),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
