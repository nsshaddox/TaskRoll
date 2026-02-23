# create-taskdao-interface

**Implemented**: 2026-02-22
**Complexity**: simple

## What Changed

- Added `getIncompleteTasks()` method to `TaskDao` interface
- New Room `@Query` returns only tasks where `is_completed = 0`, ordered by `created_at DESC`

## Why

The existing `TaskDao` had no way to query only incomplete tasks at the database level. `GetRandomTaskUseCase` was filtering incomplete tasks in-memory after fetching all tasks. This DAO-level query enables efficient filtering directly in SQLite, and prepares the data layer for UI screens that display only actionable tasks.

## Key Files

- `app/src/main/java/com/nshaddox/randomtask/data/local/TaskDao.kt` - Added `getIncompleteTasks()` with `@Query` annotation

## Implementation Notes

- Followed the exact pattern of `getAllTasks()`: same return type (`Flow<List<TaskEntity>>`), same ordering (`created_at DESC`)
- Used `WHERE is_completed = 0` matching SQLite's integer-boolean convention and the `@ColumnInfo` name from `TaskEntity`
- Single-method addition; no other files modified

## Verification

- [x] Build: `./gradlew assembleDebug` passed (Room KSP validates SQL at compile time)
- [x] Tests: `./gradlew test` passed with no regressions
- [x] Manual: Confirmed method name, return type, and SQL filter match issue requirements
