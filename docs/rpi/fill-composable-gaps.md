# fill-composable-gaps

**Implemented**: 2026-03-01
**Complexity**: simple

## What Changed

- Added optional description field to `AddTaskDialog` with a second `TextField` in a `Column` layout
- Updated `onConfirm` callback signature from `(String) -> Unit` to `(String, String?) -> Unit`
- Threaded description parameter through `TaskListViewModel.addTask()` into the `Task` domain model
- Renamed `private TaskCard` to `internal TaskListItem` and updated its call site
- Added three preview composables: `TaskListItemPreview`, `TaskListItemCompletedPreview`, `AddTaskDialogWithDescriptionPreview`
- Added ViewModel test verifying description flows through `addTask`

## Why

The composable layer had gaps: `AddTaskDialog` lacked the description field that the domain model already supported, `TaskCard` was private and unnamed inconsistently, and several composables had no preview coverage. These changes close issues #85 and #76.

## Key Files

- `ui/screens/tasklist/AddTaskDialog.kt` - Column layout with title and description TextFields, updated callback
- `ui/screens/tasklist/TaskListScreen.kt` - Renamed TaskCard to TaskListItem (internal), updated onConfirm lambda
- `ui/screens/tasklist/TaskListScreenPreview.kt` - Three new preview composables using SampleData
- `ui/screens/tasklist/TaskListViewModel.kt` - Added description parameter to addTask()
- `TaskListViewModelTest.kt` - New test for description pass-through

## Implementation Notes

- Description is optional; blank input is stored as `null` via `trim().ifBlank { null }`
- Followed existing preview patterns: `@PreviewLightDark` for primary, `@Preview(showBackground = true)` for variants
- Displaying description text within `TaskListItem` UI is intentionally out of scope

## Verification

- [x] Build: `./gradlew assembleDebug` passed
- [x] Tests: `./gradlew testDebugUnitTest` passed (all tests including new description test)
- [x] Plan: All 4 tasks and sub-tasks completed as specified
