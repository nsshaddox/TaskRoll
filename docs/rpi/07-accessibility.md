# Accessibility Improvements

**Implemented**: 2026-03-18
**Complexity**: medium

## What Changed

- Added three new content description string resources (`cd_add_task`, `cd_navigate_to_random_task`, `cd_complete_task_checkbox` with dynamic task title)
- Added `contentDescription: String? = null` param to `ThemedCheckbox` with conditional semantics block
- Applied `minimumInteractiveComponentSize()` to all three theme variants in `ThemedCheckbox` (48dp touch target)
- Replaced four hardcoded content description strings with string resources in `TaskListScreen`, `RandomTaskScreen`, and `TaskEditorScreen`
- Added `semantics(mergeDescendants = true)` to `WeightedRandomToggle` Row for unified TalkBack reading
- Added delete `IconButton` to `CompletedTaskItem` as gesture alternative to swipe-to-delete

## Why

Addresses GH#103 WCAG AA compliance requirements. Users relying on assistive technologies now have:
- Semantic content descriptions on all interactive controls
- 48dp minimum touch targets for checkboxes across all three themes
- String-based content descriptions (enables localization and consistency)
- Merged accessibility nodes for better TalkBack experience
- Non-gesture alternative for task deletion

## Key Files

- `app/src/main/res/values/strings.xml` — Added three `cd_*` entries in Content Descriptions section
- `app/src/main/java/com/nshaddox/randomtask/ui/components/ThemedCheckbox.kt` — Added `contentDescription` param, `minimumInteractiveComponentSize()`, and semantic block to all three variants
- `app/src/test/java/com/nshaddox/randomtask/ui/components/ThemedCheckboxTest.kt` — Added two new pure-function tests for `contentDescription` contract
- `app/src/main/java/com/nshaddox/randomtask/ui/screens/tasklist/TaskListScreen.kt` — Updated checkbox call site and replaced two hardcoded strings
- `app/src/main/java/com/nshaddox/randomtask/ui/screens/randomtask/RandomTaskScreen.kt` — Added merged semantics to toggle; replaced hardcoded back button string
- `app/src/main/java/com/nshaddox/randomtask/ui/screens/taskeditor/TaskEditorScreen.kt` — Replaced hardcoded back button string
- `app/src/main/java/com/nshaddox/randomtask/ui/screens/completedtasks/CompletedTasksScreen.kt` — Added delete IconButton to CompletedTaskItem Row

## Implementation Notes

- Touch target expansion uses `minimumInteractiveComponentSize()` before `.size()` in modifier chain — maintains visual size while expanding interaction area
- Conditional semantics block ensures backward compatibility: null `contentDescription` suppresses semantics, non-null applies `contentDescription`
- All string resources follow existing `cd_*` prefix convention; dynamic values use `%1$s` placeholder
- Merged semantics on `WeightedRandomToggle` uses pattern already established in `ThemedPriorityBadge`
- No ViewModel changes needed for delete button — reuses existing `onDelete` lambda from swipe gesture

## Verification

- Tests: All 41 unit tests pass (`./gradlew test`)
- Lint: Passes lintDebug with no hardcoded string violations
- Quality: Pre-commit hook checks (lintDebug + testDebugUnitTest) successful
- All 6 plan tasks marked complete with acceptance criteria met
