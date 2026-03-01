# implement-nav-graph

**Implemented**: 2026-02-28
**Complexity**: simple (from research phase)

## What Changed

- Created `NavGraph.kt` composable defining the app's navigation graph with two destinations
- Updated `MainActivity.kt` to use `NavGraph` with `rememberNavController()` instead of placeholder `Greeting` composable
- Removed unused `Greeting` and `GreetingPreview` functions from MainActivity

## Why

The app had individual screens (TaskListScreen, RandomTaskScreen) and route definitions (Screen sealed class) but no navigation graph to wire them together. This composable connects the screens via NavHost so users can navigate between the task list and random task selection. Closes #128.

## Key Files

- `app/src/main/java/com/nshaddox/randomtask/ui/navigation/NavGraph.kt` - New file; NavHost with TaskList (start) and RandomTask destinations
- `app/src/main/java/com/nshaddox/randomtask/MainActivity.kt` - Replaced Greeting with NavGraph; added rememberNavController

## Implementation Notes

- Both screen composables already call `hiltViewModel()` internally, so NavGraph only passes `navController`
- Uses existing `Screen` sealed class routes from `NavRoutes.kt`
- Start destination is `Screen.TaskList.route` per acceptance criteria
- No new dependencies required; `navigation-compose` and `hilt-navigation-compose` were already configured

## Verification

- [x] Tests: `./gradlew test` -- BUILD SUCCESSFUL (all unit tests pass)
- [x] Quality: `./gradlew assembleDebug` -- BUILD SUCCESSFUL
- [x] Manual: NavGraph.kt exists with correct destinations; MainActivity uses NavGraph
