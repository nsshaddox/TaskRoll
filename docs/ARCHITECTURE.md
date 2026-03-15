# Architecture Guide

This document describes the architecture, design patterns, and technical decisions of the Random Task application.

## Overview

Random Task is a native Android application built with Kotlin and Jetpack Compose that helps users manage tasks by randomly selecting one from their list. It follows MVVM architecture with clean separation between data, domain, and presentation layers.

## Technology Stack

### Core Framework

- **Kotlin 2.0.21** — Primary language
- **Jetpack Compose (BOM 2026.02.00)** — Declarative UI framework
- **Material3** — Design system and theming
- **Hilt 2.51.1** — Dependency injection via KSP
- **Navigation Compose 2.8.7** — Single-activity navigation

### Data Layer

- **Room 2.8.4** — SQLite abstraction with compile-time query verification
- **Kotlin Coroutines 1.10.2** — Asynchronous operations
- **Kotlin Flow** — Reactive data streams

### Build and Tooling

- **Gradle (Kotlin DSL)** — Build system
- **Android Gradle Plugin 8.9.2** — Android build toolchain
- **KSP 2.0.21-1.0.28** — Kotlin Symbol Processing for annotation processors
- **Version Catalog (`libs.versions.toml`)** — Centralized dependency management

### Platform Targets

- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 35 (Android 15)
- **Compile SDK:** 35
- **Java/Kotlin Target:** JVM 11

## Architectural Patterns

### MVVM with Clean Architecture Layers

The application follows a three-layer architecture with unidirectional data flow:

```
┌─────────────────────────────────────────────────┐
│                Presentation Layer                │
│  Screens (Compose) → ViewModels → UiState        │
│  Navigation, Theme, Preview                      │
├─────────────────────────────────────────────────┤
│                  Domain Layer                    │
│  Use Cases → Repository Interface → Task Model   │
│  Pure Kotlin, no Android dependencies            │
├─────────────────────────────────────────────────┤
│                   Data Layer                     │
│  Room DAO → TaskEntity → TaskRepositoryImpl      │
│  Mappers (Entity ↔ Domain)                       │
├─────────────────────────────────────────────────┤
│              Dependency Injection                 │
│  Hilt Modules: Database, Repository, Coroutine   │
└─────────────────────────────────────────────────┘
```

**Key rule:** Dependencies flow inward. The domain layer has zero Android dependencies. The data layer depends on domain interfaces. The presentation layer depends on domain use cases.

### Data Flow

```
User Action → ViewModel → UseCase → Repository → DAO → Room/SQLite
                                                            │
UI Update  ← StateFlow  ← Flow   ← Flow      ← Flow  ←────┘
```

- **Mutations** (add, update, delete): `suspend` functions returning `Result<T>`
- **Queries** (get tasks, observe): `Flow<T>` for reactive updates
- **UI State**: `StateFlow<UiState>` exposed by ViewModels

## Domain Model

### Core Entity

```kotlin
data class Task(
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean = false,
    val createdAt: Long,    // epoch millis
    val updatedAt: Long     // epoch millis
)
```

### Repository Interface

```kotlin
interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    fun getIncompleteTasks(): Flow<List<Task>>
    fun getTaskById(id: Long): Flow<Task?>
    suspend fun addTask(task: Task): Result<Long>
    suspend fun updateTask(task: Task): Result<Unit>
    suspend fun deleteTask(task: Task): Result<Unit>
}
```

### Use Cases

| Use Case | Responsibility |
|----------|----------------|
| `GetTasksUseCase` | Observes all tasks via repository |
| `GetRandomTaskUseCase` | Selects random incomplete task, returns `null` if none |
| `AddTaskUseCase` | Validates title not blank, delegates to repository |
| `UpdateTaskUseCase` | Validates title, updates timestamp, delegates |
| `CompleteTaskUseCase` | Marks task completed, updates timestamp |
| `DeleteTaskUseCase` | Passes through to repository |

All use cases use constructor injection and `operator fun invoke()` for clean call-site syntax.

## Data Access Layer

### Room Entity

```kotlin
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)
```

### DAO

- Queries return `Flow<List<TaskEntity>>` for reactive observation
- Mutations are `suspend` functions
- `getTaskByIdFlow()` provides Flow-based single-item observation
- `getTaskById()` provides suspend-based single-shot lookup
- All list queries ordered by `created_at DESC`

### Mappers

Extension functions in `TaskMappers.kt` handle bidirectional conversion:
- `TaskEntity.toDomain(): Task`
- `Task.toEntity(): TaskEntity`

No business logic — purely structural transformation.

### Database

- Single `AppDatabase` extending `RoomDatabase`
- Database name: `random_task_db`
- Version 1, schema export disabled (development phase)

## Dependency Injection

Three Hilt modules in the `di/` package:

| Module | Scope | Provides |
|--------|-------|----------|
| `DatabaseModule` | `@Singleton` | `AppDatabase`, `TaskDao` |
| `RepositoryModule` | `@Singleton` | `TaskRepository` (binds `TaskRepositoryImpl`) |
| `CoroutineModule` | `@Singleton` | Named dispatchers: `@Named("IO")`, `@Named("Default")`, `@Named("Main")` |

Named dispatchers enable testability — ViewModels inject dispatchers rather than hardcoding `Dispatchers.IO`.

## Presentation Layer

### Navigation

- `Screen` sealed class defines type-safe routes
- Three destinations: `TaskList` (start), `RandomTask`, `EditTask/{taskId}`
- Single `NavGraph` composable wires all routes
- Dynamic route parameter support via `navArgument`

### Screens

| Screen | ViewModel | State |
|--------|-----------|-------|
| `TaskListScreen` | `TaskListViewModel` | `TaskListUiState` (tasks, loading, error, dialog visibility) |
| `RandomTaskScreen` | `RandomTaskViewModel` | `RandomTaskUiState` (current task, loading, error, no-tasks, completed) |
| `TaskEditorScreen` | `TaskEditorViewModel` | `TaskEditorUiState` (title, loading, saved, error) |

### UI Models

- `TaskUiModel` — Display-ready task with formatted timestamps (`"MMM d, yyyy h:mm a"`)
- `Task.toUiModel()` extension mapper
- `SampleData` object provides preview data for Compose previews

### Theming

- Material3 with dynamic color support (Android 12+)
- Custom teal-based palette for light and dark schemes
- Custom typography scale
- Applied globally via `RandomTaskTheme` composable

## Entry Points

- `RandomTaskApplication` — `@HiltAndroidApp` application class
- `MainActivity` — Single `@AndroidEntryPoint` activity, enables edge-to-edge, hosts `NavGraph`

## Project Structure

```
app/src/main/java/com/nshaddox/randomtask/
├── MainActivity.kt
├── RandomTaskApplication.kt
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── TaskDao.kt
│   │   └── TaskEntity.kt
│   └── repository/
│       ├── TaskRepositoryImpl.kt
│       └── TaskMappers.kt
├── di/
│   ├── DatabaseModule.kt
│   ├── RepositoryModule.kt
│   └── CoroutineModule.kt
├── domain/
│   ├── model/
│   │   └── Task.kt
│   ├── repository/
│   │   └── TaskRepository.kt
│   └── usecase/
│       ├── GetTasksUseCase.kt
│       ├── GetRandomTaskUseCase.kt
│       ├── AddTaskUseCase.kt
│       ├── UpdateTaskUseCase.kt
│       ├── CompleteTaskUseCase.kt
│       └── DeleteTaskUseCase.kt
└── ui/
    ├── navigation/
    │   ├── NavRoutes.kt
    │   └── NavGraph.kt
    ├── screens/
    │   ├── tasklist/
    │   │   ├── TaskListScreen.kt
    │   │   ├── TaskListViewModel.kt
    │   │   ├── TaskListUiState.kt
    │   │   ├── TaskUiModel.kt
    │   │   ├── TaskUiMappers.kt
    │   │   └── AddTaskDialog.kt
    │   ├── randomtask/
    │   │   ├── RandomTaskScreen.kt
    │   │   ├── RandomTaskViewModel.kt
    │   │   └── RandomTaskUiState.kt
    │   └── taskeditor/
    │       ├── TaskEditorScreen.kt
    │       └── TaskEditorViewModel.kt
    ├── preview/
    │   └── MockData.kt
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

## Key Architectural Decisions

1. **MVVM over MVI** — Simpler for this app's scope; ViewModels expose `StateFlow<UiState>`
2. **Use cases as thin wrappers** — Validation lives in use cases, not ViewModels or repositories
3. **Result type for mutations** — All suspend operations return `Result<T>` for explicit error handling
4. **Named dispatchers** — Injected via Hilt for testability instead of hardcoded `Dispatchers.*`
5. **Extension function mappers** — Clean layer transformation without mapper classes
6. **Flow-based queries** — Room's reactive queries propagate database changes automatically to the UI
7. **Single Activity** — All navigation handled by Compose Navigation within one Activity
8. **No DTOs** — Direct entity-to-domain mapping; UI models are separate for display formatting only

## Testing Architecture

For detailed testing patterns and strategies, see the **[Testing Guide](TESTING.md)**.

### Test Pyramid

- **Unit Tests** — Use cases, ViewModels, mappers (JUnit 4, MockK, Turbine, coroutines-test)
- **Integration Tests** — Room DAO operations (Hilt testing, Room testing)
- **UI Tests** — Compose component tests (Compose UI testing, Espresso)

### Test Data

- `FakeTaskRepository` — In-memory test implementation using `MutableStateFlow`
- `SampleData` object — Pre-fabricated task instances for Compose previews
