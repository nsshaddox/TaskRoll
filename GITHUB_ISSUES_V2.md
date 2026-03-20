# Random Task App — v2.0 MVP GitHub Issues

**Milestone**: v2.0
**Total Issues**: 33
**Status**: ALL COMPLETE
**Order**: Dependency-sorted — each issue unblocks the next group

| Group                        | Issues                                                      | What it unlocks             |
|------------------------------|-------------------------------------------------------------|-----------------------------|
| 1 — Data Model Extensions    | #199 ✅, #200 ✅, #201 ✅, #202 ✅                            | Repo updates, use cases     |
| 2 — Repository Updates       | #203 ✅, #204 ✅, #205 ✅                                     | New use cases               |
| 3 — New Use Cases            | #206 ✅, #207 ✅, #208 ✅, #209 ✅                            | ViewModel updates           |
| 4 — UI State Updates         | #210 ✅, #211 ✅, #212 ✅, #213 ✅                            | ViewModels                  |
| 5 — ViewModel Updates        | #214 ✅, #215 ✅, #216 ✅                                     | Screens                     |
| 6 — Navigation Routes        | #217 ✅                                                      | Nav graph updates           |
| 7 — UI Components            | #218 ✅, #219 ✅, #220 ✅, #221 ✅                            | Screens                     |
| 8 — Screen Updates           | #222 ✅, #223 ✅, #224 ✅                                     | Nav wiring                  |
| 9 — Navigation Wiring        | #225 ✅, #226 ✅                                              | Feature completion          |
| 10 — Feature Completion      | #227 ✅, #228 ✅, #229 ✅, #230 ✅, #231 ✅                   | Done                        |

---

## Group 1: Data Model Extensions
*No dependencies beyond v1.0 completion. All four issues feed into a single Room migration.*

---

### Issue #199: Add Priority enum and field to Task and TaskEntity
**Labels**: `v2.0`, `architecture`, `database`, `P0-critical`
**Estimated Complexity**: Low
**Branch**: `issue-199-priority-enum`
**Status**: ✅ COMPLETED

**Description**:
Introduce a `Priority` enum to the domain layer and add the corresponding column to `TaskEntity`.
Priority is stored as a String in the database so it survives schema changes without conversion.

**Acceptance Criteria**:
- [x] `enum class Priority { LOW, MEDIUM, HIGH }` created in `domain.model` package
- [x] `val priority: Priority = Priority.MEDIUM` added to `Task` data class
- [x] `@ColumnInfo(name = "priority") val priority: String = "MEDIUM"` added to `TaskEntity`
- [x] `TaskMappers.kt` updated:
  - `toDomain()` converts String → Priority (default `MEDIUM` if unrecognised)
  - `toEntity()` converts Priority → String
- [x] Unit tests for mapper covering all three priority values and an unrecognised string

**Dependencies**: #120, #127 (v1.0)

---

### Issue #200: Add due date field to Task and TaskEntity
**Labels**: `v2.0`, `architecture`, `database`, `P1-high`
**Estimated Complexity**: Low
**Branch**: `issue-200-due-date`
**Status**: ✅ COMPLETED

**Description**:
Add an optional due date to tasks. The domain model uses `LocalDate` for clean business logic;
the entity stores it as epoch day (Long) for simple Room persistence.

**Acceptance Criteria**:
- [x] `val dueDate: LocalDate? = null` added to `Task` data class
- [x] `@ColumnInfo(name = "due_date") val dueDate: Long? = null` added to `TaskEntity`
- [x] `TaskMappers.kt` updated:
  - `toDomain()` converts `Long? → LocalDate?` via `LocalDate.ofEpochDay()`
  - `toEntity()` converts `LocalDate? → Long?` via `LocalDate.toEpochDay()`
- [x] Unit tests for mapper with a set date and with `null`

**Dependencies**: #120, #127 (v1.0)

---

### Issue #201: Add category tag field to Task and TaskEntity
**Labels**: `v2.0`, `architecture`, `database`, `P1-high`
**Estimated Complexity**: Low
**Branch**: `issue-201-category-tag`
**Status**: ✅ COMPLETED

**Description**:
Add an optional free-text category tag to tasks. Categories are plain strings (no separate
entity required) keeping the data model simple while enabling basic organisation.

**Acceptance Criteria**:
- [x] `val category: String? = null` added to `Task` data class
- [x] `@ColumnInfo(name = "category") val category: String? = null` added to `TaskEntity`
- [x] `TaskMappers.kt` updated to pass `category` through both directions
- [x] Unit tests for mapper with a set category and with `null`

**Dependencies**: #120, #127 (v1.0)

---

### Issue #202: Create Room database migration (version 1 → 2)
**Labels**: `v2.0`, `database`, `P0-critical`
**Estimated Complexity**: Low
**Branch**: `issue-202-db-migration`
**Status**: ✅ COMPLETED

**Description**:
Create a single Room migration that adds all three new columns introduced in #199–#201.
Existing rows receive sensible defaults so no data is lost on upgrade.

**Acceptance Criteria**:
- [x] `MIGRATION_1_2` defined in `AppDatabase` companion object
- [x] `@Database(version = 2)` updated in `AppDatabase`
- [x] Migration added to `Room.databaseBuilder(…).addMigrations(MIGRATION_1_2)` in `DatabaseModule`
- [x] `exportSchema = true` (or schema exported and committed) so migration can be validated
- [x] Project builds and `./gradlew connectedAndroidTest` passes with migration

**Dependencies**: #199, #200, #201, #87, #142 (v1.0)

---

## Group 2: Repository Updates
*Requires Group 1 (migration complete, new fields available).*

---

### Issue #203: Update TaskRepository interface with filter and search methods
**Labels**: `v2.0`, `architecture`, `P0-critical`, `repository`
**Estimated Complexity**: Low
**Branch**: `issue-203-repo-filters`
**Status**: ✅ COMPLETED

**Description**:
Extend the domain repository interface with query methods that expose the new Priority and
category fields, plus full-text search across title and description.

**Acceptance Criteria**:
- [x] Add to `TaskRepository` interface in `domain.repository`:
  - `fun getCompletedTasks(): Flow<List<Task>>`
  - `fun searchTasks(query: String): Flow<List<Task>>`
  - `fun getTasksByPriority(priority: Priority): Flow<List<Task>>`
  - `fun getTasksByCategory(category: String): Flow<List<Task>>`
- [x] Documentation comments for each new method
- [x] No data-layer imports (clean domain interface)

**Dependencies**: #149 (v1.0), #199, #201

---

### Issue #204: Update TaskDao and TaskRepositoryImpl with new query implementations
**Labels**: `v2.0`, `database`, `P0-critical`, `repository`
**Estimated Complexity**: Medium
**Branch**: `issue-204-dao-queries`
**Status**: ✅ COMPLETED

**Description**:
Add corresponding DAO queries and wire them through `TaskRepositoryImpl` with proper
entity-to-domain mapping.

**Acceptance Criteria**:
- [x] Add to `TaskDao` in `data.local`: getCompletedTasks, searchTasks, getTasksByPriority, getTasksByCategory
- [x] `TaskRepositoryImpl` implements all four new methods with entity-domain mapping
- [x] Unit tests with a fake `TaskDao` covering each new method

**Dependencies**: #203, #150 (v1.0), #202

---

### Issue #205: Update AddTaskUseCase to accept priority, due date, and category
**Labels**: `v2.0`, `use-case`, `P0-critical`
**Estimated Complexity**: Low
**Branch**: `issue-205-add-task-fields`
**Status**: ✅ COMPLETED

**Description**:
Extend `AddTaskUseCase` to propagate the three new task fields introduced in v2.0.
Default values keep all existing call sites valid without modification.

**Acceptance Criteria**:
- [x] `AddTaskUseCase` operator function updated with `priority`, `dueDate`, `category` params
- [x] New parameters passed through when constructing the `Task` domain model
- [x] Existing validation unchanged (blank title → `Result.failure`)
- [x] Unit tests for default values and each explicit combination

**Dependencies**: #102 (v1.0), #199, #200, #201

---

## Group 3: New Use Cases
*Requires #203 and #204 (new repository methods available).*

---

### Issue #206: Implement GetCompletedTasksUseCase
**Labels**: `v2.0`, `use-case`, `P0-critical`
**Estimated Complexity**: Low
**Branch**: `issue-206-completed-usecase`
**Status**: ✅ COMPLETED

**Description**:
Create a use case to retrieve all completed tasks for the history screen.

**Acceptance Criteria**:
- [x] Class `GetCompletedTasksUseCase` created in `domain.usecase` package
- [x] Constructor injects `TaskRepository`
- [x] Operator function: `operator fun invoke(): Flow<List<Task>>`
- [x] Delegates to `repository.getCompletedTasks()`
- [x] Documentation explaining purpose
- [x] Unit test with mock repository

**Dependencies**: #203, #151 (v1.0)

---

### Issue #207: Implement SearchTasksUseCase
**Labels**: `v2.0`, `use-case`, `P1-high`
**Estimated Complexity**: Low
**Branch**: `issue-207-search-usecase`
**Status**: ✅ COMPLETED

**Description**:
Create a use case that wraps task search, guarding against empty queries.

**Acceptance Criteria**:
- [x] Class `SearchTasksUseCase` created in `domain.usecase` package
- [x] Constructor injects `TaskRepository`
- [x] Operator function: `operator fun invoke(query: String): Flow<List<Task>>`
- [x] Returns `flowOf(emptyList())` immediately if `query.isBlank()`
- [x] Otherwise delegates to `repository.searchTasks(query.trim())`
- [x] Unit tests: blank query returns empty list, non-blank delegates to repository

**Dependencies**: #203, #151 (v1.0)

---

### Issue #208: Implement GetTasksByPriorityUseCase
**Labels**: `v2.0`, `use-case`, `P1-high`
**Estimated Complexity**: Low
**Branch**: `issue-208-priority-usecase`
**Status**: ✅ COMPLETED

**Description**:
Create a use case to retrieve incomplete tasks filtered to a specific priority level.

**Acceptance Criteria**:
- [x] Class `GetTasksByPriorityUseCase` created in `domain.usecase` package
- [x] Constructor injects `TaskRepository`
- [x] Operator function: `operator fun invoke(priority: Priority): Flow<List<Task>>`
- [x] Delegates to `repository.getTasksByPriority(priority)`
- [x] Unit test with mock repository

**Dependencies**: #203, #151 (v1.0)

---

### Issue #209: Implement GetTasksByCategoryUseCase
**Labels**: `v2.0`, `use-case`, `P1-high`
**Estimated Complexity**: Low
**Branch**: `issue-209-category-usecase`
**Status**: ✅ COMPLETED

**Description**:
Create a use case to retrieve incomplete tasks belonging to a specific category tag.

**Acceptance Criteria**:
- [x] Class `GetTasksByCategoryUseCase` created in `domain.usecase` package
- [x] Constructor injects `TaskRepository`
- [x] Operator function: `operator fun invoke(category: String): Flow<List<Task>>`
- [x] Delegates to `repository.getTasksByCategory(category)`
- [x] Unit test with mock repository

**Dependencies**: #203, #151 (v1.0)

---

## Group 4: UI State Updates
*Requires Group 1 (updated Task domain model).*

---

### Issue #210: Update TaskUiModel with priority, due date, and category display fields
**Labels**: `v2.0`, `ui`, `P0-critical`, `model`
**Estimated Complexity**: Low
**Branch**: `issue-210-ui-model`
**Status**: ✅ COMPLETED

**Description**:
Extend `TaskUiModel` with display-ready representations of the three new task fields.

**Acceptance Criteria**:
- [x] `TaskUiModel` updated with priority, priorityLabel, dueDateLabel, isOverdue, category
- [x] `Task.toUiModel()` mapper updated to populate all new fields
- [x] Unit tests for all new fields, including overdue logic

**Dependencies**: #68 (v1.0), #199, #200, #201

---

### Issue #211: Create CompletedTasksUiState
**Labels**: `v2.0`, `ui`, `P0-critical`, `state`
**Estimated Complexity**: Low
**Branch**: `issue-211-completed-state`
**Status**: ✅ COMPLETED

**Description**:
Define the UI state for the completed tasks history screen.

**Acceptance Criteria**:
- [x] File created: `CompletedTasksUiState.kt` in `ui.screens.completedtasks` package
- [x] Data class with tasks, isLoading, error fields
- [x] Documentation comments

**Dependencies**: #210

---

### Issue #212: Create SettingsUiState
**Labels**: `v2.0`, `ui`, `P1-high`, `state`
**Estimated Complexity**: Low
**Branch**: `issue-212-settings-state`
**Status**: ✅ COMPLETED

**Description**:
Define the UI state for the settings screen, including theme preference and default sort order.

**Acceptance Criteria**:
- [x] File created: `SettingsUiState.kt` in `ui.screens.settings` package
- [x] Supporting enums created (AppTheme, SortOrder)
- [x] Data class with theme and defaultSortOrder fields
- [x] Documentation comments

**Dependencies**: None

---

### Issue #213: Update TaskListUiState with search, filter, and sort fields
**Labels**: `v2.0`, `ui`, `P0-critical`, `state`
**Estimated Complexity**: Low
**Branch**: `issue-213-list-state`
**Status**: ✅ COMPLETED

**Description**:
Extend `TaskListUiState` to carry the active search query, filter selections, sort order,
and the list of distinct categories derived from current tasks.

**Acceptance Criteria**:
- [x] `TaskListUiState` updated with searchQuery, filterPriority, filterCategory, sortOrder, availableCategories
- [x] Existing fields unchanged
- [x] Documentation comments for each new property

**Dependencies**: #63 (v1.0), #210, #212

---

## Group 5: ViewModel Updates
*Requires Groups 3 and 4.*

---

### Issue #214: Update TaskListViewModel with search, filter, and sort
**Labels**: `v2.0`, `ui`, `P0-critical`, `viewmodel`
**Estimated Complexity**: High
**Branch**: `issue-214-list-viewmodel`
**Status**: ✅ COMPLETED

**Description**:
Extend `TaskListViewModel` to orchestrate search, filtering by priority and category,
and in-memory sorting.

**Acceptance Criteria**:
- [x] `SearchTasksUseCase`, `GetTasksByPriorityUseCase`, `GetTasksByCategoryUseCase` injected
- [x] New functions: updateSearchQuery, setFilterPriority, setFilterCategory, setSortOrder
- [x] `addTask` updated to accept `priority`, `dueDate`, `category`
- [x] `availableCategories` derived reactively from all tasks
- [x] Filtering and sorting applied in-memory
- [x] Unit tests for each new function

**Dependencies**: #97 (v1.0), #205, #207, #208, #209, #213

---

### Issue #215: Implement CompletedTasksViewModel
**Labels**: `v2.0`, `ui`, `P0-critical`, `viewmodel`
**Estimated Complexity**: Low
**Branch**: `issue-215-completed-vm`
**Status**: ✅ COMPLETED

**Description**:
Create the ViewModel for the completed tasks history screen.

**Acceptance Criteria**:
- [x] Class `CompletedTasksViewModel` created in `ui.screens.completedtasks` package
- [x] Annotated with `@HiltViewModel`
- [x] Constructor injects `GetCompletedTasksUseCase`, `DeleteTaskUseCase`
- [x] Exposes `StateFlow<CompletedTasksUiState>`
- [x] Functions: `deleteTask(task: Task)`
- [x] Collects completed tasks into state on init
- [x] Unit tests

**Dependencies**: #211, #206, #118 (v1.0)

---

### Issue #216: Implement SettingsViewModel
**Labels**: `v2.0`, `ui`, `P1-high`, `viewmodel`
**Estimated Complexity**: Medium
**Branch**: `issue-216-settings-vm`
**Status**: ✅ COMPLETED

**Description**:
Create the ViewModel for the settings screen with DataStore persistence.

**Acceptance Criteria**:
- [x] `androidx.datastore:datastore-preferences` dependency added
- [x] `DataStore<Preferences>` provided via Hilt module
- [x] Class `SettingsViewModel` created in `ui.screens.settings` package
- [x] Annotated with `@HiltViewModel`
- [x] Exposes `StateFlow<SettingsUiState>`
- [x] Functions: `setTheme()`, `setSortOrder()`
- [x] Each function writes to DataStore and state updates reactively
- [x] Unit tests

**Dependencies**: #212

---

## Group 6: Navigation Routes
*Requires #122 (v1.0 — existing Screen sealed class).*

---

### Issue #217: Add CompletedTasks and Settings navigation routes
**Labels**: `v2.0`, `ui`, `navigation`, `P0-critical`
**Estimated Complexity**: Low
**Branch**: `issue-217-nav-routes`
**Status**: ✅ COMPLETED

**Acceptance Criteria**:
- [x] `Screen.CompletedTasks` route added
- [x] `Screen.Settings` route added
- [x] Documentation comments
- [x] Project builds successfully

**Dependencies**: #122 (v1.0)

---

## Group 7: UI Components
*Requires Group 4 (updated models with Priority and category). Can be done alongside Group 5.*

---

### Issue #218: Implement PriorityBadge composable
**Labels**: `v2.0`, `ui`, `P0-critical`, `component`
**Estimated Complexity**: Low
**Branch**: `issue-218-priority-badge`
**Status**: ✅ COMPLETED

**Description**:
Create a small reusable composable that displays a colour-coded badge for a task's priority.

**Acceptance Criteria**:
- [x] Composable `PriorityBadge` created in `ui.components` package
- [x] Distinct Material3 colours per level (LOW/MEDIUM/HIGH)
- [x] Displays the priority label text inside the badge
- [x] Preview annotation
- [x] No business logic — purely presentational

**Dependencies**: #199, #210

---

### Issue #219: Implement EditTaskDialog composable
**Labels**: `v2.0`, `ui`, `P0-critical`, `component`
**Estimated Complexity**: Medium
**Branch**: `issue-219-edit-dialog`
**Status**: ✅ COMPLETED

**Description**:
Create a full-featured edit dialog that pre-populates all task fields and supports the
new v2.0 fields (priority, due date, category).

**Acceptance Criteria**:
- [x] Composable `EditTaskDialog` created in `ui.screens.tasklist` package
- [x] Parameters include `task: TaskUiModel?` (null = add mode, non-null = edit mode)
- [x] Fields: Title, Description, Priority selector, Due date picker, Category
- [x] Confirm button disabled when title is blank
- [x] Material3 `AlertDialog` styling
- [x] Preview annotations

**Dependencies**: #85 (v1.0), #199, #200, #201, #210

---

### Issue #220: Implement TaskFilterBar composable
**Labels**: `v2.0`, `ui`, `P1-high`, `component`
**Estimated Complexity**: Medium
**Branch**: `issue-220-filter-bar`
**Status**: ✅ COMPLETED

**Description**:
Create a composable filter/search bar for TaskListScreen.

**Acceptance Criteria**:
- [x] Composable `TaskFilterBar` created in `ui.screens.tasklist` package
- [x] Search field, priority filter chips, category dropdown, sort order dropdown
- [x] Callbacks for all filter/sort changes
- [x] Material3 styling
- [x] Preview annotation

**Dependencies**: #199, #201, #212, #213

---

### Issue #221: Implement DueDatePickerDialog composable
**Labels**: `v2.0`, `ui`, `P1-high`, `component`
**Estimated Complexity**: Low
**Branch**: `issue-221-date-picker`
**Status**: ✅ COMPLETED

**Description**:
Wrap Material3's `DatePicker` in a dialog composable that returns a `LocalDate` on confirm.

**Acceptance Criteria**:
- [x] Composable `DueDatePickerDialog` created in `ui.components` package
- [x] Parameters: `initialDate`, `onConfirm`, `onDismiss`
- [x] Uses Material3 `DatePicker` / `DatePickerDialog`
- [x] "Clear" button sets date to null
- [x] Preview annotation

**Dependencies**: #200

---

## Group 8: Screen Updates
*Requires ViewModel updates (Group 5) and UI components (Group 7).*

---

### Issue #222: Update TaskListScreen with search bar, filter bar, and full edit support
**Labels**: `v2.0`, `ui`, `P0-critical`, `screen`
**Estimated Complexity**: High
**Branch**: `issue-222-list-screen`
**Status**: ✅ COMPLETED

**Description**:
Integrate the new `TaskFilterBar` and `EditTaskDialog` into `TaskListScreen`.

**Acceptance Criteria**:
- [x] `TaskFilterBar` rendered below the `TopAppBar`
- [x] Search, priority filter, category filter, and sort order wired to ViewModel
- [x] FAB opens `EditTaskDialog` in add mode
- [x] Edit icon on `TaskListItem` opens `EditTaskDialog` in edit mode
- [x] "View Completed" and Settings icons in TopAppBar
- [x] `PriorityBadge`, due date label, category label displayed in TaskListItem

**Dependencies**: #108 (v1.0), #214, #218, #219, #220

---

### Issue #223: Implement CompletedTasksScreen composable
**Labels**: `v2.0`, `ui`, `P0-critical`, `screen`
**Estimated Complexity**: Medium
**Branch**: `issue-223-completed-screen`
**Status**: ✅ COMPLETED

**Description**:
Create a dedicated screen that shows the user's task completion history.

**Acceptance Criteria**:
- [x] Composable `CompletedTasksScreen` created in `ui.screens.completedtasks` package
- [x] Collects state from `CompletedTasksViewModel`
- [x] Displays completed tasks in a `LazyColumn`
- [x] Swipe-to-delete gesture
- [x] Empty state
- [x] Back navigation
- [x] Material3 `Scaffold` with `TopAppBar`

**Dependencies**: #215, #218

---

### Issue #224: Implement SettingsScreen composable
**Labels**: `v2.0`, `ui`, `P1-high`, `screen`
**Estimated Complexity**: Low
**Branch**: `issue-224-settings-screen`
**Status**: ✅ COMPLETED

**Description**:
Create a settings screen for theme and sort order preferences.

**Acceptance Criteria**:
- [x] Composable `SettingsScreen` created in `ui.screens.settings` package
- [x] Collects state from `SettingsViewModel`
- [x] Theme section with theme variant selector
- [x] Default sort order section
- [x] About section with app version
- [x] Back navigation
- [x] Material3 `Scaffold` with `TopAppBar`

**Dependencies**: #216

---

## Group 9: Navigation Wiring
*Requires #217 (new routes) and screens from Group 8.*

---

### Issue #225: Wire CompletedTasksScreen into navigation
**Labels**: `v2.0`, `ui`, `navigation`, `P0-critical`
**Estimated Complexity**: Low
**Branch**: `issue-225-completed-nav`
**Status**: ✅ COMPLETED

**Acceptance Criteria**:
- [x] `CompletedTasksScreen` registered in `AppNavGraph`
- [x] Navigation from `TaskListScreen` works
- [x] Back navigation returns to `TaskListScreen`
- [x] Integration tested

**Dependencies**: #217, #223, #128 (v1.0)

---

### Issue #226: Wire SettingsScreen into navigation
**Labels**: `v2.0`, `ui`, `navigation`, `P1-high`
**Estimated Complexity**: Low
**Branch**: `issue-226-settings-nav`
**Status**: ✅ COMPLETED

**Acceptance Criteria**:
- [x] `SettingsScreen` registered in `AppNavGraph`
- [x] Settings icon navigation works
- [x] Back navigation returns to previous screen
- [x] Integration tested

**Dependencies**: #217, #224, #128 (v1.0)

---

## Group 10: Feature Completion
*Requires screens and navigation wired (Groups 8–9). Final group.*

---

### Issue #227: Swipe-to-delete gesture on TaskListItem
**Labels**: `v2.0`, `ui`, `feature`, `P1-high`
**Estimated Complexity**: Low
**Branch**: `issue-227-swipe-delete`
**Status**: ✅ COMPLETED

**Description**:
Add a swipe-left-to-delete gesture using Material3's `SwipeToDismissBox`.

**Acceptance Criteria**:
- [x] `SwipeToDismissBox` wraps `TaskListItem` in both screens
- [x] Swipe left reveals red background with delete icon
- [x] On full swipe, ViewModel `deleteTask` function is called
- [x] Item animates out smoothly
- [x] Existing delete button still works

**Dependencies**: #222, #223

---

### Issue #228: Undo task deletion with Snackbar
**Labels**: `v2.0`, `ui`, `feature`, `P1-high`
**Estimated Complexity**: Medium
**Branch**: `issue-228-undo-delete`
**Status**: ✅ COMPLETED

**Description**:
Show a Snackbar with "Undo" action after task deletion.

**Acceptance Criteria**:
- [x] After deletion, `Snackbar` appears with "Undo" action
- [x] Tapping "Undo" restores the task
- [x] Snackbar auto-dismisses; if dismissed without undo, deletion is final
- [x] Works in both `TaskListScreen` and `CompletedTasksScreen`
- [x] Only one pending undo at a time

**Dependencies**: #227, #102 (v1.0)

---

### Issue #229: Sort tasks by priority, due date, title, or creation date
**Labels**: `v2.0`, `ui`, `feature`, `P0-critical`
**Estimated Complexity**: Low
**Branch**: `issue-229-sort-tasks`
**Status**: ✅ COMPLETED

**Description**:
Implement in-memory task sorting using the `SortOrder` enum.

**Acceptance Criteria**:
- [x] `TaskListViewModel` applies `sortOrder` to the task list after filtering
- [x] All sort orders implemented (CREATED_DATE_DESC, DUE_DATE_ASC, PRIORITY_DESC, TITLE_ASC)
- [x] `setSortOrder()` function updates state reactively
- [x] Sort order persisted via SettingsViewModel DataStore
- [x] Unit tests for each sort order

**Dependencies**: #214, #216

---

### Issue #230: Theme toggle (light / dark / system) in Settings
**Labels**: `v2.0`, `ui`, `feature`, `P1-high`
**Estimated Complexity**: Medium
**Branch**: `issue-230-theme-toggle`
**Status**: ✅ COMPLETED

**Description**:
Apply the user's chosen theme from `SettingsViewModel` to `MaterialTheme`.

**Acceptance Criteria**:
- [x] `MainActivity` collects theme state
- [x] Theme applied via three-theme system (Obsidian, Neo Brutalist, Vapor)
- [x] Theme switches immediately when changed in Settings
- [x] Selection persists across app restarts via DataStore

**Dependencies**: #216, #224

---

### Issue #231: Implement GetWeightedRandomTaskUseCase
**Labels**: `v2.0`, `use-case`, `feature`, `P2-medium`, `core-feature`
**Estimated Complexity**: Low
**Branch**: `issue-231-weighted-random`
**Status**: ✅ COMPLETED

**Description**:
Add a priority-weighted variant of the random task selection.

**Acceptance Criteria**:
- [x] Class `GetWeightedRandomTaskUseCase` created in `domain.usecase` package
- [x] Constructor injects `TaskRepository`
- [x] Weighted logic: HIGH 3×, MEDIUM 2×, LOW 1×
- [x] `RandomTaskViewModel` gains `useWeightedRandom` toggle
- [x] `RandomTaskScreen` shows toggle switch
- [x] Unit tests for weighted distribution and edge cases

**Dependencies**: #132 (v1.0), #199, #203
