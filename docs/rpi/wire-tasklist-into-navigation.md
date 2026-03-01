# wire-tasklist-into-navigation

**Implemented**: 2026-02-28
**Complexity**: simple (from research phase)

## What Changed

- Created `NavGraph.kt` composable in `ui/navigation/` with a `NavHost` wiring TaskList and RandomTask screen destinations
- Updated `MainActivity.kt` to use `rememberNavController()` + `NavGraph()` instead of the static Greeting scaffold
- Removed unused `Greeting` and `GreetingPreview` composables and their imports from MainActivity

## Why

MainActivity rendered a static "Hello Android" greeting, making the implemented TaskListScreen and RandomTaskScreen unreachable. This change wires both screens into a navigation graph so the app launches directly into TaskListScreen and supports forward/back navigation to RandomTaskScreen (Issue #119).

## Key Files

- `app/src/main/java/com/nshaddox/randomtask/ui/navigation/NavGraph.kt` - New composable defining NavHost with two screen destinations
- `app/src/main/java/com/nshaddox/randomtask/MainActivity.kt` - Replaced Greeting scaffold with NavGraph; removed Greeting/GreetingPreview

## Implementation Notes

- NavGraph uses `Screen.TaskList.route` as `startDestination`, matching the existing sealed class routes in `NavRoutes.kt`
- Screens call `hiltViewModel()` internally, so no ViewModel parameters are passed through NavGraph
- Each screen already has its own Scaffold, so the outer Scaffold in MainActivity was correctly removed
- `enableEdgeToEdge()` and `@AndroidEntryPoint` retained as required

## Verification

- [x] Build: `./gradlew assembleDebug` -- BUILD SUCCESSFUL
- [x] Tests: `./gradlew test` -- BUILD SUCCESSFUL, zero failures
- [x] Manual: NavGraph wires TaskListScreen at start destination, RandomTaskScreen at secondary route
