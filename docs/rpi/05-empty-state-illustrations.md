# Empty State Illustrations

**Implemented**: 2026-03-18
**Complexity**: medium

## What Changed

- Created `ThemedEmptyStateContent` composable with per-theme pure helper functions for typography (font size, weight, body alpha) following the `ThemedFAB` pattern
- Added two vector drawables: `ic_empty_task_list.xml` (clipboard + plus) and `ic_empty_random_task.xml` (die with dots)
- Migrated hardcoded strings "No tasks yet" and "No tasks available" to `strings.xml` (6 new string resources)
- Replaced `EmptyTaskListContent`, `NoTasksAvailableContent`, and `EmptyCompletedTasksContent` implementations to delegate to `ThemedEmptyStateContent`
- Neo Brutalist body text renders in yellow badge with 3dp border; Vapor body in teal at 40% alpha; Obsidian body in onSurfaceVariant at 60% alpha

## Why

Addresses GH#96: provides accessible, theme-aware illustrations and friendly typography for empty states across task list, random task, and completed tasks screens. Eliminates hardcoded UI strings and ensures visual consistency across all three themes (Obsidian, Neo Brutalist, Vapor).

## Key Files

- `app/src/main/java/com/nshaddox/randomtask/ui/components/ThemedEmptyStateContent.kt` - shared composable with pure helper functions
- `app/src/main/res/drawable/ic_empty_task_list.xml` - clipboard illustration (96x96dp)
- `app/src/main/res/drawable/ic_empty_random_task.xml` - die illustration (96x96dp)
- `app/src/main/res/values/strings.xml` - 6 new string resources (titles, bodies, content descriptions)
- `app/src/test/java/com/nshaddox/randomtask/ui/components/ThemedEmptyStateContentTest.kt` - 11 unit tests for pure helpers
- `app/src/main/java/com/nshaddox/randomtask/ui/screens/tasklist/TaskListScreen.kt` - updated EmptyTaskListContent
- `app/src/main/java/com/nshaddox/randomtask/ui/screens/randomtask/RandomTaskScreen.kt` - updated NoTasksAvailableContent
- `app/src/main/java/com/nshaddox/randomtask/ui/screens/completedtasks/CompletedTasksScreen.kt` - updated EmptyCompletedTasksContent

## Implementation Notes

- Followed `ThemedFAB.kt` and `ThemedCard.kt` pattern: pure testable helper functions at top of file, then composable reads `LocalThemeVariant.current`
- Optional `action` slot allows `NoTasksAvailableContent` to preserve its Go Back button
- Drawables use single fill color for tinting via `ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)`
- Neo Brutalist body slot uses structural `when(variant)` branching to render yellow badge; Obsidian and Vapor render plain text
- Strict TDD: all 11 pure helper tests written before production code

## Verification

- Tests: All 11 unit tests pass (font size/weight assertions for 3 themes, body alpha for 3 themes, badge boolean)
- Quality: `lintDebug` passes with no warnings
- Build: Full clean build successful
- Code: Zero hardcoded strings in target screens; all leverage string resources
