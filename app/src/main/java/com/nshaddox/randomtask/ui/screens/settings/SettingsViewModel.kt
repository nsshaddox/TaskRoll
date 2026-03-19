package com.nshaddox.randomtask.ui.screens.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nshaddox.randomtask.domain.model.SortOrder
import com.nshaddox.randomtask.domain.model.ThemeVariant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
        @Named("IO") private val ioDispatcher: CoroutineDispatcher,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(SettingsUiState())
        val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

        init {
            viewModelScope.launch(ioDispatcher) {
                dataStore.data.collect { preferences ->
                    val theme = preferences.readEnum(THEME_KEY, ThemeVariant.OBSIDIAN)
                    val sortOrder = preferences.readEnum(SORT_ORDER_KEY, SortOrder.CREATED_DATE_DESC)
                    val hapticEnabled = preferences[HAPTIC_KEY] ?: true
                    _uiState.value =
                        SettingsUiState(
                            appTheme = theme,
                            sortOrder = sortOrder,
                            hapticEnabled = hapticEnabled,
                        )
                }
            }
        }

        fun setTheme(theme: ThemeVariant) {
            viewModelScope.launch(ioDispatcher) {
                dataStore.edit { prefs ->
                    prefs[THEME_KEY] = theme.name
                }
            }
        }

        fun setSortOrder(sortOrder: SortOrder) {
            viewModelScope.launch(ioDispatcher) {
                dataStore.edit { prefs ->
                    prefs[SORT_ORDER_KEY] = sortOrder.name
                }
            }
        }

        fun setHapticEnabled(enabled: Boolean) {
            viewModelScope.launch(ioDispatcher) {
                dataStore.edit { prefs ->
                    prefs[HAPTIC_KEY] = enabled
                }
            }
        }

        private companion object {
            val THEME_KEY = stringPreferencesKey("app_theme")
            val SORT_ORDER_KEY = stringPreferencesKey("sort_order")
            val HAPTIC_KEY = booleanPreferencesKey("haptic_enabled")
        }
    }

/**
 * Reads an enum value from DataStore preferences with a safe fallback.
 *
 * @param key The preference key to read.
 * @param default The default value if the key is missing or the stored value is invalid.
 * @return The enum value stored under [key], or [default] if not found or unparseable.
 */
private inline fun <reified T : Enum<T>> Preferences.readEnum(
    key: Preferences.Key<String>,
    default: T,
): T =
    this[key]?.let { stored ->
        try {
            enumValueOf<T>(stored)
        } catch (_: IllegalArgumentException) {
            default
        }
    } ?: default
