# TaskList ViewModel: Search/Filter/Sort

**Implemented**: 2026-03-14
**Complexity**: medium

## What Changed

- Added reactive `combine` infrastructure to orchestrate `GetTasksUseCase` flow with four `MutableStateFlow` parameters (`searchQuery`, `filterPriority`, `filterCategory`, `sortOrder`)
- Implemented in-memory compound filtering: search via substring match on title/description, then stack priority and category filters on results
- Added `availableCategories` derivation from full unfiltered task list on every raw task emission
- Extended `addTask` signature to accept `priority`, `dueDate`, and `category` parameters, forwarded to `AddTaskUseCase`
- Added four new public functions: `updateSearchQuery()`, `setFilterPriority()`, `setFilterCategory()`, `setSortOrder()`
- Implemented sort comparators for all `SortOrder` values including null-safe `DUE_DATE_ASC` (nulls last)

## Why

Enables the task list screen to provide search, filter, and sort capabilities without new repository queries. The reactive combine pattern ensures any change to a filter/sort parameter automatically recomputes and emits the filtered/sorted task list. `availableCategories` is derived from the complete unfiltered dataset, so category filters show options regardless of active filters.

## Key Files

- `app/src/main/java/com/nshaddox/randomtask/ui/screens/tasklist/TaskListViewModel.kt` - Added 3 new use case injections, 4 private `MutableStateFlow` fields, reactive combine logic in init block, sort comparators, 4 public update functions, extended `addTask` signature
- `app/src/test/java/com/nshaddox/randomtask/ui/screens/tasklist/TaskListViewModelTest.kt` - Added 15 new test cases (total: 27 tests) covering search, filters, compound scenarios, sort orders, and extended `addTask`

## Implementation Notes

- Used `combine` pattern on raw tasks flow and four parameter flows for reactive recomputation on any filter/sort change
- Search filtering delegates to inline substring match (not `SearchTasksUseCase` lookup); then stacks priority/category filters as Kotlin collection operations
- `SortOrder.DUE_DATE_ASC` uses `compareBy(nullsLast()) { it.dueDate }` for safe null handling
- Added `@Suppress("TooManyFunctions")` to ViewModel (5 new public functions)
- Did not inject `SearchTasksUseCase`, `GetTasksByPriorityUseCase`, or `GetTasksByCategoryUseCase` — in-memory filtering sufficient

## Verification

- Tests: 27 total, 15 new (all passing)
- Lint: `./gradlew lintDebug` passed
- Build: `./gradlew testDebugUnitTest` passed
- Pre-commit: `./gradlew lintDebug testDebugUnitTest` passed
