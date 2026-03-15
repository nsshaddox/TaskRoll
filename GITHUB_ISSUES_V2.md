# Random Task App — v2.0 MVP GitHub Issues

**Milestone**: v2.0
**Total Issues**: 33
**Order**: Dependency-sorted — each issue unblocks the next group

| Group                        | Issues                                      | What it unlocks             |
|------------------------------|---------------------------------------------|-----------------------------|
| 1 — Data Model Extensions    | #199, #200, #201, #202                      | Repo updates, use cases     |
| 2 — Repository Updates       | #203, #204, #205                            | New use cases               |
| 3 — New Use Cases            | #206, #207, #208, #209                      | ViewModel updates           |
| 4 — UI State Updates         | #210, #211, #212, #213                      | ViewModels                  |
| 5 — ViewModel Updates        | #214, #215, #216                            | Screens                     |
| 6 — Navigation Routes        | #217                                        | Nav graph updates           |
| 7 — UI Components            | #218, #219, #220, #221                      | Screens                     |
| 8 — Screen Updates           | #222, #223, #224                            | Nav wiring                  |
| 9 — Navigation Wiring        | #225, #226                                  | Feature completion          |
| 10 — Feature Completion      | #227, #228, #229, #230, #231                | Done                        |

---

## Group 1: Data Model Extensions
*No dependencies beyond v1.0 completion. All four issues feed into a single Room migration.*

---

### Issue #199: Add Priority enum and field to Task and TaskEntity
**Labels**: `v2.0`, `architecture`, `database`, `P0-critical`
**Estimated Complexity**: Low
**Branch**: `issue-199-priority-enum`

**Description**:
Introduce a `Priority` enum to the domain layer and add the corresponding column to `TaskEntity`.
Priority is stored as a String in the database so it survives schema changes without conversion.

**Acceptance Criteria**:
- [ ] `enum class Priority { LOW, MEDIUM, HIGH }` created in `domain.model` package
- [ ] `val priority: Priority = Priority.MEDIUM` added to `Task` data class
- [ ] `@ColumnInfo(name = "priority") val priority: String = "MEDIUM"` added to `TaskEntity`
- [ ] `TaskMappers.kt` updated:
  - `toDomain()` converts String → Priority (default `MEDIUM` if unrecognised)
  - `toEntity()` converts Priority → String
- [ ] Unit tests for mapper covering all three priority values and an unrecognised string

**Dependencies**: #120, #127 (v1.0)

---

### Issue #200: Add due date field to Task and TaskEntity
**Labels**: `v2.0`, `architecture`, `database`, `P1-high`
**Estimated Complexity**: Low
**Branch**: `issue-200-due-date`

**Description**:
Add an optional due date to tasks. The domain model uses `LocalDate` for clean business logic;
the entity stores it as epoch day (Long) for simple Room persistence.

**Acceptance Criteria**:
- [ ] `val dueDate: LocalDate? = null` added to `Task` data class
- [ ] `@ColumnInfo(name = "due_date") val dueDate: Long? = null` added to `TaskEntity`
- [ ] `TaskMappers.kt` updated:
  - `toDomain()` converts `Long? → LocalDate?` via `LocalDate.ofEpochDay()`
  - `toEntity()` converts `LocalDate? → Long?` via `LocalDate.toEpochDay()`
- [ ] Unit tests for mapper with a set date and with `null`

**Dependencies**: #120, #127 (v1.0)

---

### Issue #201: Add category tag field to Task and TaskEntity
**Labels**: `v2.0`, `architecture`, `database`, `P1-high`
**Estimated Complexity**: Low
**Branch**: `issue-201-category-tag`

**Description**:
Add an optional free-text category tag to tasks. Categories are plain strings (no separate
entity required) keeping the data model simple while enabling basic organisation.

**Acceptance Criteria**:
- [ ] `val category: String? = null` added to `Task` data class
- [ ] `@ColumnInfo(name = "category") val category: String? = null` added to `TaskEntity`
- [ ] `TaskMappers.kt` updated to pass `category` through both directions
- [ ] Unit tests for mapper with a set category and with `null`

**Dependencies**: #120, #127 (v1.0)

---

### Issue #202: Create Room database migration (version 1 → 2)
**Labels**: `v2.0`, `database`, `P0-critical`
**Estimated Complexity**: Low
**Branch**: `issue-202-db-migration`

**Description**:
Create a single Room migration that adds all three new columns introduced in #199–#201.
Existing rows receive sensible defaults so no data is lost on upgrade.

**Acceptance Criteria**:
- [ ] `MIGRATION_1_2` defined in `AppDatabase` companion object:
  ```kotlin
  val MIGRATION_1_2 = object : Migration(1, 2) {
      override fun migrate(db: SupportSQLiteDatabase) {
          db.execSQL("ALTER TABLE tasks ADD COLUMN priority TEXT NOT NULL DEFAULT 'MEDIUM'")
          db.execSQL("ALTER TABLE tasks ADD COLUMN due_date INTEGER")
          db.execSQL("ALTER TABLE tasks ADD COLUMN category TEXT")
      }
  }
  ```
- [ ] `@Database(version = 2)` updated in `AppDatabase`
- [ ] Migration added to `Room.databaseBuilder(…).addMigrations(MIGRATION_1_2)` in `DatabaseModule`
- [ ] `exportSchema = true` (or schema exported and committed) so migration can be validated
- [ ] Project builds and `./gradlew connectedAndroidTest` passes with migration

**Dependencies**: #199, #200, #201, #87, #142 (v1.0)

---

## Group 2: Repository Updates
*Requires Group 1 (migration complete, new fields available).*

---

### Issue #203: Update TaskRepository interface with filter and search methods
**Labels**: `v2.0`, `architecture`, `P0-critical`, `repository`
**Estimated Complexity**: Low
**Branch**: `issue-203-repo-filters`

**Description**:
Extend the domain repository interface with query methods that expose the new Priority and
category fields, plus full-text search across title and description.

**Acceptance Criteria**:
- [ ] Add to `TaskRepository` interface in `domain.repository`:
  - `fun getCompletedTasks(): Flow<List<Task>>`
  - `fun searchTasks(query: String): Flow<List<Task>>`
  - `fun getTasksByPriority(priority: Priority): Flow<List<Task>>`
  - `fun getTasksByCategory(category: String): Flow<List<Task>>`
- [ ] Documentation comments for each new method
- [ ] No data-layer imports (clean domain interface)

**Dependencies**: #149 (v1.0), #199, #201

---

### Issue #204: Update TaskDao and TaskRepositoryImpl with new query implementations
**Labels**: `v2.0`, `database`, `P0-critical`, `repository`
**Estimated Complexity**: Medium
**Branch**: `issue-204-dao-queries`

**Description**:
Add corresponding DAO queries and wire them through `TaskRepositoryImpl` with proper
entity-to-domain mapping.

**Acceptance Criteria**:
- [ ] Add to `TaskDao` in `data.local`:
  ```sql
  @Query("SELECT * FROM tasks WHERE is_completed = 1 ORDER BY updated_at DESC")
  fun getCompletedTasks(): Flow<List<TaskEntity>>

  @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY created_at DESC")
  fun searchTasks(query: String): Flow<List<TaskEntity>>

  @Query("SELECT * FROM tasks WHERE priority = :priority AND is_completed = 0 ORDER BY created_at DESC")
  fun getTasksByPriority(priority: String): Flow<List<TaskEntity>>

  @Query("SELECT * FROM tasks WHERE category = :category AND is_completed = 0 ORDER BY created_at DESC")
  fun getTasksByCategory(category: String): Flow<List<TaskEntity>>
  ```
- [ ] `TaskRepositoryImpl` implements all four new methods with entity-domain mapping
- [ ] Unit tests with a fake `TaskDao` covering each new method

**Dependencies**: #203, #150 (v1.0), #202

---

### Issue #205: Update AddTaskUseCase to accept priority, due date, and category
**Labels**: `v2.0`, `use-case`, `P0-critical`
**Estimated Complexity**: Low
**Branch**: `issue-205-add-task-fields`

**Description**:
Extend `AddTaskUseCase` to propagate the three new task fields introduced in v2.0.
Default values keep all existing call sites valid without modification.

**Acceptance Criteria**:
- [ ] `AddTaskUseCase` operator function updated:
  ```kotlin
  suspend operator fun invoke(
      title: String,
      description: String?,
      priority: Priority = Priority.MEDIUM,
      dueDate: LocalDate? = null,
      category: String? = null
  ): Result<Long>
  ```
- [ ] New parameters passed through when constructing the `Task` domain model
- [ ] Existing validation unchanged (blank title → `Result.failure`)
- [ ] Unit tests for default values and each explicit combination

**Dependencies**: #102 (v1.0), #199, #200, #201

---

## Group 3: New Use Cases
*Requires #203 and #204 (new repository methods available).*

---

### Issue #206: Implement GetCompletedTasksUseCase
**Labels**: `v2.0`, `use-case`, `P0-critical`
**Estimated Complexity**: Low
**Branch**: `issue-206-completed-usecase`

**Description**:
Create a use case to retrieve all completed tasks for the history screen.

**Acceptance Criteria**:
- [ ] Class `GetCompletedTasksUseCase` created in `domain.usecase` package
- [ ] Constructor injects `TaskRepository`
- [ ] Operator function: `operator fun invoke(): Flow<List<Task>>`
- [ ] Delegates to `repository.getCompletedTasks()`
- [ ] Documentation explaining purpose
- [ ] Unit test with mock repository

**Dependencies**: #203, #151 (v1.0)

---

### Issue #207: Implement SearchTasksUseCase
**Labels**: `v2.0`, `use-case`, `P1-high`
**Estimated Complexity**: Low
**Branch**: `issue-207-search-usecase`

**Description**:
Create a use case that wraps task search, guarding against empty queries.

**Acceptance Criteria**:
- [ ] Class `SearchTasksUseCase` created in `domain.usecase` package
- [ ] Constructor injects `TaskRepository`
- [ ] Operator function: `operator fun invoke(query: String): Flow<List<Task>>`
- [ ] Returns `flowOf(emptyList())` immediately if `query.isBlank()`
- [ ] Otherwise delegates to `repository.searchTasks(query.trim())`
- [ ] Unit tests: blank query returns empty list, non-blank delegates to repository

**Dependencies**: #203, #151 (v1.0)

---

### Issue #208: Implement GetTasksByPriorityUseCase
**Labels**: `v2.0`, `use-case`, `P1-high`
**Estimated Complexity**: Low
**Branch**: `issue-208-priority-usecase`

**Description**:
Create a use case to retrieve incomplete tasks filtered to a specific priority level.

**Acceptance Criteria**:
- [ ] Class `GetTasksByPriorityUseCase` created in `domain.usecase` package
- [ ] Constructor injects `TaskRepository`
- [ ] Operator function: `operator fun invoke(priority: Priority): Flow<List<Task>>`
- [ ] Delegates to `repository.getTasksByPriority(priority)`
- [ ] Unit test with mock repository

**Dependencies**: #203, #151 (v1.0)

---

### Issue #209: Implement GetTasksByCategoryUseCase
**Labels**: `v2.0`, `use-case`, `P1-high`
**Estimated Complexity**: Low
**Branch**: `issue-209-category-usecase`

**Description**:
Create a use case to retrieve incomplete tasks belonging to a specific category tag.

**Acceptance Criteria**:
- [ ] Class `GetTasksByCategoryUseCase` created in `domain.usecase` package
- [ ] Constructor injects `TaskRepository`
- [ ] Operator function: `operator fun invoke(category: String): Flow<List<Task>>`
- [ ] Delegates to `repository.getTasksByCategory(category)`
- [ ] Unit test with mock repository

**Dependencies**: #203, #151 (v1.0)

---

## Group 4: UI State Updates
*Requires Group 1 (updated Task domain model).*

---

### Issue #210: Update TaskUiModel with priority, due date, and category display fields
**Labels**: `v2.0`, `ui`, `P0-critical`, `model`
**Estimated Complexity**: Low
**Branch**: `issue-210-ui-model`

**Description**:
Extend `TaskUiModel` with display-ready representations of the three new task fields.

**Acceptance Criteria**:
- [ ] `TaskUiModel` updated with:
  - `val priority: Priority` (raw value for color logic in composables)
  - `val priorityLabel: String` (e.g., `"High"`, `"Medium"`, `"Low"`)
  - `val dueDateLabel: String?` (e.g., `"Mar 5"` or `null` if no due date)
  - `val isOverdue: Boolean` (true if `dueDate != null && dueDate < LocalDate.now()` and task is incomplete)
  - `val category: String?`
- [ ] `Task.toUiModel()` mapper updated to populate all new fields
- [ ] Unit tests for all new fields, including overdue logic

**Dependencies**: #68 (v1.0), #199, #200, #201

---

### Issue #211: Create CompletedTasksUiState
**Labels**: `v2.0`, `ui`, `P0-critical`, `state`
**Estimated Complexity**: Low
**Branch**: `issue-211-completed-state`

**Description**:
Define the UI state for the completed tasks history screen.

**Acceptance Criteria**:
- [ ] File created: `CompletedTasksUiState.kt` in `ui.screens.completedtasks` package
- [ ] Data class:
  ```kotlin
  data class CompletedTasksUiState(
      val tasks: List<TaskUiModel> = emptyList(),
      val isLoading: Boolean = false,
      val error: String? = null
  )
  ```
- [ ] Documentation comments

**Dependencies**: #210

---

### Issue #212: Create SettingsUiState
**Labels**: `v2.0`, `ui`, `P1-high`, `state`
**Estimated Complexity**: Low
**Branch**: `issue-212-settings-state`

**Description**:
Define the UI state for the settings screen, including theme preference and default sort order.

**Acceptance Criteria**:
- [ ] File created: `SettingsUiState.kt` in `ui.screens.settings` package
- [ ] Supporting enums created in the same file (or adjacent `domain.model`):
  ```kotlin
  enum class AppTheme { LIGHT, DARK, SYSTEM }
  enum class SortOrder { CREATED_DATE_DESC, DUE_DATE_ASC, PRIORITY_DESC, TITLE_ASC }
  ```
- [ ] Data class:
  ```kotlin
  data class SettingsUiState(
      val theme: AppTheme = AppTheme.SYSTEM,
      val defaultSortOrder: SortOrder = SortOrder.CREATED_DATE_DESC
  )
  ```
- [ ] Documentation comments

**Dependencies**: None

---

### Issue #213: Update TaskListUiState with search, filter, and sort fields
**Labels**: `v2.0`, `ui`, `P0-critical`, `state`
**Estimated Complexity**: Low
**Branch**: `issue-213-list-state`

**Description**:
Extend `TaskListUiState` to carry the active search query, filter selections, sort order,
and the list of distinct categories derived from current tasks.

**Acceptance Criteria**:
- [ ] `TaskListUiState` updated with:
  ```kotlin
  val searchQuery: String = "",
  val filterPriority: Priority? = null,
  val filterCategory: String? = null,
  val sortOrder: SortOrder = SortOrder.CREATED_DATE_DESC,
  val availableCategories: List<String> = emptyList()
  ```
- [ ] Existing fields unchanged
- [ ] Documentation comments for each new property

**Dependencies**: #63 (v1.0), #210, #212

---

## Group 5: ViewModel Updates
*Requires Groups 3 and 4.*

---

### Issue #214: Update TaskListViewModel with search, filter, and sort
**Labels**: `v2.0`, `ui`, `P0-critical`, `viewmodel`
**Estimated Complexity**: High
**Branch**: `issue-214-list-viewmodel`

**Description**:
Extend `TaskListViewModel` to orchestrate search, filtering by priority and category,
and in-memory sorting. The task list shown to the UI is the result of applying all active
filters and sort order to the full task list from the use case.

**Acceptance Criteria**:
- [ ] `SearchTasksUseCase`, `GetTasksByPriorityUseCase`, `GetTasksByCategoryUseCase` injected
- [ ] New functions:
  - `fun updateSearchQuery(query: String)`
  - `fun setFilterPriority(priority: Priority?)`
  - `fun setFilterCategory(category: String?)`
  - `fun setSortOrder(sortOrder: SortOrder)`
- [ ] `addTask` updated to accept `priority`, `dueDate`, `category` and pass them to `AddTaskUseCase`
- [ ] `availableCategories` in state derived reactively from all tasks (distinct non-null categories)
- [ ] Filtering and sorting applied in-memory after the active use case emits
- [ ] Unit tests for each new function including combined filter + sort scenarios

**Dependencies**: #97 (v1.0), #205, #207, #208, #209, #213

---

### Issue #215: Implement CompletedTasksViewModel
**Labels**: `v2.0`, `ui`, `P0-critical`, `viewmodel`
**Estimated Complexity**: Low
**Branch**: `issue-215-completed-vm`

**Description**:
Create the ViewModel for the completed tasks history screen.

**Acceptance Criteria**:
- [ ] Class `CompletedTasksViewModel` created in `ui.screens.completedtasks` package
- [ ] Annotated with `@HiltViewModel`
- [ ] Constructor injects `GetCompletedTasksUseCase`, `DeleteTaskUseCase`
- [ ] Exposes `StateFlow<CompletedTasksUiState>`
- [ ] Functions:
  - `fun deleteTask(task: Task)` — permanently removes a completed task
- [ ] Collects completed tasks into state on init
- [ ] Unit tests with `TestCoroutineDispatcher`

**Dependencies**: #211, #206, #118 (v1.0)

---

### Issue #216: Implement SettingsViewModel
**Labels**: `v2.0`, `ui`, `P1-high`, `viewmodel`
**Estimated Complexity**: Medium
**Branch**: `issue-216-settings-vm`

**Description**:
Create the ViewModel for the settings screen. Preferences are persisted using
`DataStore<Preferences>` so they survive process death and app restarts.

**Acceptance Criteria**:
- [ ] `androidx.datastore:datastore-preferences` dependency added to `build.gradle.kts`
- [ ] `DataStore<Preferences>` provided via a new Hilt module (e.g., `DataStoreModule`)
- [ ] Class `SettingsViewModel` created in `ui.screens.settings` package
- [ ] Annotated with `@HiltViewModel`
- [ ] Constructor injects `DataStore<Preferences>`
- [ ] Exposes `StateFlow<SettingsUiState>`
- [ ] Functions:
  - `fun setTheme(theme: AppTheme)`
  - `fun setSortOrder(sortOrder: SortOrder)`
- [ ] Each function writes to DataStore and state updates reactively
- [ ] Unit tests using `TestCoroutineDispatcher` and an in-memory DataStore

**Dependencies**: #212

---

## Group 6: Navigation Routes
*Requires #122 (v1.0 — existing Screen sealed class).*

---

### Issue #217: Add CompletedTasks and Settings navigation routes
**Labels**: `v2.0`, `ui`, `navigation`, `P0-critical`
**Estimated Complexity**: Low
**Branch**: `issue-217-nav-routes`

**Description**:
Extend the `Screen` sealed class with routes for the two new screens introduced in v2.0.

**Acceptance Criteria**:
- [ ] `Screen.CompletedTasks` route added: `object CompletedTasks : Screen("completed_tasks")`
- [ ] `Screen.Settings` route added: `object Settings : Screen("settings")`
- [ ] Documentation comments
- [ ] Project builds successfully

**Dependencies**: #122 (v1.0)

---

## Group 7: UI Components
*Requires Group 4 (updated models with Priority and category). Can be done alongside Group 5.*

---

### Issue #218: Implement PriorityBadge composable
**Labels**: `v2.0`, `ui`, `P0-critical`, `component`
**Estimated Complexity**: Low
**Branch**: `issue-218-priority-badge`

**Description**:
Create a small reusable composable that displays a colour-coded badge for a task's priority.
Placed in a shared `ui.components` package for reuse across screens.

**Acceptance Criteria**:
- [ ] Composable `PriorityBadge(priority: Priority, modifier: Modifier = Modifier)` created in `ui.components` package
- [ ] Distinct Material3 colours per level:
  - `LOW` → green tonal container
  - `MEDIUM` → amber/yellow tonal container
  - `HIGH` → red/error tonal container
- [ ] Displays the priority label text inside the badge
- [ ] `@PreviewLightDark` annotation showing all three variants
- [ ] No business logic — purely presentational

**Dependencies**: #199, #210

---

### Issue #219: Implement EditTaskDialog composable
**Labels**: `v2.0`, `ui`, `P0-critical`, `component`
**Estimated Complexity**: Medium
**Branch**: `issue-219-edit-dialog`

**Description**:
Create a full-featured edit dialog that pre-populates all task fields and supports the
new v2.0 fields (priority, due date, category). Replaces the add flow's simple
`AddTaskDialog` for edit operations and augments the add flow to include new fields.

**Acceptance Criteria**:
- [ ] Composable `EditTaskDialog` created in `ui.screens.tasklist` package
- [ ] Parameters include `task: TaskUiModel?` (null = add mode, non-null = edit mode)
- [ ] Fields displayed:
  - Title `TextField` (required)
  - Description `TextField` (optional, multi-line)
  - Priority selector (segmented button: Low / Medium / High)
  - Due date field — tapping opens `DueDatePickerDialog`; shows formatted date or "No due date"
  - Category `TextField` (optional, single-line)
- [ ] Confirm button disabled when title is blank
- [ ] Callbacks:
  ```kotlin
  onConfirm: (title: String, description: String?, priority: Priority, dueDate: LocalDate?, category: String?) -> Unit
  onDismiss: () -> Unit
  ```
- [ ] Material3 `AlertDialog` styling
- [ ] `@PreviewLightDark` in add mode and edit mode

**Dependencies**: #85 (v1.0), #199, #200, #201, #210

---

### Issue #220: Implement TaskFilterBar composable
**Labels**: `v2.0`, `ui`, `P1-high`, `component`
**Estimated Complexity**: Medium
**Branch**: `issue-220-filter-bar`

**Description**:
Create a composable filter/search bar displayed within `TaskListScreen` that gives users
controls to search, filter by priority, filter by category, and choose sort order.

**Acceptance Criteria**:
- [ ] Composable `TaskFilterBar` created in `ui.screens.tasklist` package
- [ ] Components:
  - `OutlinedTextField` for search query (with clear icon when non-empty)
  - Priority filter chips: All / Low / Medium / High (single-select)
  - Category filter dropdown (only shown when `availableCategories` is non-empty)
  - Sort order dropdown: "Newest", "Due Date", "Priority", "Title A–Z"
- [ ] Callbacks:
  ```kotlin
  onSearchQueryChange: (String) -> Unit
  onPriorityFilterChange: (Priority?) -> Unit
  onCategoryFilterChange: (String?) -> Unit
  onSortOrderChange: (SortOrder) -> Unit
  ```
- [ ] Collapsible (hidden when no tasks exist)
- [ ] Material3 styling
- [ ] `@PreviewLightDark` annotation

**Dependencies**: #199, #201, #212, #213

---

### Issue #221: Implement DueDatePickerDialog composable
**Labels**: `v2.0`, `ui`, `P1-high`, `component`
**Estimated Complexity**: Low
**Branch**: `issue-221-date-picker`

**Description**:
Wrap Material3's `DatePicker` in a dialog composable that returns a `LocalDate` on confirm.
Used by `EditTaskDialog` to allow users to set or clear a due date.

**Acceptance Criteria**:
- [ ] Composable `DueDatePickerDialog` created in `ui.components` package
- [ ] Parameters: `initialDate: LocalDate?`, `onConfirm: (LocalDate?) -> Unit`, `onDismiss: () -> Unit`
- [ ] Uses Material3 `DatePicker` / `DatePickerDialog`
- [ ] "Clear" button sets date to null and calls `onConfirm(null)`
- [ ] Confirm button calls `onConfirm(selectedDate)`
- [ ] `@Preview` annotation
- [ ] No business logic — purely presentational

**Dependencies**: #200

---

## Group 8: Screen Updates
*Requires ViewModel updates (Group 5) and UI components (Group 7).*

---

### Issue #222: Update TaskListScreen with search bar, filter bar, and full edit support
**Labels**: `v2.0`, `ui`, `P0-critical`, `screen`
**Estimated Complexity**: High
**Branch**: `issue-222-list-screen`

**Description**:
Integrate the new `TaskFilterBar` and `EditTaskDialog` into `TaskListScreen`.
The add flow is upgraded to use `EditTaskDialog` (in add mode) so new tasks can include
priority, due date, and category. The edit action on each `TaskListItem` opens
`EditTaskDialog` pre-populated with the task's current values.

**Acceptance Criteria**:
- [ ] `TaskFilterBar` rendered below the `TopAppBar`, above the task list
- [ ] Search, priority filter, category filter, and sort order wired to `TaskListViewModel`
- [ ] FAB opens `EditTaskDialog` in add mode (task = null)
- [ ] Long-press or edit icon on `TaskListItem` opens `EditTaskDialog` in edit mode
- [ ] `EditTaskDialog` onConfirm calls appropriate ViewModel function (`addTask` or `editTask`)
- [ ] "View Completed" icon/button in `TopAppBar` navigates to `CompletedTasksScreen`
- [ ] Settings icon in `TopAppBar` navigates to `SettingsScreen`
- [ ] `PriorityBadge` displayed inside `TaskListItem` when priority is set
- [ ] Due date label displayed in `TaskListItem` (overdue in error color)
- [ ] Category label displayed in `TaskListItem` when present

**Dependencies**: #108 (v1.0), #214, #218, #219, #220

---

### Issue #223: Implement CompletedTasksScreen composable
**Labels**: `v2.0`, `ui`, `P0-critical`, `screen`
**Estimated Complexity**: Medium
**Branch**: `issue-223-completed-screen`

**Description**:
Create a dedicated screen that shows the user's task completion history.

**Acceptance Criteria**:
- [ ] Composable `CompletedTasksScreen` created in `ui.screens.completedtasks` package
- [ ] Collects state from `CompletedTasksViewModel`
- [ ] Displays completed tasks in a `LazyColumn` using `TaskListItem` (checkboxes hidden/disabled)
- [ ] Each item shows completed timestamp in the subtitle
- [ ] Swipe-to-delete gesture permanently removes a completed task
- [ ] Empty state with illustration/message when no completed tasks
- [ ] Back navigation to `TaskListScreen`
- [ ] Material3 `Scaffold` with `TopAppBar`

**Dependencies**: #215, #218

---

### Issue #224: Implement SettingsScreen composable
**Labels**: `v2.0`, `ui`, `P1-high`, `screen`
**Estimated Complexity**: Low
**Branch**: `issue-224-settings-screen`

**Description**:
Create a settings screen that lets users choose the app theme and default sort order.

**Acceptance Criteria**:
- [ ] Composable `SettingsScreen` created in `ui.screens.settings` package
- [ ] Collects state from `SettingsViewModel`
- [ ] Theme section: segmented button or radio group for Light / Dark / System
  - Selection calls `SettingsViewModel.setTheme()`
- [ ] Default sort order section: radio group for Newest / Due Date / Priority / Title A–Z
  - Selection calls `SettingsViewModel.setSortOrder()`
- [ ] About section: app version from `BuildConfig.VERSION_NAME`
- [ ] Back navigation to `TaskListScreen`
- [ ] Material3 `Scaffold` with `TopAppBar`

**Dependencies**: #216

---

## Group 9: Navigation Wiring
*Requires #217 (new routes) and screens from Group 8.*

---

### Issue #225: Wire CompletedTasksScreen into navigation
**Labels**: `v2.0`, `ui`, `navigation`, `P0-critical`
**Estimated Complexity**: Low
**Branch**: `issue-225-completed-nav`

**Acceptance Criteria**:
- [ ] `CompletedTasksScreen` registered in `AppNavGraph` under `Screen.CompletedTasks.route`
- [ ] Navigation from `TaskListScreen` ("View Completed" action) to `CompletedTasksScreen` works
- [ ] Back navigation returns to `TaskListScreen`
- [ ] Integration tested manually on device/emulator

**Dependencies**: #217, #223, #128 (v1.0)

---

### Issue #226: Wire SettingsScreen into navigation
**Labels**: `v2.0`, `ui`, `navigation`, `P1-high`
**Estimated Complexity**: Low
**Branch**: `issue-226-settings-nav`

**Acceptance Criteria**:
- [ ] `SettingsScreen` registered in `AppNavGraph` under `Screen.Settings.route`
- [ ] Settings icon in `TaskListScreen` `TopAppBar` navigates to `SettingsScreen`
- [ ] Back navigation returns to `TaskListScreen`
- [ ] Integration tested manually on device/emulator

**Dependencies**: #217, #224, #128 (v1.0)

---

## Group 10: Feature Completion
*Requires screens and navigation wired (Groups 8–9). Final group.*

---

### Issue #227: Swipe-to-delete gesture on TaskListItem
**Labels**: `v2.0`, `ui`, `feature`, `P1-high`
**Estimated Complexity**: Low
**Branch**: `issue-227-swipe-delete`

**Description**:
Add a swipe-left-to-delete gesture to `TaskListItem` using Material3's `SwipeToDismissBox`.
Works in both `TaskListScreen` (incomplete tasks) and `CompletedTasksScreen`.

**Acceptance Criteria**:
- [ ] `SwipeToDismissBox` wraps `TaskListItem` in both screens
- [ ] Swipe left reveals a red background with a `Icons.Default.Delete` icon
- [ ] On full swipe, the appropriate ViewModel `deleteTask` function is called
- [ ] Item animates out of the list smoothly
- [ ] Existing explicit delete button (if any) still works

**Dependencies**: #222, #223

---

### Issue #228: Undo task deletion with Snackbar
**Labels**: `v2.0`, `ui`, `feature`, `P1-high`
**Estimated Complexity**: Medium
**Branch**: `issue-228-undo-delete`

**Description**:
After any task deletion (swipe or button), show a Snackbar with an "Undo" action that
restores the task before it is permanently gone.

**Acceptance Criteria**:
- [ ] After deletion, a `Snackbar` appears: `"Task deleted"` with `"Undo"` action
- [ ] Tapping "Undo" calls `AddTaskUseCase` with all original task fields to restore it
- [ ] Snackbar auto-dismisses after 4 seconds; if dismissed without undo, deletion is final
- [ ] Works in both `TaskListScreen` and `CompletedTasksScreen`
- [ ] Only one pending undo at a time (new deletion replaces previous undo)

**Dependencies**: #227, #102 (v1.0)

---

### Issue #229: Sort tasks by priority, due date, title, or creation date
**Labels**: `v2.0`, `ui`, `feature`, `P0-critical`
**Estimated Complexity**: Low
**Branch**: `issue-229-sort-tasks`

**Description**:
Implement in-memory task sorting in `TaskListViewModel` using the `SortOrder` enum.
The active sort order is applied after filtering and reflected in `TaskListUiState`.

**Acceptance Criteria**:
- [ ] `TaskListViewModel` applies `sortOrder` to the task list after filtering:
  - `CREATED_DATE_DESC` → newest first (default)
  - `DUE_DATE_ASC` → tasks without a due date sorted last
  - `PRIORITY_DESC` → HIGH → MEDIUM → LOW
  - `TITLE_ASC` → alphabetical
- [ ] `setSortOrder(sortOrder: SortOrder)` function updates state reactively
- [ ] Sort order persisted via `SettingsViewModel` DataStore and restored on app restart
- [ ] Unit tests for each sort order including edge cases (null due dates, mixed priorities)

**Dependencies**: #214, #216

---

### Issue #230: Theme toggle (light / dark / system) in Settings
**Labels**: `v2.0`, `ui`, `feature`, `P1-high`
**Estimated Complexity**: Medium
**Branch**: `issue-230-theme-toggle`

**Description**:
Apply the user's chosen theme from `SettingsViewModel` to `MaterialTheme` in `MainActivity`
so the entire app reflects the preference immediately and persistently.

**Acceptance Criteria**:
- [ ] `MainActivity` collects `SettingsViewModel.uiState` and reads `theme`
- [ ] Theme applied to `MaterialTheme` via `darkTheme` parameter:
  - `AppTheme.LIGHT` → `darkTheme = false`
  - `AppTheme.DARK` → `darkTheme = true`
  - `AppTheme.SYSTEM` → `darkTheme = isSystemInDarkTheme()`
- [ ] Theme switches immediately when changed in Settings (no restart required)
- [ ] Selection persists across app restarts via DataStore

**Dependencies**: #216, #224

---

### Issue #231: Implement GetWeightedRandomTaskUseCase
**Labels**: `v2.0`, `use-case`, `feature`, `P2-medium`, `core-feature`
**Estimated Complexity**: Low
**Branch**: `issue-231-weighted-random`

**Description**:
Add a priority-weighted variant of the random task selection: HIGH priority tasks have
3× the chance of being selected, MEDIUM 2×, LOW 1×. The original uniform-random use case
is unchanged. `RandomTaskViewModel` gains a toggle to switch between modes.

**Acceptance Criteria**:
- [ ] Class `GetWeightedRandomTaskUseCase` created in `domain.usecase` package
- [ ] Constructor injects `TaskRepository`
- [ ] Operator function: `suspend operator fun invoke(): Result<Task?>`
- [ ] Logic:
  1. Get list of incomplete tasks from `repository.getIncompleteTasks()`
  2. Build weighted list: each HIGH task added 3×, MEDIUM 2×, LOW 1×
  3. Return `List.random()` on the weighted list, or `null` if empty
- [ ] `RandomTaskViewModel` gains `val useWeightedRandom: StateFlow<Boolean>` and
  `fun toggleWeightedRandom()`
- [ ] `RandomTaskScreen` shows a toggle switch labelled "Prioritise high-priority tasks"
- [ ] Unit tests:
  - Weighted distribution is correct (verify list composition)
  - Returns null when no incomplete tasks
  - Toggle in ViewModel switches between use cases

**Dependencies**: #132 (v1.0), #199, #203
