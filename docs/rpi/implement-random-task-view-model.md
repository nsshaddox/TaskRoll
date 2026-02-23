# implement-random-task-view-model

**Implemented**: 2026-02-22
**Complexity**: medium

## What Changed

- Created `RandomTaskUiState` data class with `currentTask`, `isLoading`, `error`, and `noTasksAvailable` fields (issue #54)
- Implemented `RandomTaskViewModel` as a `@HiltViewModel` with `loadRandomTask()`, `completeTask()`, and `skipTask()` functions (issue #58)
- Added 7 unit tests covering all state transitions, error paths, and edge cases

## Why

The random task selection screen needs a ViewModel to manage UI state and coordinate between the domain layer use cases (`GetRandomTaskUseCase`, `CompleteTaskUseCase`) and the Compose UI. This is a prerequisite for wiring the screen to live data (issue #61).

## Key Files

- `app/src/main/java/.../ui/screens/randomtask/RandomTaskUiState.kt` - UI state data class with sensible defaults
- `app/src/main/java/.../ui/screens/randomtask/RandomTaskViewModel.kt` - Hilt ViewModel with three public actions and shared internal loader
- `app/src/test/java/.../ui/screens/randomtask/RandomTaskViewModelTest.kt` - 7 unit tests using FakeTaskRepository and Turbine

## Implementation Notes

- Followed `SampleViewModel` pattern for Hilt/dispatcher injection (`@Named("IO")`)
- Used `MutableStateFlow` with `asStateFlow()` (not `stateIn()`) since state is mutated imperatively
- Extracted `loadRandomTaskInternal()` to avoid duplication across `loadRandomTask()`, `completeTask()`, and `skipTask()`
- Tests use real use case instances with `FakeTaskRepository` (no mocking), matching existing conventions

## Verification

- [x] Tests: 7/7 passed (`./gradlew test` BUILD SUCCESSFUL)
- [x] Quality: `./gradlew assembleDebug` BUILD SUCCESSFUL
- [x] Manual: All plan tasks verified -- UI state, ViewModel, and test files created per spec
