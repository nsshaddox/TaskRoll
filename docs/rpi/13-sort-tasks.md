# Sort Tasks Persistence

**Feature**: 13-sort-tasks
**Date**: 2026-03-14
**Complexity**: Medium
**Source Issues**: GH#229

## What Changed

- Bridged the DataStore persistence gap in `TaskListViewModel`: reads persisted sort order on init, writes back on change
- Added 2 new persistence round-trip tests

## Why

Sort logic was already implemented but the chosen sort order was lost on app restart — it always reset to "Newest First".

## Key Files

| File | Change Type |
|------|-------------|
| `ui/screens/tasklist/TaskListViewModel.kt` | Modified |
| `ui/screens/tasklist/TaskListViewModelTest.kt` | Modified |

## Implementation Notes

- Injected `DataStore<Preferences>` as 10th constructor parameter
- Uses same `SORT_ORDER_KEY` as `SettingsViewModel` (both read/write "sort_order" preference)
- DRY concern: key defined as private companion in both VMs

## Verification

- [x] 30 tests pass (28 existing + 2 new persistence tests)
- [x] Lint clean
