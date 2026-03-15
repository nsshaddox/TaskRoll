# Delete UX (Swipe + Undo)

**Feature**: 15-delete-ux
**Date**: 2026-03-14
**Complexity**: Medium
**Source Issues**: GH#227, GH#228

## What Changed

- Added SwipeToDismissBox swipe-to-delete gesture on task items in both TaskListScreen and CompletedTasksScreen
- Implemented undo snackbar: "Task deleted" with "Undo" action that restores the deleted task
- Only one pending undo at a time — new deletion replaces previous
- Undo for completed tasks re-creates and re-marks as completed

## Why

Accidental deletions needed a safety net. Swipe-to-delete is the expected mobile UX pattern.

## Key Files

| File | Change Type |
|------|-------------|
| `ui/screens/tasklist/TaskListViewModel.kt` | Modified |
| `ui/screens/tasklist/TaskListScreen.kt` | Modified |
| `ui/screens/completedtasks/CompletedTasksViewModel.kt` | Modified |
| `ui/screens/completedtasks/CompletedTasksScreen.kt` | Modified |

## Implementation Notes

- Undo re-creates task via `AddTaskUseCase` (new ID + timestamps) — acceptable tradeoff
- `pendingDeleteTask` field added to both UiState classes
- CompletedTasks undo also injects `CompleteTaskUseCase` to re-mark restored tasks

## Verification

- [x] 16 new tests (7 TaskList VM + 8 CompletedTasks VM + 6 UiState)
- [x] All quality gates pass
