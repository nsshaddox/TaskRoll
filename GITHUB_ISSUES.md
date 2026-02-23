# Random Task App - MVP GitHub Issues

**Milestone**: v1.0 MVP
**Total Issues**: 35
**Order**: Dependency-sorted — each issue unblocks the next group

| Group                      | Issues                          | What it unlocks        |
|----------------------------|---------------------------------|------------------------|
| 1 — Foundation             | #87, #99, #113                  | Everything             |
| 2 — Core Data Model        | #111                            | Domain + DAO           |
| 3 — Domain Layer + DAO     | #120, #136                      | Mappers, repo interface|
| 4 — Data Layer Completion  | #127, #142, #149                | Repo impl              |
| 5 — Repository             | #150, #151                      | Use cases              |
| 6 — Use Cases              | #81, #90, #102, #118, #109, #124, #132 | ViewModels      |
| 7 — UI State               | #68, #63, #54                   | ViewModels             |
| 8 — ViewModels             | #97, #58                        | Screens                |
| 9 — Nav Routes             | #122                            | NavGraph               |
| 10 — UI Components         | #76, #85                        | Screens                |
| 11 — Screens               | #108, #61                       | Nav wiring             |
| 12 — Navigation Wiring     | #128, #119, #66                 | Feature completion     |
| 13 — Feature Completion    | #95, #131, #77, #69             | Done                   |

---

## Group 1: Foundation
*No dependencies — start here in parallel.*

---

### Issue #87: Setup Room Database foundation
**Labels**: `phase-1`, `architecture`, `database`, `P0-critical`
**Estimated Complexity**: Medium

**Description**:
Configure Room Database as the local persistence layer for task data.

**Acceptance Criteria**:
- [ ] Room dependencies added to `build.gradle.kts`:
  - `androidx.room:room-runtime`
  - `androidx.room:room-compiler` (kapt)
  - `androidx.room:room-ktx` (Coroutines support)
- [ ] Abstract class `AppDatabase` created in `data.local` package
- [ ] `AppDatabase` extends `RoomDatabase`
- [ ] `@Database` annotation with version = 1, exportSchema = false
- [ ] Database instance provided via Hilt in `DatabaseModule`
- [ ] Database name: "random_task_db"
- [ ] Project builds successfully

**Dependencies**: None (was #78 — already done)

---

### Issue #99: Configure Kotlin Coroutines
**Labels**: `phase-1`, `architecture`, `P1-high`
**Estimated Complexity**: Low

**Description**:
Ensure Kotlin Coroutines are properly configured for asynchronous operations.

**Acceptance Criteria**:
- [ ] Coroutines dependencies verified in `build.gradle.kts`:
  - `org.jetbrains.kotlinx:kotlinx-coroutines-core`
  - `org.jetbrains.kotlinx:kotlinx-coroutines-android`
- [ ] Dispatcher configuration for tests (TestDispatcher)
- [ ] Documentation added on coroutine usage patterns
- [ ] Sample coroutine verified to work (e.g., in a ViewModel)

**Dependencies**: None (was #78 — already done)

---

### Issue #113: Add Navigation Compose dependency
**Labels**: `setup`, `ui`, `P0-critical`
**Estimated Complexity**: Low

Add Jetpack Compose Navigation library for screen navigation.

## Acceptance Criteria
- [ ] Dependency added: `androidx.navigation:navigation-compose`
- [ ] Dependency added: `androidx.hilt:hilt-navigation-compose`
- [ ] Project builds successfully
- [ ] Navigation imports work in Kotlin files

**Dependencies**: None

---

## Group 2: Core Data Model
*Requires Group 1 (#87).*

---

### Issue #111: Create Task entity for Room
**Labels**: `phase-1`, `database`, `feature`, `P0-critical`
**Estimated Complexity**: Low

**Description**:
Create the Room entity that represents a task in the database.

**Acceptance Criteria**:
- [ ] Data class `TaskEntity` created in `data.local` package
- [ ] `@Entity` annotation with tableName = "tasks"
- [ ] Fields:
  - `@PrimaryKey(autoGenerate = true) val id: Long = 0`
  - `@ColumnInfo(name = "title") val title: String`
  - `@ColumnInfo(name = "description") val description: String?`
  - `@ColumnInfo(name = "is_completed") val isCompleted: Boolean = false`
  - `@ColumnInfo(name = "created_at") val createdAt: Long`
  - `@ColumnInfo(name = "updated_at") val updatedAt: Long`
- [ ] Proper data types and nullability
- [ ] Documentation comments for each field

**Dependencies**: #87

---

## Group 3: Domain Layer + DAO
*Requires #111.*

---

### Issue #120: Create Task domain model
**Labels**: `phase-1`, `architecture`, `feature`, `P0-critical`
**Estimated Complexity**: Low

**Description**:
Create the domain layer model for Task, separate from the database entity.

**Acceptance Criteria**:
- [ ] Data class `Task` created in `domain.model` package
- [ ] No Room annotations (clean domain model)
- [ ] Fields matching TaskEntity but using appropriate domain types:
  - `val id: Long`
  - `val title: String`
  - `val description: String?`
  - `val isCompleted: Boolean`
  - `val createdAt: Instant` (or LocalDateTime)
  - `val updatedAt: Instant` (or LocalDateTime)
- [ ] Documentation comments
- [ ] No dependencies on Android or Room

**Dependencies**: #111

---

### Issue #136: Create TaskDao interface
**Labels**: `phase-1`, `database`, `feature`, `P0-critical`
**Estimated Complexity**: Medium

**Description**:
Create the Data Access Object (DAO) interface for task database operations.

**Acceptance Criteria**:
- [ ] Interface `TaskDao` created in `data.local` package
- [ ] `@Dao` annotation
- [ ] Methods with Flow return types for reactive updates:
  - `@Query("SELECT * FROM tasks ORDER BY created_at DESC") fun getAllTasks(): Flow<List<TaskEntity>>`
  - `@Query("SELECT * FROM tasks WHERE id = :id") fun getTaskById(id: Long): Flow<TaskEntity?>`
  - `@Query("SELECT * FROM tasks WHERE is_completed = 0 ORDER BY created_at DESC") fun getIncompleteTasks(): Flow<List<TaskEntity>>`
  - `@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertTask(task: TaskEntity): Long`
  - `@Update suspend fun updateTask(task: TaskEntity)`
  - `@Delete suspend fun deleteTask(task: TaskEntity)`
- [ ] Proper SQL queries
- [ ] All operations use suspend functions or Flow

**Dependencies**: #111

---

## Group 4: Data Layer Completion
*Requires #111 and #120.*

---

### Issue #127: Create data-domain mappers
**Labels**: `phase-1`, `architecture`, `P0-critical`
**Estimated Complexity**: Low

**Description**:
Create extension functions to map between TaskEntity (data layer) and Task (domain layer).

**Acceptance Criteria**:
- [ ] File created: `TaskMappers.kt` in `data.local` or `data.repository` package
- [ ] Extension function: `TaskEntity.toDomain(): Task`
- [ ] Extension function: `Task.toEntity(): TaskEntity`
- [ ] Proper timestamp conversions (Long ↔ Instant/LocalDateTime)
- [ ] All fields mapped correctly
- [ ] Unit tests for mappers

**Dependencies**: #111, #120

---

### Issue #142: Wire TaskDao into AppDatabase
**Labels**: `phase-1`, `database`, `P0-critical`
**Estimated Complexity**: Low

**Description**:
Connect the TaskDao to the Room Database and provide it via Hilt.

**Acceptance Criteria**:
- [ ] `AppDatabase` updated with `@Database(entities = [TaskEntity::class], version = 1)`
- [ ] Abstract function added: `abstract fun taskDao(): TaskDao`
- [ ] `DatabaseModule` provides `TaskDao`:
  ```kotlin
  @Provides
  fun provideTaskDao(database: AppDatabase): TaskDao = database.taskDao()
  ```
- [ ] Project builds successfully
- [ ] Database schema verified with `./gradlew kaptDebugKotlin`

**Dependencies**: #136

---

### Issue #149: Create TaskRepository interface
**Labels**: `phase-1`, `architecture`, `P0-critical`, `repository`
**Estimated Complexity**: Low

**Description**:
Define the repository interface in the domain layer for task operations.

**Acceptance Criteria**:
- [ ] Interface `TaskRepository` created in `domain.repository` package
- [ ] Methods return domain models (Task), not entities:
  - `fun getAllTasks(): Flow<List<Task>>`
  - `fun getTaskById(id: Long): Flow<Task?>`
  - `fun getIncompleteTasks(): Flow<List<Task>>`
  - `suspend fun addTask(task: Task): Result<Long>`
  - `suspend fun updateTask(task: Task): Result<Unit>`
  - `suspend fun deleteTask(task: Task): Result<Unit>`
- [ ] Use Kotlin `Result` for operations that can fail
- [ ] No dependencies on data layer
- [ ] Documentation comments explaining each method

**Dependencies**: #120

---

## Group 5: Repository Implementation
*Requires #142, #149, #127.*

---

### Issue #150: Implement TaskRepositoryImpl
**Labels**: `phase-1`, `feature`, `P0-critical`, `repository`
**Estimated Complexity**: Medium

**Description**:
Implement the TaskRepository interface with Room database operations.

**Acceptance Criteria**:
- [ ] Class `TaskRepositoryImpl` created in `data.repository` package
- [ ] Implements `TaskRepository` interface
- [ ] Constructor injects `TaskDao`
- [ ] All methods implemented with proper entity-domain mapping
- [ ] Error handling using `Result.success()` and `Result.failure()`
- [ ] Flow transformations: `dao.getAllTasks().map { entities -> entities.map { it.toDomain() } }`
- [ ] Timestamps updated for add/update operations
- [ ] Unit tests with fake/mock TaskDao

**Dependencies**: #142, #149, #127

---

### Issue #151: Create Hilt module for repository
**Labels**: `phase-1`, `dependency-injection`, `P0-critical`
**Estimated Complexity**: Low

**Description**:
Provide the TaskRepository implementation via Hilt.

**Acceptance Criteria**:
- [ ] File created: `RepositoryModule.kt` in `di` package
- [ ] `@Module` and `@InstallIn(SingletonComponent::class)` annotations
- [ ] Provides binding:
  ```kotlin
  @Binds
  @Singleton
  abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository
  ```
- [ ] TaskRepositoryImpl constructor annotated with `@Inject`
- [ ] Project builds successfully
- [ ] Repository can be injected in ViewModels

**Dependencies**: #150

---

## Group 6: Use Cases
*Requires #151 (repository injectable). #132 additionally requires #90.*

---

### Issue #81: Implement GetAllTasksUseCase
**Labels**: `feature`, `P0-critical`, `use-case`
**Estimated Complexity**: Low

Create use case to retrieve all tasks from the repository.

## Acceptance Criteria
- [ ] Class `GetAllTasksUseCase` created in `domain.usecase` package
- [ ] Constructor injects `TaskRepository`
- [ ] Operator function: `operator fun invoke(): Flow<List<Task>>`
- [ ] Simply delegates to `repository.getAllTasks()`
- [ ] Documentation explaining purpose
- [ ] Unit test with mock repository

**Dependencies**: #149, #151

---

### Issue #90: Implement GetIncompleteTasksUseCase
**Labels**: `feature`, `P0-critical`, `use-case`
**Estimated Complexity**: Low

Create use case to retrieve only incomplete (not done) tasks.

## Acceptance Criteria
- [ ] Class `GetIncompleteTasksUseCase` created in `domain.usecase` package
- [ ] Constructor injects `TaskRepository`
- [ ] Operator function: `operator fun invoke(): Flow<List<Task>>`
- [ ] Delegates to `repository.getIncompleteTasks()`
- [ ] Documentation explaining purpose
- [ ] Unit test with mock repository

**Dependencies**: #149, #151

---

### Issue #102: Implement AddTaskUseCase with validation
**Labels**: `feature`, `P0-critical`, `use-case`
**Estimated Complexity**: Medium

Create use case to add a new task with input validation.

## Acceptance Criteria
- [ ] Class `AddTaskUseCase` created in `domain.usecase` package
- [ ] Constructor injects `TaskRepository`
- [ ] Operator function: `suspend operator fun invoke(title: String, description: String?): Result<Long>`
- [ ] Validation logic:
  - Title cannot be blank (return Result.failure if blank)
  - Title trimmed of whitespace
  - Description trimmed (null if blank)
- [ ] Creates Task with current timestamp
- [ ] Delegates to `repository.addTask()`
- [ ] Unit tests for validation and success cases

**Dependencies**: #149, #151

---

### Issue #118: Implement DeleteTaskUseCase
**Labels**: `feature`, `P0-critical`, `use-case`
**Estimated Complexity**: Low

Create use case to delete a task.

## Acceptance Criteria
- [ ] Class `DeleteTaskUseCase` created in `domain.usecase` package
- [ ] Constructor injects `TaskRepository`
- [ ] Operator function: `suspend operator fun invoke(task: Task): Result<Unit>`
- [ ] Delegates to `repository.deleteTask()`
- [ ] Unit test with mock repository

**Dependencies**: #149, #151

---

### Issue #109: Implement UpdateTaskUseCase
**Labels**: `feature`, `P1-high`, `use-case`
**Estimated Complexity**: Low

Create use case to update an existing task.

## Acceptance Criteria
- [ ] Class `UpdateTaskUseCase` created in `domain.usecase` package
- [ ] Constructor injects `TaskRepository`
- [ ] Operator function: `suspend operator fun invoke(task: Task): Result<Unit>`
- [ ] Updates `updatedAt` timestamp
- [ ] Delegates to `repository.updateTask()`
- [ ] Unit test with mock repository

**Dependencies**: #149, #151

---

### Issue #124: Implement CompleteTaskUseCase
**Labels**: `feature`, `P0-critical`, `use-case`
**Estimated Complexity**: Low

Create use case to mark a task as completed.

## Acceptance Criteria
- [ ] Class `CompleteTaskUseCase` created in `domain.usecase` package
- [ ] Constructor injects `TaskRepository`
- [ ] Operator function: `suspend operator fun invoke(task: Task, isCompleted: Boolean): Result<Unit>`
- [ ] Updates task with new completion status and timestamp
- [ ] Delegates to `repository.updateTask()`
- [ ] Unit test verifying task update

**Dependencies**: #149, #151

---

### Issue #132: Implement GetRandomTaskUseCase (CORE FEATURE)
**Labels**: `feature`, `P0-critical`, `use-case`, `core-feature`
**Estimated Complexity**: Low

**This is the core feature of the app**: Create use case to retrieve a random task from incomplete tasks.

## Acceptance Criteria
- [ ] Class `GetRandomTaskUseCase` created in `domain.usecase` package
- [ ] Constructor injects `TaskRepository`
- [ ] Operator function: `suspend operator fun invoke(): Result<Task?>`
- [ ] Logic:
  1. Get list of incomplete tasks
  2. Return random task from list (use `List.random()`)
  3. Return null if list is empty
- [ ] Returns `Result.success(randomTask)` or `Result.success(null)`
- [ ] Handle errors with `Result.failure()`
- [ ] Unit tests:
  - Returns random task when tasks exist
  - Returns null when no incomplete tasks
  - Verify randomness (multiple calls return different tasks with sufficient data)

**Dependencies**: #90

---

## Group 7: UI Models & State
*Requires #120 (Task domain model).*

---

### Issue #68: Create TaskUiModel
**Labels**: `ui`, `P1-high`, `model`
**Estimated Complexity**: Low

## Description
Create a UI-specific model for displaying tasks (e.g., with formatted dates).

## Acceptance Criteria
- [ ] File created: `TaskUiModel.kt` in `ui.screens.tasklist` package
- [ ] Data class or mapping function to format domain Task for UI
- [ ] Format timestamps to readable strings
- [ ] Any other UI-specific transformations
- [ ] Mapper function: `Task.toUiModel(): TaskUiModel`

**Dependencies**: #120

---

### Issue #63: Create TaskListUiState
**Labels**: `ui`, `P0-critical`, `state`
**Estimated Complexity**: Low

## Description
Define the UI state data class for the task list screen.

## Acceptance Criteria
- [ ] File created: `TaskListUiState.kt` in `ui.screens.tasklist` package
- [ ] Data class with sealed interface or states:
  ```kotlin
  data class TaskListUiState(
      val tasks: List<Task> = emptyList(),
      val isLoading: Boolean = false,
      val error: String? = null,
      val showAddDialog: Boolean = false
  )
  ```
- [ ] Documentation comments explaining each property

**Dependencies**: #68

---

### Issue #54: Create RandomTaskUiState
**Labels**: `ui`, `phase-5`, `P0-critical`, `state`
**Estimated Complexity**: Low

Define the UI state data class for the random task screen.

## Acceptance Criteria
- [ ] File created: `RandomTaskUiState.kt` in `ui.screens.randomtask` package
- [ ] Data class:
  ```kotlin
  data class RandomTaskUiState(
      val currentTask: Task? = null,
      val isLoading: Boolean = false,
      val error: String? = null,
      val noTasksAvailable: Boolean = false
  )
  ```
- [ ] Documentation comments

**Dependencies**: #68

---

## Group 8: ViewModels
*Requires use cases (Group 6) and UI state (Group 7).*

---

### Issue #97: Implement TaskListViewModel
**Labels**: `ui`, `P0-critical`, `viewmodel`
**Estimated Complexity**: High

## Description
Create the ViewModel for the task list screen, managing state and user interactions.

## Acceptance Criteria
- [ ] Class `TaskListViewModel` created in `ui.screens.tasklist` package
- [ ] Annotated with `@HiltViewModel`
- [ ] Constructor injects use cases:
  - GetAllTasksUseCase
  - AddTaskUseCase
  - DeleteTaskUseCase
  - CompleteTaskUseCase
- [ ] Exposes `StateFlow<TaskListUiState>`
- [ ] Functions:
  - `fun addTask(title: String, description: String?)`
  - `fun deleteTask(task: Task)`
  - `fun toggleComplete(task: Task)`
  - `fun showAddDialog()` / `fun hideAddDialog()`
- [ ] Collects tasks from GetAllTasksUseCase into state
- [ ] Handles errors and updates error state
- [ ] Unit tests with TestCoroutineDispatcher

**Dependencies**: #63, #81, #102, #118, #124

---

### Issue #58: Implement RandomTaskViewModel
**Labels**: `ui`, `phase-5`, `P0-critical`, `viewmodel`
**Estimated Complexity**: Medium

## Description
Create the ViewModel for the random task screen.

## Acceptance Criteria
- [ ] Class `RandomTaskViewModel` created in `ui.screens.randomtask` package
- [ ] Annotated with `@HiltViewModel`
- [ ] Constructor injects:
  - GetRandomTaskUseCase
  - CompleteTaskUseCase
- [ ] Exposes `StateFlow<RandomTaskUiState>`
- [ ] Functions:
  - `fun getRandomTask()`
  - `fun completeTask(task: Task)`
  - `fun skipTask()`
- [ ] Unit tests

**Dependencies**: #54, #132, #124

---

## Group 9: Navigation Routes
*Requires #113 (nav dependency). Can be done alongside Group 8.*

---

### Issue #122: Define navigation routes
**Labels**: `ui`, `navigation`, `P0-critical`
**Estimated Complexity**: Low

## Acceptance Criteria
- [ ] Sealed class or object `Screen` created in `ui.navigation` package
- [ ] Routes defined:
  - `TaskList`
  - `RandomTask`
- [ ] Each route has a `route: String` property
- [ ] Documentation

**Dependencies**: #113

---

## Group 10: UI Components
*Requires #68 (TaskUiModel). Can be done alongside Groups 8–9.*

---

### Issue #76: Implement TaskListItem composable
**Labels**: `ui`, `P0-critical`, `component`
**Estimated Complexity**: Low

## Acceptance Criteria
- [ ] Composable `TaskListItem` created in `ui.screens.tasklist` package
- [ ] Displays task title, description (if present), completion status
- [ ] Checkbox or toggle for completion
- [ ] Callbacks: `onComplete: (Task) -> Unit`, `onDelete: (Task) -> Unit`
- [ ] Material3 styling
- [ ] Preview annotation

**Dependencies**: #68

---

### Issue #85: Implement AddTaskDialog composable
**Labels**: `ui`, `P0-critical`, `component`
**Estimated Complexity**: Low

## Acceptance Criteria
- [ ] Composable `AddTaskDialog` created in `ui.screens.tasklist` package
- [ ] Text fields for title (required) and description (optional)
- [ ] Confirm and cancel buttons
- [ ] Input validation: disable confirm if title is blank
- [ ] Callbacks: `onConfirm: (title: String, description: String?) -> Unit`, `onDismiss: () -> Unit`
- [ ] Material3 AlertDialog styling
- [ ] Preview annotation

**Dependencies**: None

---

## Group 11: Screens
*Requires ViewModels (Group 8) and UI components (Group 10).*

---

### Issue #108: Implement TaskListScreen composable
**Labels**: `ui`, `P0-critical`, `screen`
**Estimated Complexity**: High

## Acceptance Criteria
- [ ] Composable `TaskListScreen` created in `ui.screens.tasklist` package
- [ ] Collects state from `TaskListViewModel`
- [ ] Displays list of tasks using `TaskListItem`
- [ ] FAB to open `AddTaskDialog`
- [ ] Empty state when no tasks
- [ ] "Get Random Task" button/FAB navigates to random task screen
- [ ] Error display
- [ ] Material3 Scaffold with TopAppBar

**Dependencies**: #63, #76, #85, #97

---

### Issue #61: Implement RandomTaskScreen composable
**Labels**: `ui`, `phase-5`, `P0-critical`, `screen`
**Estimated Complexity**: Medium

## Acceptance Criteria
- [ ] Composable `RandomTaskScreen` created in `ui.screens.randomtask` package
- [ ] Collects state from `RandomTaskViewModel`
- [ ] Displays current random task (title + description)
- [ ] "Complete" button → marks task done, loads next random task
- [ ] "Skip" button → loads next random task without completing
- [ ] Empty state when no incomplete tasks
- [ ] Back navigation to task list
- [ ] Material3 styling

**Dependencies**: #54, #58

---

## Group 12: Navigation Wiring
*Requires screens (Group 11) and routes (Group 9).*

---

### Issue #128: Implement NavGraph
**Labels**: `ui`, `navigation`, `P0-critical`
**Estimated Complexity**: Low

## Acceptance Criteria
- [ ] Composable `AppNavGraph` created in `ui.navigation` package
- [ ] Uses `NavHost` with all defined routes
- [ ] Start destination: `Screen.TaskList`
- [ ] Each route renders the appropriate screen composable
- [ ] NavController passed through or hoisted appropriately

**Dependencies**: #122, #108, #61

---

### Issue #119: Wire TaskListScreen into navigation
**Labels**: `ui`, `navigation`, `P0-critical`
**Estimated Complexity**: Low

## Acceptance Criteria
- [ ] `TaskListScreen` is the start destination in `NavHost`
- [ ] Navigation action to `RandomTaskScreen` wired up
- [ ] Back stack handled correctly
- [ ] Integration tested manually

**Dependencies**: #128, #108

---

### Issue #66: Wire RandomTaskScreen into navigation
**Labels**: `ui`, `phase-5`, `navigation`, `P0-critical`
**Estimated Complexity**: Low

## Acceptance Criteria
- [ ] `RandomTaskScreen` composable registered in `NavHost`
- [ ] Navigation from TaskListScreen to RandomTaskScreen works
- [ ] Back navigation returns to TaskListScreen
- [ ] Integration tested manually

**Dependencies**: #128, #61

---

## Group 13: Feature Completion
*Requires screens and ViewModels to be wired. Final group.*

---

### Issue #95: Implement edit task functionality
**Labels**: `ui`, `feature`, `P1-high`
**Estimated Complexity**: Medium

## Acceptance Criteria
- [ ] Edit dialog or inline editing in `TaskListScreen`
- [ ] Pre-populates current title and description
- [ ] Calls `UpdateTaskUseCase` via `TaskListViewModel`
- [ ] Validates input (title not blank)
- [ ] UI updates reactively after edit

**Dependencies**: #97, #108, #109

---

### Issue #131: Implement task completion toggle
**Labels**: `ui`, `feature`, `P0-critical`
**Estimated Complexity**: Low

## Acceptance Criteria
- [ ] Checkbox/toggle in `TaskListItem` calls `toggleComplete` on ViewModel
- [ ] Completed tasks visually distinguished (strikethrough or muted color)
- [ ] State updates reactively via Flow
- [ ] Calls `CompleteTaskUseCase`

**Dependencies**: #97, #108, #124

---

### Issue #77: Implement skip task functionality
**Labels**: `ui`, `feature`, `phase-5`, `P0-critical`
**Estimated Complexity**: Low

## Acceptance Criteria
- [ ] "Skip" button on `RandomTaskScreen` loads a new random task
- [ ] Skipped task is NOT marked complete
- [ ] If no more tasks, shows empty state
- [ ] Calls `GetRandomTaskUseCase` via `RandomTaskViewModel`

**Dependencies**: #58, #61

---

### Issue #69: Implement task completion from random screen
**Labels**: `ui`, `feature`, `phase-5`, `P0-critical`
**Estimated Complexity**: Low

## Acceptance Criteria
- [ ] "Complete" button on `RandomTaskScreen` marks the task done
- [ ] After completing, automatically loads the next random task
- [ ] Completed task no longer appears in random selection pool
- [ ] Calls `CompleteTaskUseCase` via `RandomTaskViewModel`

**Dependencies**: #58, #61, #124
