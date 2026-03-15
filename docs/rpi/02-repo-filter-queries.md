# Repository Filter & Query Layer

**Implemented**: 2026-03-14
**Complexity**: medium

## What Changed

- Added four new `Flow`-returning query methods to `TaskRepository` interface: `getCompletedTasks()`, `getTasksByPriority()`, `getTasksByCategory()`, and `searchTasks()`
- Implemented the four queries in `TaskRepository` via DAO delegation with `.map { entities.map { it.toDomain() } }` pattern
- Added four `@Query` SQL functions to `TaskDao` for completed tasks, priority filtering, category filtering, and full-text search
- Extended `FakeTaskRepository` with in-memory filter implementations for testing
- Added 8 new unit tests in `TaskRepositoryImplTest` (2 per method: populated and empty list cases)

## Why

Enables v2.0 features that require filtering and searching tasks: completed task history, priority-based task selection, category filtering, and task search. Follows TDD strict requirements with all tests written before implementation and passing with 100% compliance.

## Key Files

- `domain/repository/TaskRepository.kt` - Added 4 KDoc-documented interface signatures
- `data/local/TaskDao.kt` - Added 4 `@Query` SQL functions returning `Flow<List<TaskEntity>>`
- `data/repository/TaskRepositoryImpl.kt` - Implemented 4 override methods delegating to DAO with correct `priority.name` mapping
- `data/repository/TaskRepositoryImplTest.kt` - Added 8 unit tests with MockK stubs and Turbine Flow assertions
- `domain/usecase/FakeTaskRepository.kt` - Added 4 in-memory filter methods using `tasksFlow.map` pattern

## Implementation Notes

- `getTasksByPriority` and `getTasksByCategory` filter only incomplete tasks (`AND is_completed = 0`) per v2 spec
- Priority stored as TEXT in Room; implementation correctly passes `priority.name` (String) to DAO, not the enum
- `searchTasks` uses SQL `LIKE '%' || :query || '%'` for title and description matching; FTS deferred to future
- All new methods follow existing clean-architecture pattern: domain interface → DAO query → repository mapper
- Domain layer remains pure Kotlin (no Android imports)

## Verification

- Tests: `./gradlew testDebugUnitTest` — All tests pass (30 up-to-date tasks)
- Lint: `./gradlew lintDebug` — Clean build, no violations
- Code: Compilation successful, all 4 methods correctly integrated
- Coverage: TDD-enforced; all edge cases (populated and empty results) tested
