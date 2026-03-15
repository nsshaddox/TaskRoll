# New Query Use Cases

**Implemented**: 2026-03-14
**Complexity**: simple

## What Changed

- Added 4 new query use cases to domain layer: `GetCompletedTasksUseCase`, `SearchTasksUseCase`, `GetTasksByPriorityUseCase`, `GetTasksByCategoryUseCase`
- Each use case wraps existing repository methods with clean injectable contracts
- `SearchTasksUseCase` includes blank-query guard returning empty list before repository delegation
- Added 4 comprehensive test files with 12 new test cases covering all use cases

## Why

Exposes already-implemented repository query methods as proper domain-layer use case contracts. ViewModels can now depend on thin, testable use case abstractions rather than querying the repository directly. Follows established architecture patterns and maintains strict TDD discipline.

## Key Files

- `app/src/main/java/com/nshaddox/randomtask/domain/usecase/GetCompletedTasksUseCase.kt` - No-arg invoke, delegates to `repository.getCompletedTasks()`
- `app/src/main/java/com/nshaddox/randomtask/domain/usecase/SearchTasksUseCase.kt` - String parameter, blank-query guard with `flowOf(emptyList())`
- `app/src/main/java/com/nshaddox/randomtask/domain/usecase/GetTasksByPriorityUseCase.kt` - Priority parameter, filters by priority
- `app/src/main/java/com/nshaddox/randomtask/domain/usecase/GetTasksByCategoryUseCase.kt` - String parameter, filters by category
- All 4 test files verify correct delegation, edge cases, and completion filtering

## Implementation Notes

- All use cases follow `GetTasksUseCase` pattern with `@Inject constructor` and `operator fun invoke()`
- Domain layer remains pure Kotlin (no Android imports)
- TDD approach: test files written first (RED), then minimal production code (GREEN)
- `SearchTasksUseCase` is only use case with conditional logic; requires 100% branch coverage
- All completed tasks automatically excluded from priority/category results via repository filtering
- No Hilt module changes needed — Hilt injects these use cases directly via `@Inject constructor`

## Verification

- Tests: `./gradlew testDebugUnitTest` — **PASSED** (12 new test cases)
- Quality: `./gradlew lintDebug` — **PASSED** (no lint violations)
- Manual: All 4 use cases compile, no Android imports in production code, delegation verified in tests
