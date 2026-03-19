# Interaction Polish (Animations + Haptics)

**Implemented**: 2026-03-18
**Complexity**: medium

## What Changed

- Added theme-aware animation duration helper (`200/250/300ms` for Obsidian/Neo Brutalist/Vapor)
- Implemented scale animations on checkbox toggle and FAB button press via `animateFloatAsState`
- Added list item enter/exit animations with `animateItem()` in TaskListScreen and CompletedTasksScreen
- Implemented screen transition animations (slide + fade) in NavGraph with unified 300ms duration
- Added global haptic feedback toggle to settings, persisted via DataStore
- Integrated haptic feedback on checkbox, FAB, and swipe-to-delete using `LocalHapticFeedback` and `Vibrator` APIs
- Added VIBRATE permission to AndroidManifest

## Why

GH#92 and GH#105 requested improved interaction feedback to enhance perceived responsiveness and polish. Theme-differentiated animation durations respect the design system (fast for Obsidian, flowing for Vapor). Haptic feedback improves tactile confirmation without relying solely on visual cues. A global toggle respects user preferences and accessibility needs.

## Key Files

- `ui/theme/AnimationTokens.kt` - Theme-aware duration helper (200/250/300ms) and NAV_TRANSITION_DURATION_MS constant
- `ui/screens/settings/SettingsUiState.kt` - Extended with `hapticEnabled: Boolean = true`
- `ui/screens/settings/SettingsViewModel.kt` - Added `setHapticEnabled()` with DataStore persistence
- `ui/screens/settings/SettingsScreen.kt` - Added haptic toggle row UI
- `ui/components/ThemedCheckbox.kt` - Added scale animation + haptic on toggle
- `ui/components/ThemedFAB.kt` - Added press-scale animation + haptic on click
- `ui/screens/tasklist/TaskListScreen.kt` - Added `animateItem()` + swipe-to-delete vibration
- `ui/screens/completedtasks/CompletedTasksScreen.kt` - Added `animateItem()` + swipe-to-delete vibration
- `ui/navigation/NavGraph.kt` - Added enter/exit/popEnter/popExit transitions
- `app/src/main/AndroidManifest.xml` - Added VIBRATE permission

## Implementation Notes

- Used pure Kotlin helper function (`animationDurationMs()`) following existing `checkboxCheckedColor` pattern
- Haptic toggle follows DataStore pattern established by `SORT_ORDER_KEY` and `THEME_KEY`
- Checkbox/FAB use `LocalHapticFeedback.performHapticFeedback()` (zero-permission Compose path)
- Swipe-to-delete uses native `Vibrator` with API 26+ branch and fallback to `vibrate(long)` for API 24-25
- Nav transitions use shared constant (not per-theme) to avoid asymmetric jarring transitions
- All animations parameterized by theme variant for future tuning

## Verification

- `./gradlew lintDebug` — PASSED (no lint warnings)
- `./gradlew testDebugUnitTest` — PASSED (all tests including new AnimationTokensTest, SettingsViewModelTest extensions)
- Animation tokens unit tested: 3 theme variants + NAV constant
- Haptic toggle tested for persistence and state emission via Turbine
- Manual testing confirmed: list animations, checkbox/FAB scale, swipe vibration, nav transitions, haptic toggle disabling all feedback
