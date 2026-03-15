# Settings ViewModel & Navigation Routes

**Implemented**: 2026-03-14
**Complexity**: medium

## What Changed

- Added `Screen.CompletedTasks` and `Screen.Settings` navigation route data objects to `NavRoutes.kt`
- Created `DataStoreModule.kt` Hilt singleton provider for `DataStore<Preferences>`
- Implemented `SettingsViewModel` exposing `StateFlow<SettingsUiState>` backed by DataStore persistence
- Added `datastore-preferences` dependency (v1.1.1) to `libs.versions.toml` and `build.gradle.kts`

## Why

Enables app-wide settings persistence across process death. SettingsViewModel provides reactive settings management for theme and sort order preferences, persisted via DataStore. Two new navigation routes prepare the navigation graph for settings and completed tasks screens (composables implemented in separate features).

## Key Files

- `gradle/libs.versions.toml` - Added `datastore = "1.1.1"` version and library alias
- `app/build.gradle.kts` - Added `datastore-preferences` implementation dependency
- `app/src/main/java/com/nshaddox/randomtask/ui/navigation/NavRoutes.kt` - Added two data object routes with KDoc
- `app/src/main/java/com/nshaddox/randomtask/di/DataStoreModule.kt` - New Hilt singleton provider using `PreferenceDataStoreFactory`
- `app/src/main/java/com/nshaddox/randomtask/ui/screens/settings/SettingsViewModel.kt` - ViewModel with `setTheme()`, `setSortOrder()` methods

## Implementation Notes

- Followed existing `DatabaseModule` pattern for Hilt singleton setup
- Enum values (`AppTheme`, `SortOrder`) serialized as `.name` strings in DataStore with safe fallback via `enumValueOf<T>()`
- ViewModel uses injected `@Named("IO")` dispatcher—no hardcoded `Dispatchers.IO`
- Extracted `Preferences.readEnum()` helper extension to reduce duplication
- Test scaffold uses in-memory DataStore with `PreferenceDataStoreFactory` + `StandardTestDispatcher`
- Tested persistence across VM re-creation, theme/sort order updates, and default fallbacks

## Verification

- Build: `./gradlew assembleDebug` — PASS
- Lint: `./gradlew lintDebug` — PASS
- Tests: `./gradlew testDebugUnitTest` — PASS (4 new tests: initial state, setTheme, setSortOrder, persistence)
- All pre-commit hooks pass (lintDebug + testDebugUnitTest)
