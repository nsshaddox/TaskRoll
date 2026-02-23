# implement-task-list-view-model

**Implemented**: 2026-02-22
**Complexity**: medium

## What Changed

- Added `TaskListUiState` data class with loading, error, and dialog visibility fields
- Added `TaskListViewModel` with add/delete/toggle-completion/dialog/error operations
- Updated `TaskListScreen` and `RandomTaskScreen` to use domain `Task` instead of preview `Task`
- Updated `SampleData` mock data to provide domain `Task` instances with timestamps
- Removed the preview-only `Task` data class from `MockData.kt`
- Added 11 unit tests covering all ViewModel operations and state transitions

## Why

The TaskListScreen was a static wireframe using mock preview models. The ViewModel wires it
to the domain layer, enabling real task CRUD operations through existing use cases and the
Room-backed repository. This is a prerequisite for a functional task list feature.

## Key Files

- `app/.../ui/screens/tasklist/TaskListUiState.kt` - NEW: UI state data class
- `app/.../ui/screens/tasklist/TaskListViewModel.kt` - NEW: Hilt ViewModel with 5 use cases
- `app/.../ui/screens/tasklist/TaskListScreen.kt` - Import changed to domain Task
- `app/.../ui/screens/randomtask/RandomTaskScreen.kt` - Import changed to domain Task
- `app/.../ui/preview/MockData.kt` - SampleData now uses domain Task; preview Task class removed
- `app/src/test/.../tasklist/TaskListViewModelTest.kt` - NEW: 11 unit tests with Turbine

## Implementation Notes

- Followed `SampleViewModel` patterns: `@HiltViewModel`, `@Named("IO")` dispatcher, `MutableStateFlow`
- Uses `CompleteTaskUseCase` for incomplete-to-complete, `UpdateTaskUseCase` for complete-to-incomplete
- `addTask` success auto-hides the add dialog; task list updates reactively via repository flow

## Verification

- [x] Tests: `./gradlew testDebugUnitTest` -- BUILD SUCCESSFUL (11 tests pass)
- [x] Quality: `./gradlew assembleDebug` -- BUILD SUCCESSFUL
- [x] Manual: All plan tasks (1.0-5.0) marked complete, code reviewed against plan
