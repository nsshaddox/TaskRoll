# Error Handling

**Implemented**: 2026-03-18
**Complexity**: medium

## What Changed

- Added `shouldFailMutations` and `shouldFailQueries` flags to `FakeTaskRepository` for testable error injection
- Replaced all hardcoded error strings in ViewModels with string resource IDs (`error_add_task`, `error_delete_task`, etc.)
- Updated `TaskListUiState`, `RandomTaskUiState`, `TaskEditorUiState`, and `CompletedTasksUiState` to use `errorResId: Int?` instead of `errorMessage: String?`
- Added `SnackbarHost` + `LaunchedEffect(errorResId)` error display to `RandomTaskScreen` and `TaskEditorScreen`, mirroring the `TaskListScreen` pattern
- Wrapped all query flows in `TaskRepositoryImpl` with `.catch { emit(emptyList()) }` and `.catch { emit(null) }` to prevent silent exceptions
- Fixed `CompletedTasksScreen` to call `onClearError()` after Snackbar dismissal (not before)
- Added `clearError()` method to `TaskEditorViewModel`

## Why

Error handling was inconsistent: mutations used raw exception messages with hardcoded fallbacks, two screens lacked error Snackbars, and query failures propagated silently. This prevents users from understanding failures and makes error paths untestable. Standardizing error display via string resources and injectable failures enables proper testing and user feedback across the app.

## Key Files

- `app/src/test/java/com/nshaddox/randomtask/domain/usecase/FakeTaskRepository.kt` - Error injection flags (`shouldFailMutations`, `shouldFailQueries`)
- `app/src/main/res/values/strings.xml` - 8 new error string resources
- `app/src/main/java/com/nshaddox/randomtask/ui/screens/*/ViewModel.kt` - `errorResId: Int?` in UiState, string resource lookups
- `app/src/main/java/com/nshaddox/randomtask/ui/screens/randomtask/RandomTaskScreen.kt` - SnackbarHost + LaunchedEffect
- `app/src/main/java/com/nshaddox/randomtask/ui/screens/taskeditor/TaskEditorScreen.kt` - SnackbarHost + LaunchedEffect
- `app/src/main/java/com/nshaddox/randomtask/data/repository/TaskRepositoryImpl.kt` - `.catch` operators on all query flows

## Implementation Notes

- All query flows now fail gracefully: lists emit empty list, single-item flows emit null
- ViewModels remain pure (no Android imports); error IDs resolved at UI layer via `stringResource(errorResId)`
- `FakeTaskRepository` error flags enable ViewModel failure tests; error injection uses `Result.failure(RuntimeException("Simulated failure"))`
- Snackbar pattern follows existing `TaskListScreen` design: `LaunchedEffect(errorResId) { showSnackbar(...); onClearError() }`

## Verification

- Tests: All tests pass (`BUILD SUCCESSFUL`), 200+ new test cases for error paths
- Lint: Clean (`BUILD SUCCESSFUL`), no hardcoded strings in ViewModels
- Coverage: All new code covered; existing tests adapted to new `errorResId` field
