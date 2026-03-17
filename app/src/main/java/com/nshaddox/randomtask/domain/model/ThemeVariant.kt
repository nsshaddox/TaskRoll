package com.nshaddox.randomtask.domain.model

/**
 * Represents the visual theme variant for the application.
 *
 * Each variant determines the complete visual identity of the app:
 * color palette, typography weight/casing, shapes, borders, and shadows.
 *
 * Used by [SettingsUiState][com.nshaddox.randomtask.ui.screens.settings.SettingsUiState]
 * to store and expose the user's chosen theme.
 */
enum class ThemeVariant {
    /** Sleek dark theme with teal accent. Gradient cards, priority edge accents. */
    OBSIDIAN,

    /** Bold light theme with thick borders, offset drop shadows, and punchy color blocks. */
    NEO_BRUTALIST,

    /** Soft pastel-tinted dark mode. Priority determines card tint. Rounded shapes. */
    VAPOR,
}
