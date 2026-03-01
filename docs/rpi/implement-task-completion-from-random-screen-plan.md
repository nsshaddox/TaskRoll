# Implementation Plan: Implement Task Completion from Random Screen

**Research**: [implement-task-completion-from-random-screen-research.md](./implement-task-completion-from-random-screen-research.md)
**Created**: 2026-02-28

## Overview

Wire up the "Complete Task" button on the random task screen so that tapping it: (1) marks the task as completed in the database (already works), (2) navigates the user back to the task list screen, and (3) shows a "Task completed!" Snackbar on the task list screen. The core persistence logic already exists in `RandomTaskViewModel.completeTask()` -- the remaining work is changing the success path from "load next random task" to "signal completion", then having the UI react by navigating back and passing a result to the task list screen via `savedStateHandle`.

### Key Decisions
- Use `taskCompleted: Boolean` flag in `RandomTaskUiState` as a one-shot event signal (consumed and reset after UI reacts)
- Use Navigation Compose `savedStateHandle` to pass the completion result back to the task list screen (Approach B from research -- better UX than showing Snackbar on random task screen)
- Show Snackbar on the task list screen using the existing `SnackbarHostState` pattern already established in `TaskListScreen`
- Set the saved state handle result *before* calling `popBackStack()` to avoid race conditions

### Approach
Modify the ViewModel's `completeTask()` success path, add a `taskCompleted` flag to UI state, update the stateful `RandomTaskScreen` composable to observe the flag and navigate back with a result, and update the stateful `TaskListScreen` composable to observe the result and show a Snackbar. Update existing tests and add new ones.

## Tasks

**Task 1.0** [ ]: Add `taskCompleted` flag to `RandomTaskUiState`
- **Sub-tasks**:
  - [ ] 1.1: Add `val taskCompleted: Boolean = false` field to the `RandomTaskUiState` data class
- **Files**: `app/src/main/java/com/nshaddox/randomtask/ui/screens/randomtask/RandomTaskUiState.kt` -- modify
- **Approach**: Add the new field with a default of `false` after the existing `noTasksAvailable` field. This flag signals that a task was just successfully completed and the UI should react (navigate back).
- **Verify**: `./gradlew assembleDebug` succeeds
- **Depends**: None

**Task 2.0** [ ]: Update `RandomTaskViewModel.completeTask()` to signal completion instead of loading next task
- **Sub-tasks**:
  - [ ] 2.1: Change `completeTask()` success path from calling `loadRandomTaskInternal()` to setting `_uiState.update { it.copy(isLoading = false, taskCompleted = true) }`
  - [ ] 2.2: Add `resetTaskCompleted()` function that sets `_uiState.update { it.copy(taskCompleted = false) }` -- called by UI after consuming the event
- **Files**: `app/src/main/java/com/nshaddox/randomtask/ui/screens/randomtask/RandomTaskViewModel.kt` -- modify
- **Approach**: In `completeTask()`, replace `.onSuccess { loadRandomTaskInternal() }` with `.onSuccess { _uiState.update { it.copy(isLoading = false, taskCompleted = true) } }`. Add a new public `resetTaskCompleted()` function.
- **Verify**: `./gradlew assembleDebug` succeeds
- **Depends**: Task 1.0

**Task 3.0** [ ]: Update `RandomTaskScreen` to navigate back on task completion with result
- **Sub-tasks**:
  - [ ] 3.1: Add a `LaunchedEffect` keyed on `uiState.taskCompleted` that, when true: sets `"task_completed"` to `true` on `navController.previousBackStackEntry?.savedStateHandle`, calls `navController.popBackStack()`, and calls `viewModel.resetTaskCompleted()`
- **Files**: `app/src/main/java/com/nshaddox/randomtask/ui/screens/randomtask/RandomTaskScreen.kt` -- modify
- **Approach**: In the stateful `RandomTaskScreen` composable, after collecting `uiState`, add a `LaunchedEffect(uiState.taskCompleted)` block. When the flag is `true`, set the saved state handle result before popping the back stack to avoid race conditions.
- **Verify**: `./gradlew assembleDebug` succeeds
- **Depends**: Task 2.0

**Task 4.0** [ ]: Update `TaskListScreen` to observe completion result and show Snackbar
- **Sub-tasks**:
  - [ ] 4.1: In the stateful `TaskListScreen` composable, observe `navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<Boolean>("task_completed", false)` and collect it as state
  - [ ] 4.2: Add a `LaunchedEffect` keyed on the `taskCompleted` value that, when true: shows a Snackbar with "Task completed!" message, then clears the saved state handle entry by setting `"task_completed"` to `false`
  - [ ] 4.3: Pass the `snackbarHostState` into the stateless composable (or hoist the Snackbar logic in the stateful wrapper)
- **Files**: `app/src/main/java/com/nshaddox/randomtask/ui/screens/tasklist/TaskListScreen.kt` -- modify
- **Approach**: The stateless `TaskListScreen` already receives error Snackbar handling. Move `SnackbarHostState` creation up to the stateful wrapper so both the completion Snackbar and the error Snackbar can share it. Observe the saved state handle flow in the stateful wrapper and trigger the Snackbar there. Pass the `snackbarHostState` down to the stateless composable as a parameter.
- **Verify**: `./gradlew assembleDebug` succeeds
- **Depends**: Task 3.0

**Task 5.0** [ ]: Update existing tests and add new tests for task completion flow
- **Sub-tasks**:
  - [ ] 5.1: Update `completeTask marks current task complete and loads next random task` test in `RandomTaskViewModelTest.kt` -- change assertion from expecting a new random task to expecting `taskCompleted = true` and no new task loaded
  - [ ] 5.2: Add test: `completeTask sets taskCompleted flag to true on success` -- verify the flag is set after completion
  - [ ] 5.3: Add test: `resetTaskCompleted clears the taskCompleted flag` -- call `resetTaskCompleted()` after completion and verify flag is `false`
  - [ ] 5.4: Add test: `completeTask failure does not set taskCompleted flag` -- verify that on failure, `taskCompleted` remains `false` and `error` is set
- **Files**: `app/src/test/java/com/nshaddox/randomtask/ui/screens/randomtask/RandomTaskViewModelTest.kt` -- modify
- **Approach**: Follow existing test patterns using `FakeTaskRepository`, `StandardTestDispatcher`, `advanceUntilIdle()`, and direct state assertions. For the failure test, use an error repository (already exists in the test file) to simulate `CompleteTaskUseCase` failure.
- **Verify**: `./gradlew test` passes with all tests green
- **Depends**: Task 2.0

## Success Criteria

- Tapping "Complete Task" on the random task screen calls `viewModel.completeTask()`
- Task `isCompleted` is set to `true` in the database (already working, unchanged)
- User is navigated back to the task list screen after completion
- Completed task no longer appears in the incomplete tasks list (already working via `getIncompleteTasks()`)
- Snackbar with "Task completed!" appears on the task list screen
- `./gradlew test` passes with all existing and new tests green
- `./gradlew assembleDebug` passes
