# Settings Feature

**Feature**: 12-settings-feature
**Date**: 2026-03-14
**Complexity**: Medium
**Source Issues**: GH#224, GH#226, GH#230

## What Changed

- Created `SettingsScreen` with theme selection (Light/Dark/System) and default sort order radio groups
- Wired Settings route into NavGraph and added Settings icon to TaskListScreen TopAppBar
- Applied theme preference to `MaterialTheme` in `MainActivity` via extracted `resolveTheme()` function
- Enabled `buildConfig` for `BuildConfig.VERSION_NAME` in About section

## Why

Users need a settings screen to control app theme and default sort behavior, persisted across restarts.

## Key Files

| File | Change Type |
|------|-------------|
| `ui/screens/settings/SettingsScreen.kt` | Created |
| `ThemeResolution.kt` | Created |
| `MainActivity.kt` | Modified |
| `ui/navigation/NavGraph.kt` | Modified |
| `app/build.gradle.kts` | Modified |

## Implementation Notes

- `resolveTheme()` is a pure function for testable theme logic (no Compose dependency)
- SettingsViewModel was pre-existing; only the screen composable was new

## Verification

- [x] 4 theme resolution tests (all AppTheme branches)
- [x] Lint clean, no hardcoded strings
