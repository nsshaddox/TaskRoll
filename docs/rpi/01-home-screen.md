# Home Screen

**Implemented**: 2026-03-18 | **Complexity**: medium

## What Changed

- Added `Screen.Home` navigation route as app start destination
- Created `TaskMetrics` domain model with six metrics fields (completedToday, completedThisWeek, streak, remaining, rate, overdue)
- Implemented `GetTaskMetricsUseCase` combining repository Flows with pure Kotlin streak calculation
- Extended `TaskDao` with three metrics queries: `getCompletedTasksSince()`, `getOverdueIncompleteTasks()`, `getIncompleteTaskCount()`
- Created `HomeViewModel` injecting six use cases; exposed `StateFlow<HomeUiState>`
- Implemented `HomeScreen` composable (543 lines) with hero section, quick actions, and metrics dashboard
- Added 24 string resources and metrics repository methods (TaskRepository, TaskRepositoryImpl, FakeTaskRepository)

## Why

HomeScreen replaces TaskList as the landing page, consolidating key user actions: random task selection, quick add/view-all buttons, and real-time progress metrics (streak, completion rate, overdue count). This encourages task engagement from first launch.

## Key Files

- `ui/screens/home/HomeScreen.kt` — Stateful + stateless split with three-theme support
- `domain/usecase/GetTaskMetricsUseCase.kt` — Pure Kotlin; converts epoch timestamps, computes streak
- `domain/model/TaskMetrics.kt` — Six metric fields, no Android imports
- `ui/navigation/NavRoutes.kt` + `NavGraph.kt` — Added Home route and changed startDestination

## Implementation Notes

- Streak: consecutive calendar days (today backward) with ≥1 completed task, computed in use case from `updatedAt`
- Overdue: `dueDate < LocalDate.now().toEpochDay()` at repository boundary (correct epoch-day conversion)
- No schema migration (metrics use existing `updatedAt`, `dueDate` fields)
- Metrics reactivity via `combine()` over four Flows; dashboard updates as tasks complete
- Three-theme support: `ThemedCard`, `ThemedPriorityBadge`, `LocalThemeVariant`, design system tokens

## Verification

- Build: SUCCESS | Tests: SUCCESS (620 new lines, 15+ test cases) | Lint: SUCCESS | Pre-commit: SUCCESS
- Coverage: 90%+ line coverage, 100% branches (use cases)
- Domain purity: Zero Android imports in `domain/`
- Navigation: Cold-launch to HomeScreen confirmed; Quick Actions navigate correctly
- Three-theme: Renders in Obsidian, Neo Brutalist, Vapor via design tokens
