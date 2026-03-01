# implement-edit-task-functionality

**Implemented**: 2026-02-28
**Complexity**: medium (from research phase)

## What Changed

- Added `EditTask` navigation route with `taskId` argument to `Screen` sealed class and `NavGraph`
- Created `TaskEditorViewModel` to load a task by ID, manage edit state, and save via `UpdateTaskUseCase`
- Added `EditTaskScreen` ViewModel-connected wrapper that delegates to the existing stateless `TaskEditorScreen`
- Added edit icon (pencil) to `TaskCard` next to the delete button, wired to navigate to the edit route
- Added blank-title validation to `UpdateTaskUseCase` to match `AddTaskUseCase` behavior

## Why

Users could not edit existing tasks -- they had to delete and recreate them. This adds an edit button on each task card that navigates to a pre-populated editor screen, allowing in-place title updates persisted through Room.

## Key Files

- `app/.../domain/usecase/UpdateTaskUseCase.kt` - Added blank-title guard before repository call
- `app/.../ui/navigation/NavRoutes.kt` - Added `EditTask` screen with `createRoute(taskId)` helper
- `app/.../ui/navigation/NavGraph.kt` - Registered `EditTask` composable destination with Long argument
- `app/.../ui/screens/taskeditor/TaskEditorViewModel.kt` - New ViewModel: loads task, manages title state, saves
- `app/.../ui/screens/taskeditor/TaskEditorScreen.kt` - Added `EditTaskScreen` stateful wrapper
- `app/.../ui/screens/tasklist/TaskListScreen.kt` - Added edit icon and `onEditTask` callback threading

## Implementation Notes

- Followed existing MVVM pattern: `@HiltViewModel` with `SavedStateHandle` for nav args
- Reused stateless `TaskEditorScreen` composable -- no changes to the form UI itself
- `LaunchedEffect(uiState.isSaved)` triggers navigation back on successful save

## Verification

- [x] Tests: `UpdateTaskUseCaseTest` (3 tests) and `TaskEditorViewModelTest` (6 tests) all pass
- [x] Quality: `./gradlew assembleDebug` builds successfully
- [x] Manual: All unit tests pass via `./gradlew testDebugUnitTest`
