# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Random Task is a native Android app built with Kotlin and Jetpack Compose that helps users manage tasks by randomly selecting one from their list. The app follows MVVM architecture with clean separation between data, domain, and presentation layers.

**Core Functionality:**
- Add, edit, delete, and view tasks
- Generate random task selections from the list
- Mark tasks as completed or skip them
- Persist task data locally with Room Database

**Current Status:** v1.0 features complete. v2.0 development planned (priority, due dates, categories, search/filter/sort, settings, completed tasks history).

## Documentation

The `docs/` directory contains detailed guides. **Read them on-demand, not upfront.**

- **`docs/ARCHITECTURE.md`** — Read when making architectural decisions, adding new layers/modules, or needing to understand data flow and design patterns.
- **`docs/DEVELOPMENT.md`** — Read when setting up the project, adding dependencies, or troubleshooting build/environment issues.
- **`docs/TESTING.md`** — Read when writing or debugging tests, or when you need test patterns and the coroutine testing checklist.
- **`docs/PRECOMMIT.md`** — Read when a pre-commit hook fails, or when modifying the hook configuration.

## Build Commands

```bash
# Build debug variant
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run specific test class
./gradlew test --tests com.nshaddox.randomtask.domain.usecase.AddTaskUseCaseTest

# Run lint + tests (same as pre-commit hook)
./gradlew lintDebug testDebugUnitTest

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Install pre-commit hooks
./gradlew installGitHooks

# Clean build
./gradlew clean assembleDebug
```

## Technical Stack

- **Language:** Kotlin 2.0.21
- **UI Framework:** Jetpack Compose with Material3
- **Architecture:** MVVM with clean architecture layers (data/domain/ui)
- **Database:** Room 2.8.4
- **Dependency Injection:** Hilt 2.51.1 (via KSP)
- **Async:** Kotlin Coroutines 1.10.2, Flow
- **Navigation:** Navigation Compose 2.8.7
- **Testing:** JUnit 4, MockK, Turbine, coroutines-test
- **Min SDK:** 24 / **Target SDK:** 35

## Architecture at a Glance

```
Presentation (Compose Screens → ViewModels → UiState)
    ↓ uses
Domain (Use Cases → Repository Interface → Task Model)
    ↓ implements
Data (Room DAO → TaskEntity → TaskRepositoryImpl → Mappers)
```

- **Domain layer has zero Android dependencies** — pure Kotlin
- Queries return `Flow<T>`, mutations return `Result<T>`
- ViewModels expose `StateFlow<UiState>`
- Named dispatchers injected via Hilt for testability

## Project Structure

```
app/src/main/java/com/nshaddox/randomtask/
├── data/
│   ├── local/          # AppDatabase, TaskDao, TaskEntity
│   └── repository/     # TaskRepositoryImpl, TaskMappers
├── di/                 # DatabaseModule, RepositoryModule, CoroutineModule
├── domain/
│   ├── model/          # Task data class
│   ├── repository/     # TaskRepository interface
│   └── usecase/        # GetTasks, GetRandom, Add, Update, Complete, Delete
└── ui/
    ├── navigation/     # NavRoutes (Screen sealed class), NavGraph
    ├── screens/
    │   ├── tasklist/   # TaskListScreen, ViewModel, UiState, AddTaskDialog
    │   ├── randomtask/ # RandomTaskScreen, ViewModel, UiState
    │   └── taskeditor/ # TaskEditorScreen, ViewModel
    ├── preview/        # MockData (SampleData for Compose previews)
    └── theme/          # Color, Theme, Type
```

## Testing Summary

- **16 unit test files** covering use cases, ViewModels, mappers, and UI state
- `FakeTaskRepository` — in-memory test implementation using `MutableStateFlow`
- ViewModel tests use `StandardTestDispatcher` + Turbine for Flow assertions
- Pre-commit hook runs `lintDebug` + `testDebugUnitTest` before every commit
- CI runs the same checks via GitHub Actions

Read `docs/TESTING.md` when writing or debugging tests.

## Development Guardrails

- **Pre-commit hook** runs lint and unit tests — do not skip to avoid fixing failures
- **Branch naming:** `issue-{number}-{short-description}`
- **All new code must have tests** — test use cases, ViewModels, and mappers
- **Domain layer stays pure Kotlin** — no Android imports in `domain/`
- **Use named dispatchers** — inject `@Named("IO")` instead of hardcoding `Dispatchers.IO`
- **Mappers are extension functions** — no mapper classes, no business logic in mappers

## Review Checklist

Before committing:

- [ ] All tests pass (`./gradlew test`)
- [ ] Lint passes (`./gradlew lintDebug`)
- [ ] New code has corresponding tests
- [ ] No hardcoded strings in UI (use string resources)
- [ ] Domain layer has no Android imports
- [ ] Pre-commit hook passes

## Important Context

- `the-idea/` — Planning documents (requirements, technical decisions, roadmap)
- `docs/` — Development documentation (architecture, testing, pre-commit)
- `GITHUB_ISSUES_V1_5.md` — v1.5 milestone: dev tooling and foundations
- `GITHUB_ISSUES_V2.md` — v2.0 milestone: feature expansion (33 issues, 10 groups)
