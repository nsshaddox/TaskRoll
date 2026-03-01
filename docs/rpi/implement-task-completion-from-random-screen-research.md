# Research: Implement Task Completion from Random Screen (Issue #69)

**Date**: 2026-02-28
**Complexity**: simple
**Status**: Research complete

---

## Summary

Add behavior so that when the user taps "Complete Task" on the random task screen, the task is marked complete in the database, the user is navigated back to the task list, and success feedback is shown via a Snackbar. The core completion logic (`completeTask()`) already exists in `RandomTaskViewModel`; the remaining work is adding navigation-back-on-success and a Snackbar/toast notification.

**Dependencies**: Issue #61 (Implement RandomTaskScreen composable) -- CLOSED/complete.

---

## FAR Assessment

| Dimension     | Score | Rationale |
|---------------|-------|-----------|
| **Feasibility** | 10/10 | All infrastructure is in place. `CompleteTaskUseCase` marks the task as `isCompleted = true` and persists via `TaskRepository.updateTask()`. `RandomTaskViewModel.completeTask()` already calls this use case and handles success/failure. The Room DAO `updateTask()` method is working. The `GetRandomTaskUseCase` already filters by incomplete tasks only. The only new behavior is: (1) emitting a "task completed" event to trigger navigation + feedback, and (2) the UI reacting to it. |
| **Accuracy**    | 9/10  | The acceptance criteria are precise and map cleanly to existing code. The only ambiguity is the exact feedback mechanism (toast vs. Snackbar) -- the task list screen already uses Snackbar via `SnackbarHostState`, establishing the project pattern. One minor consideration: the current `completeTask()` loads the next random task on success rather than signaling navigation back. This behavior needs to change to navigate back instead. |
| **Readiness**   | 10/10 | Codebase builds clean (`./gradlew assembleDebug` passes). All 10+ unit tests pass (`./gradlew test`). The `RandomTaskViewModel` already has `completeTask()` with full test coverage including success, failure, and no-current-task edge cases. `FakeTaskRepository` is available for testing. The existing `RandomTaskViewModelTest` provides the exact test pattern to extend. |

**FAR Mean Score: 9.7/10**

---

## Key Findings

1. **`completeTask()` already works end-to-end for persistence**: `RandomTaskViewModel.completeTask()` calls `CompleteTaskUseCase`, which sets `isCompleted = true` and `updatedAt = now`, then calls `repository.updateTask()`. The Room DAO persists the change. `GetRandomTaskUseCase` and `TaskDao.getIncompleteTasks()` already filter by `is_completed = 0`, so a completed task automatically disappears from the incomplete tasks list. **No data layer changes are needed.**

2. **Current behavior loads next random task instead of navigating back**: After successful completion, `completeTask()` calls `loadRandomTaskInternal()` to fetch another random task. Per the acceptance criteria, the behavior should instead navigate the user back to the task list with success feedback. This requires changing the success path from "load next task" to "emit a completion event."

3. **Navigation-back requires a one-shot event pattern**: The stateful `RandomTaskScreen` composable calls `navController.popBackStack()` for back navigation. To trigger navigation from the ViewModel, a one-shot event (e.g., a `taskCompleted: Boolean` flag in `RandomTaskUiState`, or a `SharedFlow` side-effect channel) is needed. The simplest approach consistent with the existing codebase is adding a `taskCompleted` flag to `RandomTaskUiState` and consuming it in a `LaunchedEffect`.

4. **Snackbar feedback follows the existing TaskListScreen pattern**: `TaskListScreen` already uses `SnackbarHostState` with `LaunchedEffect(errorMessage)` to show error snackbars. The same pattern can be applied in `RandomTaskScreen` for success feedback. However, since the user navigates back to the task list, the snackbar should appear on the task list screen, not the random task screen. This can be achieved by passing a result back via the navigation saved state handle.

---

## Implementation Approach

### Changes Required

**Approach A (Simpler -- Snackbar on random task screen before navigating back):**
This approach briefly shows a Snackbar on the random task screen then navigates back. It is simpler but the user may not see the snackbar if navigation is fast.

**Approach B (Better UX -- Navigate back and show Snackbar on task list screen):**
Pass a result back to the task list screen via `NavController.previousBackStackEntry?.savedStateHandle`. The task list screen observes this result and shows a Snackbar. This is the standard Compose Navigation pattern for returning results.

**Recommended: Approach B** -- This is the better UX and follows Compose Navigation best practices.

### Files to Modify

1. **`RandomTaskUiState.kt`** -- Add `taskCompleted: Boolean = false` field to signal that a task was just completed successfully.

2. **`RandomTaskViewModel.kt`** -- Change `completeTask()` success path: instead of calling `loadRandomTaskInternal()`, set `_uiState.update { it.copy(isLoading = false, taskCompleted = true) }`. Add a `resetTaskCompleted()` function to clear the flag after the UI consumes it.

3. **`RandomTaskScreen.kt`** -- In the stateful `RandomTaskScreen` composable:
   - Observe `uiState.taskCompleted` in a `LaunchedEffect`
   - When true, set a result on the previous back stack entry's saved state handle: `navController.previousBackStackEntry?.savedStateHandle?.set("task_completed", true)`
   - Call `navController.popBackStack()`
   - Call `viewModel.resetTaskCompleted()`

4. **`TaskListScreen.kt`** -- In the stateful `TaskListScreen` composable:
   - Observe `navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("task_completed")` or use `savedStateHandle.getStateFlow()`
   - When the result is `true`, show a Snackbar with "Task completed!" message
   - Clear the saved state handle entry after consuming

5. **`RandomTaskViewModelTest.kt`** -- Update existing `completeTask` tests:
   - `completeTask marks current task complete and sets taskCompleted flag` (update existing test)
   - Add test for `resetTaskCompleted()` clearing the flag
   - Verify the task is actually persisted as completed in the repository

### No New Files Needed

All changes are modifications to existing files. No new Kotlin files, DAOs, entities, use cases, or modules are required.

---

## Risks & Considerations

- **Saved state handle approach requires `LiveData` or `StateFlow` observation**: The `savedStateHandle` in Navigation Compose uses `LiveData` by default. Using `getStateFlow()` requires the `lifecycle-viewmodel-compose` dependency which should already be present via Hilt navigation compose.
- **One-shot event consumption**: The `taskCompleted` flag in `RandomTaskUiState` must be consumed (reset) after the UI reacts to it, to prevent re-triggering on recomposition. The `LaunchedEffect` keyed on `uiState.taskCompleted` handles this cleanly.
- **Race condition on fast navigation**: If `popBackStack()` is called before the Snackbar result is set on the saved state handle, the task list screen might not receive it. Setting the result before popping avoids this.
- **Existing test changes**: The current `completeTask marks current task complete and loads next random task` test expects the ViewModel to load the next random task after completion. This test assertion must change to expect `taskCompleted = true` instead.

---

## Verification Checklist

- [ ] Tapping "Complete Task" calls `viewModel.completeTask()`
- [ ] Task `isCompleted` is set to `true` in the database
- [ ] User is navigated back to the task list screen
- [ ] Completed task no longer appears in the incomplete tasks list
- [ ] Snackbar with "Task completed!" appears on the task list screen
- [ ] `./gradlew test` passes with updated/new tests
- [ ] `./gradlew assembleDebug` passes
