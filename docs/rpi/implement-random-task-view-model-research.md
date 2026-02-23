# Research: Implement RandomTaskViewModel (Issue #58)

**Date**: 2026-02-22
**Complexity**: medium
**Status**: Research complete

---

## Summary

Implement `RandomTaskViewModel` in the `ui.screens.randomtask` package. This is a `@HiltViewModel` that drives the random task selection screen. It injects `GetRandomTaskUseCase` and `CompleteTaskUseCase`, exposes a `StateFlow<RandomTaskUiState>`, and provides `loadRandomTask()`, `completeTask()`, and `skipTask()` functions. Unit tests are required with mock/fake use cases.

**Dependencies**: Issue #54 (RandomTaskUiState - not yet implemented), Issue #33/#34 reference unclear but #132 (GetRandomTaskUseCase) and #124 (CompleteTaskUseCase) are both complete.

---

## FAR Assessment

| Dimension     | Score | Rationale |
|---------------|-------|-----------|
| **Feasibility** | 9/10  | All runtime dependencies exist: `GetRandomTaskUseCase`, `CompleteTaskUseCase`, `TaskRepository`, Hilt modules, coroutine dispatchers. The only missing piece is `RandomTaskUiState` (issue #54), which is trivially defined as a data class. Existing `SampleViewModel` + `SampleViewModelTest` provide a proven pattern to follow. |
| **Accuracy**    | 9/10  | The issue spec is precise: class name, package, annotations, injected dependencies, exposed state type, and function signatures are all explicitly defined. The existing screen composable (`RandomTaskScreen.kt`) already expects the exact callbacks (`onSelectRandom`, `onCompleteTask`, `onSkipTask`) that map 1:1 to ViewModel functions. |
| **Readiness**   | 8/10  | Codebase builds clean, all 10+ existing tests pass. Test infrastructure (JUnit, Turbine, MockK, coroutines-test, FakeTaskRepository) is fully configured. The only pre-work is creating `RandomTaskUiState` (issue #54), which is a ~10-line data class and can be done inline with this implementation. |

**FAR Mean Score: 8.7/10**

---

## Key Findings

1. **Existing ViewModel pattern is well-established**: `SampleViewModel` demonstrates the exact pattern to follow -- `@HiltViewModel`, constructor-injected use cases, `@Named("IO")` dispatcher injection, `StateFlow` exposed via `stateIn()` with `SharingStarted.WhileSubscribed(5_000)`. The test (`SampleViewModelTest`) shows proper setup with `StandardTestDispatcher`, `Dispatchers.setMain/resetMain`, and Turbine for flow assertions.

2. **RandomTaskUiState (issue #54) must be created first**: The spec calls for a data class with `currentTask: Task?`, `isLoading: Boolean`, `error: String?`, and `noTasksAvailable: Boolean`. This is a prerequisite not yet implemented. It is trivial (~10 lines) and can be created as part of this task or immediately before.

3. **Screen already defines the exact callback interface**: `RandomTaskScreen.kt` accepts `selectedTask: Task?`, `onSelectRandom`, `onCompleteTask`, and `onSkipTask` -- these map directly to the ViewModel's `loadRandomTask()`, `completeTask()`, `skipTask()` functions. However, the screen currently uses the preview `Task` model (`ui.preview.Task`) rather than the domain `Task` model (`domain.model.Task`). This will need to be updated when wiring the ViewModel to the screen (issue #61).

4. **Use case signatures are straightforward**: `GetRandomTaskUseCase.invoke()` is a `suspend` function returning `Task?` (null means no incomplete tasks). `CompleteTaskUseCase.invoke(task)` is a `suspend` function returning `Result<Unit>`. The ViewModel needs to handle both success and failure paths from `CompleteTaskUseCase`, and the null/non-null return from `GetRandomTaskUseCase`.

---

## Implementation Approach

### New Files to Create

1. **`app/src/main/java/com/nshaddox/randomtask/ui/screens/randomtask/RandomTaskUiState.kt`** (issue #54 prerequisite)
   ```kotlin
   package com.nshaddox.randomtask.ui.screens.randomtask

   import com.nshaddox.randomtask.domain.model.Task

   data class RandomTaskUiState(
       val currentTask: Task? = null,
       val isLoading: Boolean = false,
       val error: String? = null,
       val noTasksAvailable: Boolean = false
   )
   ```

2. **`app/src/main/java/com/nshaddox/randomtask/ui/screens/randomtask/RandomTaskViewModel.kt`**
   ```kotlin
   package com.nshaddox.randomtask.ui.screens.randomtask

   import androidx.lifecycle.ViewModel
   import androidx.lifecycle.viewModelScope
   import com.nshaddox.randomtask.domain.usecase.CompleteTaskUseCase
   import com.nshaddox.randomtask.domain.usecase.GetRandomTaskUseCase
   import dagger.hilt.android.lifecycle.HiltViewModel
   import kotlinx.coroutines.CoroutineDispatcher
   import kotlinx.coroutines.flow.MutableStateFlow
   import kotlinx.coroutines.flow.StateFlow
   import kotlinx.coroutines.flow.asStateFlow
   import kotlinx.coroutines.flow.update
   import kotlinx.coroutines.launch
   import javax.inject.Inject
   import javax.inject.Named

   @HiltViewModel
   class RandomTaskViewModel @Inject constructor(
       private val getRandomTaskUseCase: GetRandomTaskUseCase,
       private val completeTaskUseCase: CompleteTaskUseCase,
       @Named("IO") private val ioDispatcher: CoroutineDispatcher
   ) : ViewModel() {

       private val _uiState = MutableStateFlow(RandomTaskUiState())
       val uiState: StateFlow<RandomTaskUiState> = _uiState.asStateFlow()

       fun loadRandomTask() {
           viewModelScope.launch(ioDispatcher) {
               _uiState.update { it.copy(isLoading = true, error = null) }
               try {
                   val task = getRandomTaskUseCase()
                   _uiState.update {
                       it.copy(
                           currentTask = task,
                           isLoading = false,
                           noTasksAvailable = task == null
                       )
                   }
               } catch (e: Exception) {
                   _uiState.update {
                       it.copy(isLoading = false, error = e.message ?: "Unknown error")
                   }
               }
           }
       }

       fun completeTask() {
           val task = _uiState.value.currentTask ?: return
           viewModelScope.launch(ioDispatcher) {
               _uiState.update { it.copy(isLoading = true, error = null) }
               completeTaskUseCase(task)
                   .onSuccess { loadRandomTaskInternal() }
                   .onFailure { e ->
                       _uiState.update {
                           it.copy(isLoading = false, error = e.message ?: "Failed to complete task")
                       }
                   }
           }
       }

       fun skipTask() {
           viewModelScope.launch(ioDispatcher) {
               loadRandomTaskInternal()
           }
       }

       private suspend fun loadRandomTaskInternal() {
           _uiState.update { it.copy(isLoading = true, error = null) }
           try {
               val task = getRandomTaskUseCase()
               _uiState.update {
                   it.copy(
                       currentTask = task,
                       isLoading = false,
                       noTasksAvailable = task == null
                   )
               }
           } catch (e: Exception) {
               _uiState.update {
                   it.copy(isLoading = false, error = e.message ?: "Unknown error")
               }
           }
       }
   }
   ```

3. **`app/src/test/java/com/nshaddox/randomtask/ui/screens/randomtask/RandomTaskViewModelTest.kt`**
   - Uses `StandardTestDispatcher` and `Dispatchers.setMain/resetMain` (matching `SampleViewModelTest` pattern)
   - Uses `FakeTaskRepository` to construct real use case instances (no mocking needed -- consistent with existing test patterns)
   - Uses Turbine (`StateFlow.test {}`) for flow assertions
   - Test cases:
     - Initial state has null task, not loading, no error
     - `loadRandomTask()` returns a task when incomplete tasks exist
     - `loadRandomTask()` sets `noTasksAvailable = true` when no incomplete tasks
     - `completeTask()` marks current task complete and loads next random task
     - `completeTask()` does nothing when no current task
     - `skipTask()` loads a new random task without completing current one
     - Error handling when use case throws

---

## Risks & Considerations

- **Issue #54 dependency**: `RandomTaskUiState` does not exist yet. It should be created as part of this implementation or immediately before. The data class definition is explicit in the issue spec and trivial.
- **Screen model mismatch**: `RandomTaskScreen.kt` currently uses `ui.preview.Task` (a preview mock model) rather than `domain.model.Task`. This will be resolved in issue #61 (wire screen to ViewModel), not in this issue.
- **`GetRandomTaskUseCase` returns `Task?` not `Result<Task?>`**: The current implementation returns raw `Task?` rather than wrapping in `Result`. The ViewModel should use try/catch for error handling rather than `Result.onFailure`.
- **Dispatcher injection**: Following `SampleViewModel` pattern, inject `@Named("IO")` dispatcher for background work. This keeps the ViewModel testable by swapping in `StandardTestDispatcher` during tests.

---

## Verification Checklist

- [ ] `RandomTaskUiState.kt` created in `ui.screens.randomtask` package
- [ ] `RandomTaskViewModel.kt` created with `@HiltViewModel`, correct injections
- [ ] `StateFlow<RandomTaskUiState>` exposed
- [ ] `loadRandomTask()`, `completeTask()`, `skipTask()` implemented
- [ ] Unit tests cover: initial state, load success, load empty, complete success, complete no-op, skip, error handling
- [ ] `./gradlew test` passes
- [ ] `./gradlew assembleDebug` passes
