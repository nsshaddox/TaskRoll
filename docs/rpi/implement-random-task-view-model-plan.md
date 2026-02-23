# Implementation Plan: Implement RandomTaskViewModel

**Research**: [implement-random-task-view-model-research.md](./implement-random-task-view-model-research.md)
**Created**: 2026-02-22

## Overview

Implement `RandomTaskViewModel` as a `@HiltViewModel` in the `ui.screens.randomtask` package. The ViewModel injects `GetRandomTaskUseCase` and `CompleteTaskUseCase`, exposes a `StateFlow<RandomTaskUiState>`, and provides `loadRandomTask()`, `completeTask()`, and `skipTask()` functions. A prerequisite `RandomTaskUiState` data class (issue #54) must be created first. Unit tests use `FakeTaskRepository`, `StandardTestDispatcher`, and Turbine.

### Key Decisions
- Create `RandomTaskUiState` as part of this implementation (issue #54 prerequisite, trivial ~10-line data class)
- Use `MutableStateFlow` with `asStateFlow()` (not `stateIn()`) since state is mutated imperatively by user actions rather than derived from a single upstream flow
- Use try/catch for `GetRandomTaskUseCase` error handling (returns raw `Task?`, not `Result`)
- Use `Result.onSuccess/onFailure` for `CompleteTaskUseCase` (returns `Result<Unit>`)
- Inject `@Named("IO")` dispatcher following the `SampleViewModel` pattern
- Test with real use case instances backed by `FakeTaskRepository` (no mocking), matching existing test conventions

### Approach
Create the UI state data class, implement the ViewModel with three public functions, then write comprehensive unit tests covering all state transitions and error paths.

## Tasks

**Task 1.0** [ ]: Create RandomTaskUiState data class
- **Sub-tasks**:
  - [ ] 1.1: Create `RandomTaskUiState.kt` in `ui.screens.randomtask` package
  - [ ] 1.2: Define data class with `currentTask: Task?`, `isLoading: Boolean`, `error: String?`, `noTasksAvailable: Boolean` — all with sensible defaults
- **Files**: `app/src/main/java/com/nshaddox/randomtask/ui/screens/randomtask/RandomTaskUiState.kt` — create new file
- **Approach**: Data class with default values: `currentTask = null`, `isLoading = false`, `error = null`, `noTasksAvailable = false`. Import `domain.model.Task`.
- **Verify**: `./gradlew assembleDebug` succeeds
- **Depends**: None

**Task 2.0** [ ]: Implement RandomTaskViewModel class
- **Sub-tasks**:
  - [ ] 2.1: Create `RandomTaskViewModel.kt` in `ui.screens.randomtask` package with `@HiltViewModel` annotation
  - [ ] 2.2: Inject `GetRandomTaskUseCase`, `CompleteTaskUseCase`, and `@Named("IO") CoroutineDispatcher` via constructor
  - [ ] 2.3: Expose `uiState: StateFlow<RandomTaskUiState>` backed by private `MutableStateFlow`
  - [ ] 2.4: Implement `loadRandomTask()` — sets loading, calls `GetRandomTaskUseCase`, updates state with task or `noTasksAvailable`, handles exceptions
  - [ ] 2.5: Implement `completeTask()` — no-ops if no current task, sets loading, calls `CompleteTaskUseCase`, on success loads next random task, on failure sets error
  - [ ] 2.6: Implement `skipTask()` — loads a new random task without completing the current one
  - [ ] 2.7: Extract `loadRandomTaskInternal()` private suspend function to avoid duplication between `loadRandomTask()`, `completeTask()`, and `skipTask()`
- **Files**: `app/src/main/java/com/nshaddox/randomtask/ui/screens/randomtask/RandomTaskViewModel.kt` — create new file
- **Approach**: Follow `SampleViewModel` pattern for Hilt/dispatcher injection. Use `viewModelScope.launch(ioDispatcher)` for all coroutine work. Use `_uiState.update {}` for thread-safe state mutations. `loadRandomTask()` and `skipTask()` delegate to `loadRandomTaskInternal()`. `completeTask()` calls `completeTaskUseCase` then chains to `loadRandomTaskInternal()` on success.
- **Verify**: `./gradlew assembleDebug` succeeds
- **Depends**: Task 1.0

**Task 3.0** [ ]: Write unit tests for RandomTaskViewModel
- **Sub-tasks**:
  - [ ] 3.1: Create `RandomTaskViewModelTest.kt` in test `ui.screens.randomtask` package with setup/teardown using `StandardTestDispatcher` and `Dispatchers.setMain/resetMain`
  - [ ] 3.2: Test initial state — `currentTask` is null, `isLoading` is false, no error, `noTasksAvailable` is false
  - [ ] 3.3: Test `loadRandomTask()` returns a task when incomplete tasks exist
  - [ ] 3.4: Test `loadRandomTask()` sets `noTasksAvailable = true` when no incomplete tasks exist
  - [ ] 3.5: Test `completeTask()` marks current task complete and loads next random task
  - [ ] 3.6: Test `completeTask()` does nothing when no current task is set
  - [ ] 3.7: Test `skipTask()` loads a new random task without completing current one
  - [ ] 3.8: Test error handling when `GetRandomTaskUseCase` throws an exception
- **Files**: `app/src/test/java/com/nshaddox/randomtask/ui/screens/randomtask/RandomTaskViewModelTest.kt` — create new file
- **Approach**: Follow `SampleViewModelTest` pattern: `@OptIn(ExperimentalCoroutinesApi::class)`, `StandardTestDispatcher`, `Dispatchers.setMain/resetMain` in `@Before/@After`. Construct real `GetRandomTaskUseCase` and `CompleteTaskUseCase` with `FakeTaskRepository`. Use Turbine (`uiState.test {}`) for flow assertions. Pre-populate repository with tasks as needed per test case.
- **Verify**: `./gradlew test` passes with all new tests green
- **Depends**: Task 1.0, Task 2.0

## Success Criteria

- `RandomTaskUiState.kt` exists in `ui.screens.randomtask` with correct fields and defaults
- `RandomTaskViewModel.kt` exists with `@HiltViewModel`, correct constructor injections, `StateFlow<RandomTaskUiState>` exposed
- `loadRandomTask()`, `completeTask()`, `skipTask()` implemented with proper error handling
- All 7+ unit tests pass covering: initial state, load success, load empty, complete success, complete no-op, skip, error handling
- `./gradlew test` passes
- `./gradlew assembleDebug` passes
