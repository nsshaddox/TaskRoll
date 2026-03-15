# TaskListScreen Update

**Feature**: 11-tasklist-screen-update
**Date**: 2026-03-14
**Complexity**: High
**Source Issues**: GH#220, GH#222

## What Changed

- Created `TaskFilterBar` composable with search field, priority filter chips, category dropdown, sort order selector
- Overhauled `TaskListScreen` to display v2 task fields (priority badges, due dates, categories) in task items
- Integrated `EditTaskDialog` for both add and edit flows, replacing `AddTaskDialog`
- Added edit dialog state management to `TaskListViewModel` (showEditDialog, hideEditDialog, editTask)
- 11 new unit tests covering edit flow and state transitions

## Why

The main task list needed to expose all v2.0 features: search/filter/sort controls and full task metadata display.

## Key Files

| File | Change Type |
|------|-------------|
| `ui/screens/tasklist/TaskFilterBar.kt` | Created |
| `ui/screens/tasklist/TaskListScreen.kt` | Modified (major) |
| `ui/screens/tasklist/TaskListViewModel.kt` | Modified |
| `ui/screens/tasklist/TaskListUiState.kt` | Modified |

## Implementation Notes

- TaskListScreen now works with `TaskUiModel` instead of raw `Task` for display
- Edit dialog state (isEditDialogVisible, editingTask) added to UiState
- `AddTaskDialog` still exists but is no longer used from the ViewModel wrapper

## Verification

- [x] 36 ViewModel tests, 24 UiState tests pass
- [x] Lint clean
