package com.nshaddox.randomtask.domain.model

/**
 * Represents the visual theme preference for the application.
 *
 * Used by [SettingsUiState][com.nshaddox.randomtask.ui.screens.settings.SettingsUiState]
 * to store and expose the user's chosen theme.
 */
enum class AppTheme {
    /** Always use light color scheme. */
    LIGHT,

    /** Always use dark color scheme. */
    DARK,

    /** Follow the device system setting for light or dark mode. */
    SYSTEM,
}
