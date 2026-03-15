# AddTaskUseCase v2 fields

**Implemented**: 2026-03-15
**Complexity**: simple

## What Changed

- Changed `AddTaskUseCase.invoke` signature from `invoke(task: Task)` to `invoke(title, description, priority, dueDate, category)` with defaults for v2 fields
- `Task` now constructed internally by the use case instead of by the caller
- Timestamp assignment (`createdAt` and `updatedAt`) moved into the use case body
- Replaced 3 old tests with 6 new parameter-based tests covering all field combinations and defaults

## Why

Isolates timestamp responsibility inside the use case and removes the caller's need to know about `Task` construction details. Prepares the API for v2 fields (priority, dueDate, category) that were added in feature F1. Simplifies the calling code by allowing individual parameters with sensible defaults instead of pre-constructing `Task` objects.

## Key Files

- `app/src/main/java/com/nshaddox/randomtask/domain/usecase/AddTaskUseCase.kt` - New signature accepts individual parameters; constructs Task internally with `System.currentTimeMillis()` for both timestamps; Priority.MEDIUM is default for priority
- `app/src/test/java/com/nshaddox/randomtask/domain/usecase/AddTaskUseCaseTest.kt` - 6 tests covering defaults, explicit v2 field values, timestamp assertions, and blank/empty title validation

## Implementation Notes

- Blank-title validation remains before Task construction, preserving existing behavior
- Default: `Priority.MEDIUM`, `dueDate: null`, `category: null`
- Both `createdAt` and `updatedAt` set to the same timestamp captured via `System.currentTimeMillis()`
- Followed pattern established in UpdateTaskUseCase and CompleteTaskUseCase (no injected clock)
- Domain layer remains pure Kotlin — no Android imports added

## Verification

- Tests: All 6 tests passing (AddTaskUseCaseTest)
- Quality: `./gradlew lintDebug testDebugUnitTest` passes with no failures
- Manual: Timestamp test confirms both fields set to ≥ pre-call time; default values verified
