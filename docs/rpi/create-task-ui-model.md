# create-task-ui-model

**Implemented**: 2026-02-22
**Complexity**: simple

## What Changed

- Added `TaskUiModel` data class in `ui.screens.tasklist` package with pre-formatted date strings
- Added `Task.toUiModel()` extension mapper using `SimpleDateFormat` with `Locale.US`
- Added unit tests covering field mapping, null description, epoch 0, and known timestamp formatting

## Why

The domain `Task` model stores timestamps as raw epoch millis, which are not display-ready. `TaskUiModel` bridges the domain layer to the UI layer, keeping date formatting logic out of Composables and enabling the task list screen to render human-readable dates.

## Key Files

- `app/src/main/java/com/nshaddox/randomtask/ui/screens/tasklist/TaskUiModel.kt` - UI model data class with String date fields
- `app/src/main/java/com/nshaddox/randomtask/ui/screens/tasklist/TaskUiMappers.kt` - `Task.toUiModel()` extension and private `formatTimestamp` helper
- `app/src/test/java/com/nshaddox/randomtask/ui/screens/tasklist/TaskUiMappersTest.kt` - 4 unit tests for the mapper

## Implementation Notes

- Followed the existing `TaskMappers.kt` extension-function pattern for consistency
- Used `SimpleDateFormat` because core library desugaring is not enabled (minSdk 24)
- Fixed format `"MMM d, yyyy h:mm a"` with `Locale.US` to avoid locale-dependent test flakiness
- Tests pin timezone to UTC via `@Before`/`@After` for deterministic assertions

## Verification

- [x] Tests: `./gradlew test` -- BUILD SUCCESSFUL (all 4 mapper tests pass)
- [x] Quality: `./gradlew assembleDebug` -- BUILD SUCCESSFUL
- [x] Manual: All plan tasks marked complete; files match acceptance criteria
