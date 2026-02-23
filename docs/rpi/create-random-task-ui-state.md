# create-random-task-ui-state

**Implemented**: 2026-02-22
**Complexity**: simple

## What Changed

- Added `RandomTaskUiState` data class for the random task screen's UI state
- Added unit tests covering default values, copy semantics, and equality

## Why

The random task screen needs a unified state object to drive its UI through a ViewModel. This data class replaces ad-hoc parameter passing with a single state holder containing `currentTask`, `isLoading`, `error`, and `noTasksAvailable` fields, following the MVVM pattern established in the project.

## Key Files

- `app/src/main/java/com/nshaddox/randomtask/ui/screens/randomtask/RandomTaskUiState.kt` - New data class with four defaulted fields and KDoc
- `app/src/test/java/com/nshaddox/randomtask/ui/screens/randomtask/RandomTaskUiStateTest.kt` - Seven unit tests for defaults, copy, equality, and inequality

## Implementation Notes

- Followed `Task.kt` KDoc pattern with `@property` tags for all fields
- References the domain `Task` model, not the preview mock
- All fields have default values so the future ViewModel can start with `RandomTaskUiState()`
- Tests use JUnit4 with backtick-quoted names matching existing test conventions

## Verification

- [x] Tests: `./gradlew test` -- BUILD SUCCESSFUL (all unit tests pass)
- [x] Quality: `./gradlew assembleDebug` -- BUILD SUCCESSFUL
- [x] Manual: File exists at correct package path, data class has four fields with correct types and defaults
