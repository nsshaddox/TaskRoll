# UI State Extensions

**Implemented**: 2026-03-14
**Complexity**: medium

## What Changed

- Added `AppTheme` enum (`LIGHT`, `DARK`, `SYSTEM`) to domain layer for theme preferences
- Added `SortOrder` enum (`CREATED_DATE_DESC`, `DUE_DATE_ASC`, `PRIORITY_DESC`, `TITLE_ASC`) to domain layer for task ordering
- Extended `TaskUiModel` with five new display fields: `priority`, `priorityLabel`, `dueDateLabel`, `isOverdue`, `category`
- Updated `toUiModel()` mapper to accept `currentEpochDay` parameter for testable overdue logic
- Created `CompletedTasksUiState` data class for completed tasks screen
- Created `SettingsUiState` data class with `AppTheme` and `SortOrder` defaults
- Extended `TaskListUiState` with five new fields: `searchQuery`, `filterPriority`, `filterCategory`, `sortOrder`, `availableCategories`

## Why

These UI state extensions establish the foundation for v2 features: theme settings, task sorting/filtering, completed tasks history, and search. Placing enums in the domain layer enables reuse across multiple screens without creating circular dependencies or Android imports in pure Kotlin layers.

## Key Files

- `app/src/main/java/com/nshaddox/randomtask/domain/model/AppTheme.kt` - New enum for theme choices
- `app/src/main/java/com/nshaddox/randomtask/domain/model/SortOrder.kt` - New enum for sort ordering
- `app/src/main/java/com/nshaddox/randomtask/ui/screens/tasklist/TaskUiModel.kt` - Added five display fields
- `app/src/main/java/com/nshaddox/randomtask/ui/screens/tasklist/TaskUiMappers.kt` - Extended with `currentEpochDay` parameter
- `app/src/main/java/com/nshaddox/randomtask/ui/screens/tasklist/TaskListUiState.kt` - Added five search/filter/sort fields
- `app/src/main/java/com/nshaddox/randomtask/ui/screens/completedtasks/CompletedTasksUiState.kt` - New state class
- `app/src/main/java/com/nshaddox/randomtask/ui/screens/settings/SettingsUiState.kt` - New state class

## Implementation Notes

- Enums placed in `domain.model` (pure Kotlin, zero Android imports) to enable cross-screen reuse without circular dependencies
- `toUiModel()` now requires `currentEpochDay` parameter instead of calling a clock inline—improves testability by allowing callers to inject the current date
- `priorityLabel` computed in mapper as string ("High"/"Medium"/"Low"), not from Android resources—keeps mapper Android-free
- `dueDateLabel` uses `SimpleDateFormat` consistent with existing `formatTimestamp` helper
- Overdue logic: `dueDate != null && dueDate < currentEpochDay` covers all three boundary cases

## Verification

- Tests: 73 new unit tests across 5 test files, all passing
- Quality: `./gradlew lintDebug testDebugUnitTest` passes with zero warnings
- Coverage: New code at ~90%+ line coverage, 100% branch coverage on business logic
- Domain: Zero Android imports in `domain/model/` files

