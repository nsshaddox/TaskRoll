# create-task-repository-interface

**Implemented**: 2026-02-22
**Complexity**: simple

## What Changed

- Updated `TaskRepository` interface to match issue #149 API: renamed `getTasks` to `getAllTasks`, added `getIncompleteTasks`, changed `getTaskById` to return `Flow<Task?>`, changed `deleteTask` to accept `Task`
- Added `getTaskByIdFlow` to `TaskDao` returning `Flow<TaskEntity?>` for reactive single-task observation
- Updated `TaskRepositoryImpl`, all use cases, `FakeTaskRepository`, and unit tests to match new signatures
- Added KDoc documentation to the `TaskRepository` interface and all its methods

## Why

The repository interface needed to align with the API contract defined in issue #149. Key drivers: `getIncompleteTasks` moves filtering to the database layer for efficiency, `Flow<Task?>` on `getTaskById` enables reactive UI updates, and `deleteTask(task)` provides a safer API than raw ID deletion.

## Key Files

- `domain/repository/TaskRepository.kt` - Rewrote interface with new method names, signatures, and KDoc
- `data/local/TaskDao.kt` - Added `getTaskByIdFlow` returning `Flow<TaskEntity?>`
- `data/repository/TaskRepositoryImpl.kt` - Updated all overrides to match new interface
- `domain/usecase/DeleteTaskUseCase.kt` - Changed parameter from `taskId: Long` to `Task`
- `domain/usecase/GetRandomTaskUseCase.kt` - Uses `getIncompleteTasks()` instead of client-side filtering
- `domain/usecase/GetTasksUseCase.kt` - Renamed `getTasks()` call to `getAllTasks()`
- `test/.../FakeTaskRepository.kt` - Implemented all new interface methods
- `test/.../DeleteTaskUseCaseTest.kt` - Updated to pass `Task` objects

## Implementation Notes

- Kept existing suspend `getTaskById` in DAO for backward compatibility; added `getTaskByIdFlow` alongside it
- `FakeTaskRepository` derives `getIncompleteTasks` and `getTaskById` from `MutableStateFlow` using `.map`

## Verification

- [x] Tests: `./gradlew test` - BUILD SUCCESSFUL (all unit tests pass)
- [x] Quality: `./gradlew assembleDebug` - BUILD SUCCESSFUL
- [x] Manual: All 5 plan tasks verified complete, 10 files modified as expected
