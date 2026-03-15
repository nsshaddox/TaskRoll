# CompletedTasks Feature

**Feature**: 10-completedtasks-feature
**Date**: 2026-03-14
**Complexity**: Medium
**Source Issues**: GH#218, GH#223, GH#225

## What Changed

- Created `PriorityBadge` reusable composable with color-coded badges (green/amber/red) per priority level
- Created `CompletedTasksScreen` with SwipeToDismissBox, empty state, and completed timestamp display
- Wired CompletedTasks route into NavGraph and added history icon to TaskListScreen TopAppBar
- Added 19 unit tests (9 PriorityBadge + 10 screen state/date formatting)

## Why

Users need to view their task completion history and see priority levels at a glance across screens.

## Key Files

| File | Change Type |
|------|-------------|
| `ui/components/PriorityBadge.kt` | Created |
| `ui/screens/completedtasks/CompletedTasksScreen.kt` | Created |
| `ui/navigation/NavGraph.kt` | Modified |
| `ui/screens/tasklist/TaskListScreen.kt` | Modified |

## Implementation Notes

- Extracted `resolveContentState()` pure function for testable state logic
- `formatCompletedDate()` accepts explicit `Locale` parameter for CI stability
- SwipeToDismissBox uses `@OptIn(ExperimentalMaterial3Api::class)`

## Verification

- [x] 19 unit tests pass
- [x] Lint clean, no hardcoded strings
