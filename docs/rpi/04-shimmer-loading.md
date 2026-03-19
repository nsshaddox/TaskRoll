# Shimmer Loading (Themed Loading Indicator)

**Implemented**: 2026-03-18
**Complexity**: simple

## What Changed

- Created `ThemedLoadingIndicator.kt` — new reusable composable component with two pure helper functions (`loadingIndicatorColorForVariant`, `loadingIndicatorStrokeWidthForVariant`)
- Replaced `CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)` on TaskListScreen, RandomTaskScreen, and CompletedTasksScreen with theme-aware `ThemedLoadingIndicator()`
- Fixed Vapor theme spec compliance: loading spinner now correctly uses `vaporAccentTeal` (0xFF5EEAD4) instead of `vaporAccentPink`

## Why

The old `CircularProgressIndicator` with hardcoded `MaterialTheme.colorScheme.primary` did not respect per-theme spinner specs defined in DESIGN_SYSTEM.md §5.8. Vapor theme violated its spec by mapping primary to pink instead of teal. Creating a themed component ensures all three themes render the correct colors and stroke widths as specified.

## Key Files

- `app/src/main/java/com/nshaddox/randomtask/ui/components/ThemedLoadingIndicator.kt` - Pure functions and composable for themed loading spinner
- `app/src/test/java/com/nshaddox/randomtask/ui/components/ThemedLoadingIndicatorTest.kt` - 6 JUnit tests covering all variants
- `app/src/main/java/com/nshaddox/randomtask/ui/screens/tasklist/TaskListScreen.kt:296` - Swapped spinner, updated imports
- `app/src/main/java/com/nshaddox/randomtask/ui/screens/randomtask/RandomTaskScreen.kt:188` - Swapped spinner, updated imports
- `app/src/main/java/com/nshaddox/randomtask/ui/screens/completedtasks/CompletedTasksScreen.kt:197` - Swapped spinner, updated imports

## Implementation Notes

- Followed `ThemedFAB` pattern: pure functions tested independently, composable reads `LocalThemeVariant.current` and delegates
- Per-theme specifications: Obsidian (teal/4dp), Neo Brutalist (pink/4dp), Vapor (teal/2dp)
- No `when` block in composable body—all themes use `CircularProgressIndicator`, only color and stroke width differ
- All imports use named color constants from `ui/theme/Color.kt`, no hardcoded hex values

## Verification

- Tests: 6 passing unit tests for color and stroke width across all variants
- Quality: `./gradlew lintDebug` passes with no unused import warnings
- Build: `./gradlew assembleDebug` successful, clean debug APK generated
- Full suite: `./gradlew testDebugUnitTest` all unit tests pass
