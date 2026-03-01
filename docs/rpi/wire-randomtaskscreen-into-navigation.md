# wire-randomtaskscreen-into-navigation

**Implemented**: 2026-02-28
**Complexity**: simple (from research phase)

## What Changed

- Replaced placeholder Greeting composable in `MainActivity.kt` with a `NavHost`
- Wired `TaskListScreen` as the start destination and `RandomTaskScreen` as a secondary destination
- Removed unused `Greeting` composable, `GreetingPreview`, and their associated imports

## Why

The app had two fully implemented screens (TaskListScreen and RandomTaskScreen) with
ViewModels and navigation-aware composables, but no NavHost in MainActivity to make them
reachable. This change connects the screens into a working navigation graph so users can
view their task list and navigate to the random task selector.

## Key Files

- `app/src/main/java/com/nshaddox/randomtask/MainActivity.kt` - Replaced Scaffold+Greeting with NavHost containing two composable destinations

## Implementation Notes

- Used `rememberNavController()` from Navigation Compose, consistent with existing screen signatures
- Kept `@AndroidEntryPoint` annotation and `RandomTaskTheme` wrapper intact
- Start destination is `Screen.TaskList.route`; screens handle their own navigation actions
- Single-file change (~15 lines net reduction) with no modifications to screens or ViewModels

## Verification

- [x] Build: `./gradlew assembleDebug` - BUILD SUCCESSFUL
- [x] Tests: `./gradlew test` - BUILD SUCCESSFUL (all unit tests pass)
- [x] Manual: TaskListScreen renders as start destination; navigation routes resolve correctly
