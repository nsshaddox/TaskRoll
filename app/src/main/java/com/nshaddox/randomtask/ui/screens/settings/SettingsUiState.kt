package com.nshaddox.randomtask.ui.screens.settings

import com.nshaddox.randomtask.domain.model.AppTheme
import com.nshaddox.randomtask.domain.model.SortOrder

/**
 * UI state for the settings screen.
 *
 * @property appTheme The user's selected visual theme preference.
 * @property sortOrder The user's selected default sort order for task lists.
 * @property isLoading Whether a settings operation (load or save) is in progress.
 * @property errorMessage An optional error message to display. Null when there is no error.
 */
data class SettingsUiState(
    val appTheme: AppTheme = AppTheme.SYSTEM,
    val sortOrder: SortOrder = SortOrder.CREATED_DATE_DESC,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
