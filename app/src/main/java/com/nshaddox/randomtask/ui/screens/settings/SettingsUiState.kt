package com.nshaddox.randomtask.ui.screens.settings

import com.nshaddox.randomtask.domain.model.SortOrder
import com.nshaddox.randomtask.domain.model.ThemeVariant

/**
 * UI state for the settings screen.
 *
 * @property appTheme The user's selected visual theme variant.
 * @property sortOrder The user's selected default sort order for task lists.
 * @property hapticEnabled Whether haptic feedback is enabled globally.
 * @property isLoading Whether a settings operation (load or save) is in progress.
 * @property errorMessage An optional error message to display. Null when there is no error.
 */
data class SettingsUiState(
    val appTheme: ThemeVariant = ThemeVariant.OBSIDIAN,
    val sortOrder: SortOrder = SortOrder.CREATED_DATE_DESC,
    val hapticEnabled: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
