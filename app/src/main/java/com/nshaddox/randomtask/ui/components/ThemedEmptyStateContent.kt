@file:Suppress("MagicNumber")

package com.nshaddox.randomtask.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nshaddox.randomtask.domain.model.ThemeVariant
import com.nshaddox.randomtask.ui.theme.LocalThemeVariant
import com.nshaddox.randomtask.ui.theme.Spacing
import com.nshaddox.randomtask.ui.theme.neoBrutalistAccentYellow
import com.nshaddox.randomtask.ui.theme.neoBrutalistBorder
import com.nshaddox.randomtask.ui.theme.vaporAccentTeal

// ── Pure helper functions (package-private, testable without Compose runtime) ──

fun emptyStateTitleFontSize(variant: ThemeVariant): TextUnit =
    when (variant) {
        ThemeVariant.OBSIDIAN -> 22.sp
        ThemeVariant.NEO_BRUTALIST -> 28.sp
        ThemeVariant.VAPOR -> 24.sp
    }

fun emptyStateTitleFontWeight(variant: ThemeVariant): FontWeight =
    when (variant) {
        ThemeVariant.OBSIDIAN -> FontWeight.SemiBold
        ThemeVariant.NEO_BRUTALIST -> FontWeight.Black
        ThemeVariant.VAPOR -> FontWeight.Light
    }

fun emptyStateBodyAlpha(variant: ThemeVariant): Float =
    when (variant) {
        ThemeVariant.OBSIDIAN -> 0.6f
        ThemeVariant.NEO_BRUTALIST -> 1.0f
        ThemeVariant.VAPOR -> 0.4f
    }

fun emptyStateUsesYellowBadge(variant: ThemeVariant): Boolean =
    when (variant) {
        ThemeVariant.OBSIDIAN -> false
        ThemeVariant.NEO_BRUTALIST -> true
        ThemeVariant.VAPOR -> false
    }

// ── Composables ──

@Composable
fun ThemedEmptyStateContent(
    illustration: Painter,
    illustrationContentDescription: String,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    val variant = LocalThemeVariant.current
    val isNeoBrutalist = variant == ThemeVariant.NEO_BRUTALIST

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            Image(
                painter = illustration,
                contentDescription = illustrationContentDescription,
                modifier = Modifier.size(96.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
            )
            Spacer(modifier = Modifier.height(Spacing.small))
            Text(
                text = if (isNeoBrutalist) title.uppercase() else title,
                fontSize = emptyStateTitleFontSize(variant),
                fontWeight = emptyStateTitleFontWeight(variant),
                color =
                    if (variant == ThemeVariant.NEO_BRUTALIST) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
            )
            EmptyStateBody(variant = variant, body = body)
            if (action != null) {
                Spacer(modifier = Modifier.height(Spacing.medium))
                action()
            }
        }
    }
}

@Composable
private fun EmptyStateBody(
    variant: ThemeVariant,
    body: String,
) {
    when (variant) {
        ThemeVariant.NEO_BRUTALIST -> {
            Box(
                modifier =
                    Modifier
                        .background(neoBrutalistAccentYellow, RoundedCornerShape(4.dp))
                        .border(3.dp, neoBrutalistBorder, RoundedCornerShape(4.dp))
                        .padding(horizontal = Spacing.small, vertical = Spacing.extraSmall),
            ) {
                Text(
                    text = body.uppercase(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = neoBrutalistBorder,
                )
            }
        }
        ThemeVariant.VAPOR -> {
            Text(
                text = body,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = vaporAccentTeal.copy(alpha = emptyStateBodyAlpha(variant)),
            )
        }
        ThemeVariant.OBSIDIAN -> {
            Text(
                text = body,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = emptyStateBodyAlpha(variant),
                    ),
            )
        }
    }
}
