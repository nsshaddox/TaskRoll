package com.nshaddox.randomtask

import com.nshaddox.randomtask.domain.model.AppTheme

/**
 * Resolves the effective dark-theme flag from the user's [AppTheme] preference
 * and the current system dark-mode state.
 *
 * This is a pure function with no Android dependencies, making it easy to
 * unit-test without instrumentation.
 *
 * @param appTheme The user's chosen theme preference.
 * @param isSystemDark Whether the device is currently in system dark mode.
 * @return `true` if the app should use dark theme, `false` otherwise.
 */
fun resolveTheme(
    appTheme: AppTheme,
    isSystemDark: Boolean,
): Boolean =
    when (appTheme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemDark
    }
