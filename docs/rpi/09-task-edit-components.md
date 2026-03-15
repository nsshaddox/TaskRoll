# Task Edit Components

**Feature**: 09-task-edit-components
**Date**: 2026-03-14
**Complexity**: Medium
**Source Issues**: GH#219, GH#221

## What Changed

- Created `EditTaskDialog` dual-mode composable (add/edit) with title, description, priority selector, due date picker, and category fields
- Created `DueDatePickerDialog` wrapping Material3 DatePicker with clear/confirm/cancel actions
- Enabled core library desugaring for `java.time.LocalDate` API support on API 24+
- Added 17 string resources and 14 instrumented Compose UI tests

## Why

v2.0 requires full-featured task editing with priority, due dates, and categories. The simple `AddTaskDialog` (title + description only) needed replacement.

## Key Files

| File | Change Type |
|------|-------------|
| `ui/screens/tasklist/EditTaskDialog.kt` | Created |
| `ui/components/DueDatePickerDialog.kt` | Created |
| `app/build.gradle.kts` | Modified (desugaring) |
| `res/values/strings.xml` | Modified |

## Implementation Notes

- `EditTaskDialog` accepts `TaskUiModel?` — null for add mode, non-null for edit mode
- `DueDatePickerDialog` placed in `ui.components` for cross-screen reuse
- Added `initialDueDate: LocalDate?` parameter beyond plan spec since `TaskUiModel` has no raw date field

## Verification

- [x] 14 instrumented tests (6 DueDatePicker + 8 EditTaskDialog)
- [x] Lint + unit tests pass
