# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Random Task is a native Android app built with Kotlin and Jetpack Compose that helps users manage tasks by randomly selecting one from their list. The app follows MVVM architecture and is designed for simplicity and performance.

**Core Functionality:**
- Add, edit, delete, and view tasks
- Generate random task selections from the list
- Mark tasks as completed or skip them
- Persist task data locally with Room Database

**Current Status:** Initial project setup phase. The project structure is established, but core features are not yet implemented. See `the-idea/project-roadmap.md` for detailed implementation phases.

## Build Commands

```bash
# Build the app (debug variant)
./gradlew assembleDebug

# Build release variant
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run instrumented tests on connected device/emulator
./gradlew connectedAndroidTest

# Run specific test class
./gradlew test --tests com.nshaddox.randomtask.YourTestClass

# Clean build artifacts
./gradlew clean

# Install debug build to connected device
./gradlew installDebug

# View all available tasks
./gradlew tasks
```

## Technical Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose with Material3
- **Architecture:** MVVM (Model-View-ViewModel)
- **Database:** Room (SQLite) - *to be implemented*
- **Dependency Injection:** Hilt - *to be implemented*
- **Async Operations:** Kotlin Coroutines - *to be implemented*
- **Navigation:** Navigation Component - *to be implemented*
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 35

## Project Structure

```
app/src/main/java/com/nshaddox/randomtask/
├── MainActivity.kt              # Main entry point
└── ui/
    └── theme/                   # Compose theme definitions
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

**Planned Structure** (as architecture is implemented):
```
app/src/main/java/com/nshaddox/randomtask/
├── data/
│   ├── local/                   # Room database entities, DAOs
│   └── repository/              # Repository implementations
├── domain/
│   ├── model/                   # Domain models
│   └── usecase/                 # Business logic use cases
├── ui/
│   ├── tasklist/                # Task list screen
│   ├── randomtask/              # Random task selection screen
│   └── theme/                   # Theme definitions
└── di/                          # Hilt dependency injection modules
```

## Architecture Guidelines

**MVVM Pattern:**
- **Model:** Data layer (Room entities, repositories)
- **View:** Jetpack Compose UI components
- **ViewModel:** Manages UI state and business logic, exposes StateFlow/LiveData

**Key Principles:**
- UI state should be hoisted to ViewModels
- Use Kotlin Flows for reactive data streams
- Repository pattern abstracts data sources
- Dependency injection via Hilt for testability
- Single Activity architecture with Compose Navigation

## Development Notes

**Observability:**
- OpenTelemetry instrumentation planned for development builds only
- Will use debug-only dependencies to avoid production overhead
- Focus on database operations and UI rendering paths
- See `the-idea/observability-strategy.md` for implementation details

**Data Persistence:**
- Room Database will be the primary storage mechanism
- ACID-compliant local storage for tasks
- No cloud sync in MVP (post-MVP feature)

**Testing Strategy:**
- Unit tests for ViewModels and repositories
- Integration tests for database operations
- UI tests with Compose Testing library
- Test files located in `app/src/test/` (unit) and `app/src/androidTest/` (instrumented)

## Important Context

The `the-idea/` directory contains comprehensive planning documents that define the project vision:
- `core-requirements.md` - Functional and non-functional requirements
- `technical-options.md` - Architecture decisions and technical stack rationale
- `observability-strategy.md` - Development observability approach
- `project-roadmap.md` - Phased implementation plan

These documents should be consulted when making architectural decisions or implementing new features to ensure alignment with the original vision.
