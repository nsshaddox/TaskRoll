# Random Task App - GitHub Issues

**Total Issues**: 105
**Phases**: 11 (0-10 + Launch)
**Estimated Timeline**: 12-15 weeks

---

## Phase 0: Project Foundation & Setup

### Issue #1: Setup GitHub repository with branch protection
**Labels**: `setup`, `P0-critical`
**Estimated Complexity**: Low

**Description**:
Configure the GitHub repository with proper branch protection rules to ensure code quality and prevent accidental force pushes.

**Acceptance Criteria**:
- [ ] Repository exists on GitHub
- [ ] Branch protection enabled on `main` branch
- [ ] Require at least 1 PR review before merging
- [ ] Require status checks to pass before merging
- [ ] Disable force pushes to `main`
- [ ] Enable linear history (prevent merge commits)

**Dependencies**: None

---

### Issue #2: Create bug report issue template
**Labels**: `setup`, `documentation`, `P1-high`
**Estimated Complexity**: Low

**Description**:
Create a standardized bug report template to help users provide necessary information when reporting bugs.

**Acceptance Criteria**:
- [ ] File created at `.github/ISSUE_TEMPLATE/bug_report.md`
- [ ] Template includes sections for:
  - Bug description
  - Steps to reproduce
  - Expected behavior
  - Actual behavior
  - Device/OS information
  - App version
  - Screenshots (optional)
- [ ] Template is available when creating new issues

**Dependencies**: #1

---

### Issue #3: Create feature request issue template
**Labels**: `setup`, `documentation`, `P1-high`
**Estimated Complexity**: Low

**Description**:
Create a standardized feature request template to help users suggest new features with proper context.

**Acceptance Criteria**:
- [ ] File created at `.github/ISSUE_TEMPLATE/feature_request.md`
- [ ] Template includes sections for:
  - Feature description
  - Use case/problem it solves
  - Proposed solution
  - Alternative solutions considered
  - Additional context
- [ ] Template is available when creating new issues

**Dependencies**: #1

---

### Issue #4: Setup CI/CD with GitHub Actions
**Labels**: `setup`, `ci-cd`, `P0-critical`
**Estimated Complexity**: Medium

**Description**:
Create a GitHub Actions workflow to automatically build, test, and validate pull requests and commits to main branch.

**Acceptance Criteria**:
- [ ] Workflow file created at `.github/workflows/android.yml`
- [ ] Workflow runs on PR creation and updates
- [ ] Workflow runs on push to `main` branch
- [ ] Steps include:
  - Checkout code
  - Set up JDK 17
  - Cache Gradle dependencies
  - Run `./gradlew build`
  - Run `./gradlew test`
  - Run lint checks
  - Upload test results as artifacts
- [ ] Workflow completes successfully on sample PR

**Dependencies**: #1

---

### Issue #5: Configure ktlint for code formatting
**Labels**: `setup`, `code-quality`, `P1-high`
**Estimated Complexity**: Medium

**Description**:
Add ktlint for consistent Kotlin code formatting across the project.

**Acceptance Criteria**:
- [ ] ktlint Gradle plugin added to `build.gradle.kts`
- [ ] Configuration file `.editorconfig` created with project standards
- [ ] Gradle task `./gradlew ktlintCheck` runs successfully
- [ ] Gradle task `./gradlew ktlintFormat` auto-fixes formatting issues
- [ ] ktlint integrated into CI/CD pipeline
- [ ] Existing code passes ktlint checks

**Dependencies**: #4

---

### Issue #6: Configure detekt for static analysis
**Labels**: `setup`, `code-quality`, `P1-high`
**Estimated Complexity**: Medium

**Description**:
Add detekt for static code analysis to catch potential bugs and code smells.

**Acceptance Criteria**:
- [ ] detekt Gradle plugin added to `build.gradle.kts`
- [ ] Configuration file `detekt.yml` created with project rules
- [ ] Gradle task `./gradlew detekt` runs successfully
- [ ] detekt integrated into CI/CD pipeline
- [ ] Baseline file generated for existing code if needed
- [ ] Critical issues resolved

**Dependencies**: #4

---

### Issue #7: Enhance .gitignore file
**Labels**: `setup`, `P1-high`
**Estimated Complexity**: Low

**Description**:
Update `.gitignore` with comprehensive exclusions for Android development.

**Acceptance Criteria**:
- [ ] Android build outputs excluded (`/build`, `/app/build`, `*.apk`, `*.aab`)
- [ ] IDE files excluded (`.idea/`, `*.iml`, `.DS_Store`, `*.swp`)
- [ ] Gradle cache excluded (`.gradle/`, `/local.properties`)
- [ ] Sensitive files excluded (`keystore.jks`, `*.keystore`, `google-services.json`, `.env`)
- [ ] Generated files excluded (`*.hprof`, `captures/`)
- [ ] No tracked files should be in `.gitignore` list
- [ ] Verify with `git status` that unwanted files are ignored

**Dependencies**: None

---

### Issue #8: Create comprehensive README.md
**Labels**: `documentation`, `P0-critical`
**Estimated Complexity**: Medium

**Description**:
Write a detailed README that serves as the entry point for understanding and contributing to the project.

**Acceptance Criteria**:
- [ ] Project name and description
- [ ] Features list (MVP features)
- [ ] Screenshots section (placeholder for now)
- [ ] Tech stack overview
- [ ] Requirements (Android Studio version, SDK requirements)
- [ ] Build instructions (clone, build, run)
- [ ] Testing instructions
- [ ] Contributing link (to CONTRIBUTING.md)
- [ ] License information
- [ ] Acknowledgments/Credits
- [ ] Professional formatting with proper headings and badges

**Dependencies**: #1

---

### Issue #9: Create CONTRIBUTING.md documentation
**Labels**: `documentation`, `P1-high`
**Estimated Complexity**: Medium

**Description**:
Provide clear guidelines for contributors on how to participate in the project.

**Acceptance Criteria**:
- [ ] Development environment setup instructions
- [ ] Code style guidelines (reference to ktlint/detekt)
- [ ] Branch naming conventions (e.g., `feature/`, `bugfix/`, `docs/`)
- [ ] Commit message format (Conventional Commits preferred)
- [ ] PR process and checklist
- [ ] Testing requirements (unit tests for new features)
- [ ] Code review expectations
- [ ] How to report bugs and request features (link to templates)
- [ ] Code of Conduct reference

**Dependencies**: #8

---

### Issue #10: Create ARCHITECTURE.md documentation
**Labels**: `documentation`, `architecture`, `P1-high`
**Estimated Complexity**: High

**Description**:
Document the architectural decisions and patterns used in the project to help developers understand the codebase structure.

**Acceptance Criteria**:
- [ ] Overview of MVVM pattern and why it was chosen
- [ ] Layer separation explanation:
  - Data layer (Room, repositories)
  - Domain layer (models, use cases)
  - UI layer (Compose, ViewModels)
- [ ] Dependency flow diagram (data → domain ← ui)
- [ ] Package structure with explanations
- [ ] Key libraries and their purposes:
  - Jetpack Compose
  - Room Database
  - Hilt (DI)
  - Kotlin Coroutines
  - Navigation Compose
- [ ] Data flow examples (how a user action becomes a database update)
- [ ] Testing strategy overview
- [ ] Diagrams created using Mermaid or similar

**Dependencies**: #8

---

### Issue #11: Create CODE_OF_CONDUCT.md
**Labels**: `documentation`, `P2-medium`
**Estimated Complexity**: Low

**Description**:
Establish community standards and expected behavior for contributors.

**Acceptance Criteria**:
- [ ] File created at root: `CODE_OF_CONDUCT.md`
- [ ] Use Contributor Covenant template or similar
- [ ] Define expected behaviors
- [ ] Define unacceptable behaviors
- [ ] Enforcement procedures
- [ ] Contact information for reporting issues
- [ ] Referenced in CONTRIBUTING.md

**Dependencies**: #8

---

### Issue #12: Setup dependency management with version catalog
**Labels**: `setup`, `P2-medium`
**Estimated Complexity**: Medium

**Description**:
Centralize dependency version management using Gradle version catalogs for easier updates and consistency.

**Acceptance Criteria**:
- [ ] File created: `gradle/libs.versions.toml`
- [ ] All dependency versions defined in `[versions]` section
- [ ] All libraries defined in `[libraries]` section
- [ ] All plugins defined in `[plugins]` section
- [ ] `build.gradle.kts` files updated to use version catalog
- [ ] Project builds successfully with new configuration
- [ ] Documentation added to README or CONTRIBUTING on how to add dependencies

**Dependencies**: None

---

## Phase 1: Architecture Foundation

### Issue #13: Setup MVVM package structure
**Labels**: `architecture`, `P0-critical`
**Estimated Complexity**: Low

**Description**:
Create the foundational package structure following MVVM clean architecture principles.

**Acceptance Criteria**:
- [ ] Package created: `com.nshaddox.randomtask.data.local` (Room entities, DAOs)
- [ ] Package created: `com.nshaddox.randomtask.data.repository` (Repository implementations)
- [ ] Package created: `com.nshaddox.randomtask.domain.model` (Domain models)
- [ ] Package created: `com.nshaddox.randomtask.domain.usecase` (Business logic use cases)
- [ ] Package created: `com.nshaddox.randomtask.domain.repository` (Repository interfaces)
- [ ] Package created: `com.nshaddox.randomtask.ui.screens.tasklist` (Task list screen)
- [ ] Package created: `com.nshaddox.randomtask.ui.screens.randomtask` (Random task screen)
- [ ] Package created: `com.nshaddox.randomtask.di` (Hilt modules)
- [ ] Each package contains a `package-info.kt` or placeholder file
- [ ] Package structure documented in ARCHITECTURE.md

**Dependencies**: #1

---

### Issue #14: Setup Hilt dependency injection
**Labels**: `architecture`, `dependency-injection`, `P0-critical`
**Estimated Complexity**: Medium

**Description**:
Configure Hilt for dependency injection to enable testable, modular architecture.

**Acceptance Criteria**:
- [ ] Hilt dependencies added to `build.gradle.kts`:
  - `com.google.dagger:hilt-android`
  - `com.google.dagger:hilt-android-compiler`
  - `androidx.hilt:hilt-navigation-compose`
- [ ] Hilt Gradle plugin applied
- [ ] Application class created: `RandomTaskApplication`
- [ ] Application class annotated with `@HiltAndroidApp`
- [ ] `AndroidManifest.xml` updated with application name
- [ ] `MainActivity` annotated with `@AndroidEntryPoint`
- [ ] Project builds successfully with Hilt
- [ ] Sample injection verified (e.g., inject a simple class)

**Dependencies**: #13

---

### Issue #15: Setup Room Database foundation
**Labels**: `architecture`, `database`, `P0-critical`
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

**Dependencies**: #14

---

### Issue #16: Configure Kotlin Coroutines
**Labels**: `architecture`, `P1-high`
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

**Dependencies**: #14

---

### Issue #17: Create Task entity for Room
**Labels**: `database`, `feature`, `P0-critical`
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

**Dependencies**: #15

---

### Issue #18: Create Task domain model
**Labels**: `architecture`, `feature`, `P0-critical`
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

**Dependencies**: #17

---

### Issue #19: Create data-domain mappers
**Labels**: `architecture`, `P0-critical`
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

**Dependencies**: #17, #18

---

### Issue #20: Create TaskDao interface
**Labels**: `database`, `feature`, `P0-critical`
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

**Dependencies**: #17

---

### Issue #21: Wire TaskDao into AppDatabase
**Labels**: `database`, `P0-critical`
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

**Dependencies**: #20

---

### Issue #22: Create TaskRepository interface
**Labels**: `architecture`, `repository`, `P0-critical`
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

**Dependencies**: #18

---

### Issue #23: Implement TaskRepositoryImpl
**Labels**: `repository`, `feature`, `P0-critical`
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

**Dependencies**: #21, #22, #19

---

### Issue #24: Create Hilt module for repository
**Labels**: `dependency-injection`, `P0-critical`
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

**Dependencies**: #23

---

### Issue #25: Setup testing infrastructure
**Labels**: `testing`, `setup`, `P1-high`
**Estimated Complexity**: Medium

**Description**:
Configure testing dependencies and infrastructure for unit and instrumented tests.

**Acceptance Criteria**:
- [ ] Test dependencies added to `build.gradle.kts`:
  - JUnit 4
  - Mockito or MockK
  - Kotlin Coroutines Test
  - Turbine (Flow testing)
  - AndroidX Test Core
  - Room Testing
  - Hilt Testing
- [ ] Test directories verified: `app/src/test/`, `app/src/androidTest/`
- [ ] Sample unit test created and passes
- [ ] Sample instrumented test created and passes
- [ ] CI/CD runs tests successfully

**Dependencies**: #4

---

### Issue #26: Write TaskDao tests
**Labels**: `testing`, `database`, `P1-high`
**Estimated Complexity**: Medium

**Description**:
Create instrumented tests for TaskDao database operations.

**Acceptance Criteria**:
- [ ] Test class `TaskDaoTest` created in `androidTest`
- [ ] Use in-memory database for testing
- [ ] Tests for all DAO methods:
  - Insert task and verify retrieval
  - Update task and verify changes
  - Delete task and verify removal
  - Get all tasks returns correct order
  - Get incomplete tasks filters completed tasks
  - Get task by ID returns correct task
- [ ] Tests use Flow collection with `first()` or Turbine
- [ ] All tests pass

**Dependencies**: #21, #25

---

### Issue #27: Write TaskRepository tests
**Labels**: `testing`, `repository`, `P1-high`
**Estimated Complexity**: Medium

**Description**:
Create unit tests for TaskRepository with mocked TaskDao.

**Acceptance Criteria**:
- [ ] Test class `TaskRepositoryImplTest` created in `test`
- [ ] Mock or fake TaskDao
- [ ] Tests for all repository methods:
  - Verify entity-domain mapping
  - Verify error handling (Result.failure)
  - Verify successful operations (Result.success)
  - Verify Flow transformations
- [ ] Test timestamp updates on add/update
- [ ] All tests pass
- [ ] Coverage > 80% for repository class

**Dependencies**: #23, #25

---

## Phase 2: Domain Layer - Use Cases

### Issue #28: Implement GetAllTasksUseCase
**Labels**: `feature`, `use-case`, `P0-critical`
**Estimated Complexity**: Low

**Description**:
Create use case to retrieve all tasks from the repository.

**Acceptance Criteria**:
- [ ] Class `GetAllTasksUseCase` created in `domain.usecase` package
- [ ] Constructor injects `TaskRepository`
- [ ] Operator function: `operator fun invoke(): Flow<List<Task>>`
- [ ] Simply delegates to `repository.getAllTasks()`
- [ ] Documentation explaining purpose
- [ ] Unit test with mock repository

**Dependencies**: #24

---

### Issue #29: Implement GetIncompleteTasksUseCase
**Labels**: `feature`, `use-case`, `P0-critical`
**Estimated Complexity**: Low

**Description**:
Create use case to retrieve only incomplete (not done) tasks.

**Acceptance Criteria**:
- [ ] Class `GetIncompleteTasksUseCase` created in `domain.usecase` package
- [ ] Constructor injects `TaskRepository`
- [ ] Operator function: `operator fun invoke(): Flow<List<Task>>`
- [ ] Delegates to `repository.getIncompleteTasks()`
- [ ] Documentation explaining purpose
- [ ] Unit test with mock repository

**Dependencies**: #24

---

### Issue #30: Implement AddTaskUseCase with validation
**Labels**: `feature`, `use-case`, `P0-critical`
**Estimated Complexity**: Medium

**Description**:
Create use case to add a new task with input validation.

**Acceptance Criteria**:
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

**Dependencies**: #24

---

### Issue #31: Implement UpdateTaskUseCase
**Labels**: `feature`, `use-case`, `P1-high`
**Estimated Complexity**: Low

**Description**:
Create use case to update an existing task.

**Acceptance Criteria**:
- [ ] Class `UpdateTaskUseCase` created in `domain.usecase` package
- [ ] Constructor injects `TaskRepository`
- [ ] Operator function: `suspend operator fun invoke(task: Task): Result<Unit>`
- [ ] Updates `updatedAt` timestamp
- [ ] Delegates to `repository.updateTask()`
- [ ] Unit test with mock repository

**Dependencies**: #24

---

### Issue #32: Implement DeleteTaskUseCase
**Labels**: `feature`, `use-case`, `P0-critical`
**Estimated Complexity**: Low

**Description**:
Create use case to delete a task.

**Acceptance Criteria**:
- [ ] Class `DeleteTaskUseCase` created in `domain.usecase` package
- [ ] Constructor injects `TaskRepository`
- [ ] Operator function: `suspend operator fun invoke(task: Task): Result<Unit>`
- [ ] Delegates to `repository.deleteTask()`
- [ ] Unit test with mock repository

**Dependencies**: #24

---

### Issue #33: Implement CompleteTaskUseCase
**Labels**: `feature`, `use-case`, `P0-critical`
**Estimated Complexity**: Low

**Description**:
Create use case to mark a task as completed.

**Acceptance Criteria**:
- [ ] Class `CompleteTaskUseCase` created in `domain.usecase` package
- [ ] Constructor injects `TaskRepository`
- [ ] Operator function: `suspend operator fun invoke(task: Task, isCompleted: Boolean): Result<Unit>`
- [ ] Updates task with new completion status and timestamp
- [ ] Delegates to `repository.updateTask()`
- [ ] Unit test verifying task update

**Dependencies**: #24

---

### Issue #34: Implement GetRandomTaskUseCase (CORE FEATURE)
**Labels**: `feature`, `use-case`, `P0-critical`, `core-feature`
**Estimated Complexity**: Low

**Description**:
**This is the core feature of the app**: Create use case to retrieve a random task from incomplete tasks.

**Acceptance Criteria**:
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

**Dependencies**: #24

---

### Issue #35: Write comprehensive use case tests
**Labels**: `testing`, `use-case`, `P1-high`
**Estimated Complexity**: Medium

**Description**:
Ensure all use cases have thorough unit test coverage.

**Acceptance Criteria**:
- [ ] Test suite for each use case (7 total)
- [ ] Tests cover:
  - Success paths
  - Error handling
  - Edge cases (empty lists, null values, validation failures)
  - Flow emissions for Flow-based use cases
- [ ] Mock repository used in all tests
- [ ] All tests pass
- [ ] Coverage > 85% for use case package

**Dependencies**: #28-#34

---

## Phase 3: UI Foundation - Navigation & Theme

### Issue #36: Add Navigation Compose dependency
**Labels**: `setup`, `ui`, `P0-critical`
**Estimated Complexity**: Low

**Description**:
Add Jetpack Compose Navigation library for screen navigation.

**Acceptance Criteria**:
- [ ] Dependency added: `androidx.navigation:navigation-compose`
- [ ] Dependency added: `androidx.hilt:hilt-navigation-compose`
- [ ] Project builds successfully
- [ ] Navigation imports work in Kotlin files

**Dependencies**: #14

---

### Issue #37: Define navigation routes
**Labels**: `ui`, `navigation`, `P0-critical`
**Estimated Complexity**: Low

**Description**:
Create a sealed class or object to define app navigation routes.

**Acceptance Criteria**:
- [ ] File created: `NavRoutes.kt` in `ui.navigation` package (create package if needed)
- [ ] Sealed class or object defining routes:
  ```kotlin
  sealed class Screen(val route: String) {
      object TaskList : Screen("task_list")
      object RandomTask : Screen("random_task")
  }
  ```
- [ ] Documentation comments for each route
- [ ] Type-safe navigation setup

**Dependencies**: #36

---

### Issue #38: Implement NavGraph
**Labels**: `ui`, `navigation`, `P0-critical`
**Estimated Complexity**: Medium

**Description**:
Create the navigation graph that defines screen transitions.

**Acceptance Criteria**:
- [ ] File created: `NavGraph.kt` in `ui.navigation` package
- [ ] Composable function: `NavGraph(navController: NavHostController)`
- [ ] NavHost with startDestination = TaskList
- [ ] Composable routes defined:
  - `composable(Screen.TaskList.route) { TaskListScreen(navController) }`
  - `composable(Screen.RandomTask.route) { RandomTaskScreen(navController) }`
- [ ] ViewModels obtained using `hiltViewModel()`
- [ ] Documentation comments

**Dependencies**: #37

---

### Issue #39: Enhance Material3 theme
**Labels**: `ui`, `theme`, `P1-high`
**Estimated Complexity**: Medium

**Description**:
Customize the Material3 theme with brand colors and typography.

**Acceptance Criteria**:
- [ ] Update `Color.kt` with custom color palette:
  - Primary color
  - Secondary color
  - Tertiary color
  - Background colors
  - Surface colors
  - Error color
- [ ] Update `Theme.kt` with proper light/dark color schemes
- [ ] Update `Type.kt` with custom typography if needed
- [ ] Theme applied correctly in `MainActivity`
- [ ] App displays with new theme colors

**Dependencies**: None

---

### Issue #40: Create reusable UI component library
**Labels**: `ui`, `components`, `P2-medium`
**Estimated Complexity**: Medium

**Description**:
Create a library of reusable Compose components to ensure UI consistency.

**Acceptance Criteria**:
- [ ] Package created: `ui.components`
- [ ] Components created:
  - `TaskCard` - Displays a task item
  - `EmptyState` - Shown when no data available
  - `LoadingIndicator` - Shows loading state
  - `ConfirmationDialog` - Reusable alert dialog
  - `PrimaryButton` - Styled button component
- [ ] Each component well-documented
- [ ] Preview functions for each component
- [ ] Components use theme colors and typography

**Dependencies**: #39

---

### Issue #41: Create dimensions and spacing system
**Labels**: `ui`, `theme`, `P2-medium`
**Estimated Complexity**: Low

**Description**:
Define consistent spacing and dimension values for the UI.

**Acceptance Criteria**:
- [ ] File created: `Dimensions.kt` in `ui.theme` package
- [ ] Object defining spacing values:
  ```kotlin
  object Spacing {
      val extraSmall = 4.dp
      val small = 8.dp
      val medium = 16.dp
      val large = 24.dp
      val extraLarge = 32.dp
  }
  ```
- [ ] Object defining common sizes (icon sizes, button heights, etc.)
- [ ] Used consistently across UI components

**Dependencies**: #39

---

### Issue #42: Design app icon and splash screen
**Labels**: `ui`, `assets`, `P2-medium`
**Estimated Complexity**: Medium

**Description**:
Create a custom app icon and splash screen for the app.

**Acceptance Criteria**:
- [ ] App icon designed (adaptive icon with foreground and background layers)
- [ ] Icon assets generated for all densities (mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi)
- [ ] Assets placed in appropriate `res/mipmap-*` directories
- [ ] Splash screen configured using AndroidX SplashScreen API
- [ ] `ic_launcher` and `ic_launcher_round` updated
- [ ] Icon displays correctly on device/emulator
- [ ] Splash screen shows briefly on app launch

**Dependencies**: None

---

## Phase 4: Task List Screen

### Issue #43: Create TaskListUiState
**Labels**: `ui`, `state`, `P0-critical`
**Estimated Complexity**: Low

**Description**:
Define the UI state data class for the task list screen.

**Acceptance Criteria**:
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

**Dependencies**: #18

---

### Issue #44: Create TaskUiModel
**Labels**: `ui`, `model`, `P1-high`
**Estimated Complexity**: Low

**Description**:
Create a UI-specific model for displaying tasks if needed (e.g., with formatted dates).

**Acceptance Criteria**:
- [ ] File created: `TaskUiModel.kt` in `ui.screens.tasklist` package
- [ ] Data class or mapping function to format domain Task for UI
- [ ] Format timestamps to readable strings
- [ ] Any other UI-specific transformations
- [ ] Mapper function: `Task.toUiModel(): TaskUiModel`

**Dependencies**: #18

---

### Issue #45: Implement TaskListItem composable
**Labels**: `ui`, `component`, `P0-critical`
**Estimated Complexity**: Medium

**Description**:
Create the composable for displaying a single task item in the list.

**Acceptance Criteria**:
- [ ] Composable function: `TaskListItem(task: Task, onToggleComplete: (Task) -> Unit, onDelete: (Task) -> Unit)`
- [ ] Displays task title and description
- [ ] Checkbox for completion status
- [ ] Delete icon button
- [ ] Card or list item styling
- [ ] Completed tasks show strike-through text
- [ ] Preview function with sample data
- [ ] Accessible (content descriptions, semantic properties)

**Dependencies**: #40

---

### Issue #46: Implement AddTaskDialog composable
**Labels**: `ui`, `component`, `P0-critical`
**Estimated Complexity**: Medium

**Description**:
Create a dialog for adding new tasks.

**Acceptance Criteria**:
- [ ] Composable function: `AddTaskDialog(onDismiss: () -> Unit, onConfirm: (title: String, description: String?) -> Unit)`
- [ ] Text field for task title (required)
- [ ] Text field for task description (optional)
- [ ] Cancel and Add buttons
- [ ] Validation: Add button disabled if title is blank
- [ ] Dialog dismisses after successful add
- [ ] Preview function
- [ ] Accessible

**Dependencies**: #40

---

### Issue #47: Implement TaskListViewModel
**Labels**: `ui`, `viewmodel`, `P0-critical`
**Estimated Complexity**: High

**Description**:
Create the ViewModel for the task list screen, managing state and user interactions.

**Acceptance Criteria**:
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
- [ ] Collects tasks from GetAllTasksUseCase and updates state
- [ ] Error handling with user-friendly messages
- [ ] Uses viewModelScope for coroutines
- [ ] Unit tests with mock use cases

**Dependencies**: #28-#33, #43

---

### Issue #48: Implement TaskListScreen composable
**Labels**: `ui`, `screen`, `P0-critical`
**Estimated Complexity**: High

**Description**:
Create the main task list screen UI.

**Acceptance Criteria**:
- [ ] Composable function: `TaskListScreen(navController: NavController, viewModel: TaskListViewModel = hiltViewModel())`
- [ ] Scaffold with:
  - TopAppBar with title "My Tasks"
  - FAB with "+" icon to show add dialog
  - Body: LazyColumn of tasks
- [ ] Collect UI state: `val state by viewModel.uiState.collectAsState()`
- [ ] Display tasks using TaskListItem
- [ ] Show AddTaskDialog when `state.showAddDialog` is true
- [ ] Show loading indicator when `state.isLoading`
- [ ] Show error message when `state.error` is not null
- [ ] Show EmptyState when no tasks
- [ ] Button/icon to navigate to random task screen
- [ ] Preview function with sample state

**Dependencies**: #45, #46, #47

---

### Issue #49: Wire TaskListScreen into navigation
**Labels**: `ui`, `navigation`, `P0-critical`
**Estimated Complexity**: Low

**Description**:
Add TaskListScreen to the NavGraph and update MainActivity.

**Acceptance Criteria**:
- [ ] `NavGraph` updated with TaskListScreen composable route
- [ ] `MainActivity` sets content with NavGraph
- [ ] TaskList is the start destination
- [ ] App launches and shows TaskListScreen
- [ ] Navigation to other screens works (when implemented)

**Dependencies**: #48, #38

---

### Issue #50: Implement task deletion with confirmation
**Labels**: `feature`, `ui`, `P1-high`
**Estimated Complexity**: Medium

**Description**:
Add confirmation dialog before deleting a task.

**Acceptance Criteria**:
- [ ] ConfirmationDialog component created (if not already in #40)
- [ ] Delete button in TaskListItem shows confirmation dialog
- [ ] Dialog message: "Are you sure you want to delete this task?"
- [ ] Confirm button calls `viewModel.deleteTask(task)`
- [ ] Cancel button dismisses dialog
- [ ] Task is deleted only after confirmation

**Dependencies**: #48

---

### Issue #51: Implement task completion toggle
**Labels**: `feature`, `ui`, `P0-critical`
**Estimated Complexity**: Low

**Description**:
Allow users to mark tasks as complete/incomplete.

**Acceptance Criteria**:
- [ ] Checkbox in TaskListItem bound to `task.isCompleted`
- [ ] Clicking checkbox calls `viewModel.toggleComplete(task)`
- [ ] UI updates immediately (optimistic update or state update)
- [ ] Completed tasks show visual distinction (strike-through, gray text, etc.)
- [ ] Database updates persist the change

**Dependencies**: #48

---

### Issue #52: Write UI tests for task list screen
**Labels**: `testing`, `ui`, `P1-high`
**Estimated Complexity**: High

**Description**:
Create Compose UI tests for the task list screen.

**Acceptance Criteria**:
- [ ] Test class `TaskListScreenTest` created in `androidTest`
- [ ] Use `createAndroidComposeRule` or `createComposeRule`
- [ ] Tests:
  - Screen displays list of tasks
  - FAB shows add dialog
  - Adding task updates the list
  - Deleting task removes it from list
  - Toggling completion updates UI
  - Empty state shown when no tasks
  - Error message displayed on error
- [ ] All tests pass

**Dependencies**: #48, #25

---

## Phase 5: Random Task Screen

### Issue #53: Create RandomTaskUiState
**Labels**: `ui`, `state`, `P0-critical`
**Estimated Complexity**: Low

**Description**:
Define the UI state data class for the random task screen.

**Acceptance Criteria**:
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

**Dependencies**: #18

---

### Issue #54: Implement RandomTaskViewModel
**Labels**: `ui`, `viewmodel`, `P0-critical`
**Estimated Complexity**: Medium

**Description**:
Create the ViewModel for the random task screen.

**Acceptance Criteria**:
- [ ] Class `RandomTaskViewModel` created in `ui.screens.randomtask` package
- [ ] Annotated with `@HiltViewModel`
- [ ] Constructor injects:
  - GetRandomTaskUseCase
  - CompleteTaskUseCase
- [ ] Exposes `StateFlow<RandomTaskUiState>`
- [ ] Functions:
  - `fun loadRandomTask()`
  - `fun completeTask()`
  - `fun skipTask()`
- [ ] `init` block calls `loadRandomTask()`
- [ ] Error handling
- [ ] Unit tests with mock use cases

**Dependencies**: #34, #33, #53

---

### Issue #55: Implement RandomTaskScreen composable
**Labels**: `ui`, `screen`, `P0-critical`
**Estimated Complexity**: Medium

**Description**:
Create the random task screen UI.

**Acceptance Criteria**:
- [ ] Composable function: `RandomTaskScreen(navController: NavController, viewModel: RandomTaskViewModel = hiltViewModel())`
- [ ] Scaffold with TopAppBar
- [ ] Prominently display current task title and description
- [ ] "Complete" button (green, primary action)
- [ ] "Skip" button (secondary action, loads new random task)
- [ ] Back button to return to task list
- [ ] Loading indicator when fetching task
- [ ] Empty state when no incomplete tasks available
- [ ] Error message display
- [ ] Preview function

**Dependencies**: #54, #40

---

### Issue #56: Wire RandomTaskScreen into navigation
**Labels**: `ui`, `navigation`, `P0-critical`
**Estimated Complexity**: Low

**Description**:
Add RandomTaskScreen to the NavGraph.

**Acceptance Criteria**:
- [ ] `NavGraph` updated with RandomTaskScreen route
- [ ] TaskListScreen has button/icon to navigate to RandomTaskScreen
- [ ] Navigation works both ways (task list ↔ random task)
- [ ] Back button on random task screen returns to task list

**Dependencies**: #55, #38

---

### Issue #57: Implement task completion from random screen
**Labels**: `feature`, `ui`, `P0-critical`
**Estimated Complexity**: Low

**Description**:
Allow users to mark the random task as complete and return to task list.

**Acceptance Criteria**:
- [ ] "Complete" button calls `viewModel.completeTask()`
- [ ] Task is marked as complete in database
- [ ] User is navigated back to task list
- [ ] Task no longer appears in incomplete tasks
- [ ] Success feedback (e.g., toast or snackbar)

**Dependencies**: #55

---

### Issue #58: Implement skip task functionality
**Labels**: `feature`, `ui`, `P0-critical`
**Estimated Complexity**: Low

**Description**:
Allow users to skip the current task and load a different random task.

**Acceptance Criteria**:
- [ ] "Skip" button calls `viewModel.skipTask()`
- [ ] ViewModel loads a new random task
- [ ] UI updates to show new task
- [ ] Previous task remains incomplete
- [ ] If only one task available, same task may appear again (acceptable)

**Dependencies**: #55

---

### Issue #59: Write UI tests for random task screen
**Labels**: `testing`, `ui`, `P1-high`
**Estimated Complexity**: Medium

**Description**:
Create Compose UI tests for the random task screen.

**Acceptance Criteria**:
- [ ] Test class `RandomTaskScreenTest` created in `androidTest`
- [ ] Tests:
  - Random task is displayed
  - Complete button marks task done and navigates back
  - Skip button loads new random task
  - Empty state shown when no tasks available
  - Error message displayed on error
  - Loading indicator shown while fetching
- [ ] All tests pass

**Dependencies**: #55, #25

---

## Phase 6: Data Persistence & State Management

### Issue #60: Create Room database migration strategy
**Labels**: `database`, `migration`, `P1-high`
**Estimated Complexity**: Medium

**Description**:
Plan and implement database migration strategy for future schema changes.

**Acceptance Criteria**:
- [ ] Documentation created on migration approach
- [ ] Sample migration created (e.g., version 1 to 2 with dummy change)
- [ ] Migration tested with existing data
- [ ] AutoMigration or manual Migration implemented
- [ ] `AppDatabase` version incremented
- [ ] Migration tests created

**Dependencies**: #15

---

### Issue #61: Implement database pre-population (optional)
**Labels**: `database`, `feature`, `P3-low`
**Estimated Complexity**: Low

**Description**:
Optionally pre-populate the database with sample tasks on first launch for demo purposes.

**Acceptance Criteria**:
- [ ] Callback added to Room database builder
- [ ] `onCreate()` callback inserts sample tasks
- [ ] Only runs on fresh install (not on upgrades)
- [ ] Sample tasks are diverse (some complete, some incomplete)
- [ ] Feature can be easily disabled for production

**Dependencies**: #15

---

### Issue #62: Implement database export/import for debugging
**Labels**: `database`, `tooling`, `P3-low`
**Estimated Complexity**: Medium

**Description**:
Add ability to export and import database for debugging purposes.

**Acceptance Criteria**:
- [ ] Function to export database file to external storage (debug builds only)
- [ ] Function to import database file
- [ ] Accessible via debug menu (see Phase 8)
- [ ] Proper file permissions handling
- [ ] Warning for data overwrite on import

**Dependencies**: #15

---

### Issue #63: Implement ViewModel state persistence
**Labels**: `ui`, `state`, `P2-medium`
**Estimated Complexity**: Medium

**Description**:
Ensure ViewModel state survives configuration changes and process death where appropriate.

**Acceptance Criteria**:
- [ ] SavedStateHandle used in ViewModels where needed
- [ ] Critical UI state (e.g., dialog visibility) persists across config changes
- [ ] Non-critical transient state allowed to reset
- [ ] Tested with:
  - Screen rotation
  - Dark mode toggle
  - Process death (developer options)
- [ ] Documentation on what state is/isn't persisted

**Dependencies**: #47, #54

---

### Issue #64: Write end-to-end integration tests
**Labels**: `testing`, `integration`, `P1-high`
**Estimated Complexity**: High

**Description**:
Create comprehensive integration tests that cover full user workflows.

**Acceptance Criteria**:
- [ ] Test class `EndToEndTest` created in `androidTest`
- [ ] Tests:
  - Add task → view in list → mark complete → verify
  - Add multiple tasks → get random task → complete → verify list updates
  - Add task → delete task → verify removal
  - Skip random task multiple times → verify different tasks
- [ ] Use real database (not mocked)
- [ ] Use Hilt test setup
- [ ] All tests pass

**Dependencies**: #52, #59, #25

---

## Phase 7: Polish & UX Enhancements

### Issue #65: Implement loading states with shimmer effects
**Labels**: `ui`, `ux`, `P2-medium`
**Estimated Complexity**: Medium

**Description**:
Add polished loading indicators with shimmer effects for better UX.

**Acceptance Criteria**:
- [ ] Shimmer library added (e.g., accompanist or custom implementation)
- [ ] Shimmer skeleton shown while loading tasks in TaskListScreen
- [ ] Smooth transitions between loading and content states
- [ ] Loading states don't flash for fast operations
- [ ] Accessible (announce loading to screen readers)

**Dependencies**: #48

---

### Issue #66: Implement task list sorting and filtering
**Labels**: `feature`, `ui`, `P2-medium`
**Estimated Complexity**: Medium

**Description**:
Allow users to sort and filter tasks.

**Acceptance Criteria**:
- [ ] Sort options:
  - Date created (newest first, oldest first)
  - Alphabetical (A-Z, Z-A)
  - Completion status
- [ ] Filter options:
  - All tasks
  - Incomplete only
  - Completed only
- [ ] UI controls (dropdown menu or bottom sheet)
- [ ] Sort/filter preference persisted (DataStore or SharedPreferences)
- [ ] ViewModel updated with sort/filter logic

**Dependencies**: #48

---

### Issue #67: Implement swipe-to-delete gesture
**Labels**: `feature`, `ui`, `P2-medium`
**Estimated Complexity**: Medium

**Description**:
Add swipe gesture for deleting tasks from the list.

**Acceptance Criteria**:
- [ ] Swipe left/right on TaskListItem reveals delete action
- [ ] Confirmation dialog still shown (or undo snackbar)
- [ ] Smooth animation
- [ ] Haptic feedback on swipe
- [ ] Works well with touch targets
- [ ] Accessible alternative (delete button remains)

**Dependencies**: #48

---

### Issue #68: Add animations and transitions
**Labels**: `ui`, `ux`, `P2-medium`
**Estimated Complexity**: Medium

**Description**:
Polish the app with smooth animations and transitions.

**Acceptance Criteria**:
- [ ] List item animations (add, remove, reorder)
- [ ] Screen transition animations
- [ ] Button press animations (scale, ripple)
- [ ] Task completion checkmark animation
- [ ] Smooth dialog enter/exit animations
- [ ] Performance remains smooth (60 fps)

**Dependencies**: #48, #55

---

### Issue #69: Implement edit task functionality
**Labels**: `feature`, `ui`, `P1-high`
**Estimated Complexity**: Medium

**Description**:
Allow users to edit existing tasks.

**Acceptance Criteria**:
- [ ] Edit button/icon on TaskListItem
- [ ] EditTaskDialog or reuse AddTaskDialog with edit mode
- [ ] Pre-populate fields with current task data
- [ ] UpdateTaskUseCase called on save
- [ ] UI updates immediately
- [ ] Validation same as add task
- [ ] Cancel button discards changes

**Dependencies**: #48, #31

---

### Issue #70: Create empty state illustrations
**Labels**: `ui`, `assets`, `P2-medium`
**Estimated Complexity**: Medium

**Description**:
Design and implement friendly empty state graphics.

**Acceptance Criteria**:
- [ ] Empty state illustration designed or sourced
- [ ] Illustration for "No tasks yet" in task list
- [ ] Illustration for "No incomplete tasks" in random task screen
- [ ] SVG or vector drawable format
- [ ] Friendly message encouraging user to add tasks
- [ ] Accessible (content description)

**Dependencies**: #48, #55

---

### Issue #71: Implement accessibility features
**Labels**: `accessibility`, `ux`, `P1-high`
**Estimated Complexity**: Medium

**Description**:
Ensure the app is accessible to users with disabilities.

**Acceptance Criteria**:
- [ ] All interactive elements have content descriptions
- [ ] Minimum touch target size 48dp
- [ ] Color contrast meets WCAG AA standards
- [ ] Screen reader tested (TalkBack)
- [ ] Focus order is logical
- [ ] All actions can be performed without gestures (alternatives provided)
- [ ] Text can scale up to 200% without breaking layout

**Dependencies**: #48, #55

---

### Issue #72: Add haptic feedback
**Labels**: `feature`, `ux`, `P3-low`
**Estimated Complexity**: Low

**Description**:
Add subtle haptic feedback for user interactions.

**Acceptance Criteria**:
- [ ] Haptic feedback on task completion
- [ ] Haptic feedback on task deletion
- [ ] Haptic feedback on swipe actions
- [ ] Haptic feedback on button presses (optional, may be excessive)
- [ ] Respects system haptic settings
- [ ] Not overused (only on significant actions)

**Dependencies**: #48

---

### Issue #73: Implement comprehensive error handling
**Labels**: `feature`, `ux`, `P1-high`
**Estimated Complexity**: Medium

**Description**:
Ensure all error states are handled gracefully with user-friendly messages.

**Acceptance Criteria**:
- [ ] Database errors caught and displayed
- [ ] Network errors (if any future API calls)
- [ ] User-friendly error messages (not stack traces)
- [ ] Retry mechanisms where appropriate
- [ ] Error states tested (inject failures in tests)
- [ ] Snackbar or toast for transient errors
- [ ] Error screen for critical failures

**Dependencies**: #47, #54

---

### Issue #74: Implement app feedback mechanism
**Labels**: `feature`, `P3-low`
**Estimated Complexity**: Low

**Description**:
Add a way for users to provide feedback or report issues.

**Acceptance Criteria**:
- [ ] "Send Feedback" option in settings or menu
- [ ] Opens email client with pre-filled subject
- [ ] Includes app version and device info in email body
- [ ] Link to GitHub issues page (if public repo)
- [ ] Clear instructions for submitting feedback

**Dependencies**: None

---

### Issue #75: Perform performance profiling
**Labels**: `performance`, `testing`, `P2-medium`
**Estimated Complexity**: Medium

**Description**:
Profile the app to identify and fix performance bottlenecks.

**Acceptance Criteria**:
- [ ] Use Android Profiler to measure:
  - App startup time
  - Frame rendering time
  - Memory usage
  - Database query performance
- [ ] Identify any jank or slow operations
- [ ] Optimize identified bottlenecks
- [ ] Recomposition counts optimized in Compose
- [ ] Database queries indexed if needed
- [ ] App maintains 60 fps during normal use

**Dependencies**: #48, #55

---

### Issue #76: Create settings screen (optional)
**Labels**: `feature`, `ui`, `P3-low`
**Estimated Complexity**: Medium

**Description**:
Add a settings screen for user preferences.

**Acceptance Criteria**:
- [ ] Settings screen created
- [ ] Options:
  - Theme (Light/Dark/System)
  - Sort preference (default sort order)
  - Haptic feedback toggle
  - About section (version, licenses, etc.)
- [ ] Preferences stored in DataStore
- [ ] Preferences applied throughout app
- [ ] Navigation to settings from main screen

**Dependencies**: #48

---

## Phase 8: Observability for Development

### Issue #77: Setup logging infrastructure with Timber
**Labels**: `observability`, `setup`, `P2-medium`
**Estimated Complexity**: Low

**Description**:
Add Timber for improved logging during development.

**Acceptance Criteria**:
- [ ] Timber dependency added
- [ ] Timber initialized in Application class (debug builds only)
- [ ] `Timber.DebugTree()` planted for debug builds
- [ ] Replace `Log.*` calls with `Timber.*` throughout codebase
- [ ] Logs visible in Logcat with clear tags
- [ ] No Timber logging in release builds

**Dependencies**: #14

---

### Issue #78: Configure OpenTelemetry (debug builds only)
**Labels**: `observability`, `setup`, `P3-low`
**Estimated Complexity**: High

**Description**:
Set up OpenTelemetry instrumentation for development observability (as per `the-idea/observability-strategy.md`).

**Acceptance Criteria**:
- [ ] OpenTelemetry Android SDK added (debugImplementation only)
- [ ] OTLP exporter configured to send to local collector
- [ ] Instrumentation added for:
  - Database operations (Room queries)
  - Use case executions
  - ViewModel actions
- [ ] Traces include relevant attributes (task IDs, operation types)
- [ ] Configuration documented in observability-strategy.md
- [ ] No overhead in release builds (not included)

**Dependencies**: #14

---

### Issue #79: Setup trace visualization with Jaeger
**Labels**: `observability`, `tooling`, `P3-low`
**Estimated Complexity**: Medium

**Description**:
Set up Jaeger for visualizing OpenTelemetry traces during development.

**Acceptance Criteria**:
- [ ] Docker compose file for running Jaeger locally
- [ ] OpenTelemetry collector configured to forward traces to Jaeger
- [ ] Documentation on how to run Jaeger and view traces
- [ ] Sample trace captured and verified in Jaeger UI
- [ ] README updated with observability setup instructions

**Dependencies**: #78

---

### Issue #80: Create debug menu for development tools
**Labels**: `tooling`, `debug`, `P3-low`
**Estimated Complexity**: Medium

**Description**:
Create an in-app debug menu for development tools (debug builds only).

**Acceptance Criteria**:
- [ ] Debug menu accessible via long-press on app icon or hidden gesture
- [ ] Menu options:
  - View database (export)
  - Clear all data
  - Generate sample tasks
  - Toggle OpenTelemetry
  - View app version and build info
- [ ] Only compiled in debug builds
- [ ] UI for debug menu (dialog or bottom sheet)

**Dependencies**: #77

---

## Phase 9: Testing & Quality Assurance

### Issue #81: Achieve 80%+ unit test coverage
**Labels**: `testing`, `P1-high`
**Estimated Complexity**: High

**Description**:
Ensure high unit test coverage across all layers.

**Acceptance Criteria**:
- [ ] Coverage report generated (use JaCoCo or similar)
- [ ] Overall coverage > 80%
- [ ] Domain layer (use cases, models) > 90%
- [ ] Data layer (repository, mappers) > 80%
- [ ] UI layer (ViewModels) > 75%
- [ ] Coverage report added to CI/CD
- [ ] Coverage report accessible to developers

**Dependencies**: #27, #35, #47, #54

---

### Issue #82: Write instrumented tests for database operations
**Labels**: `testing`, `database`, `P1-high`
**Estimated Complexity**: Medium

**Description**:
Comprehensive instrumented tests for all database operations.

**Acceptance Criteria**:
- [ ] Tests for TaskDao (covered in #26)
- [ ] Tests for database migrations
- [ ] Tests for concurrent access (if applicable)
- [ ] Tests run in CI/CD
- [ ] All tests pass consistently

**Dependencies**: #26, #60

---

### Issue #83: Manual testing on multiple devices
**Labels**: `testing`, `qa`, `P1-high`
**Estimated Complexity**: High

**Description**:
Perform manual testing on various devices and Android versions.

**Acceptance Criteria**:
- [ ] Test on physical devices:
  - At least one phone (different screen sizes)
  - At least one tablet (if supporting tablets)
- [ ] Test on Android versions:
  - Min SDK 24 (Android 7.0)
  - Target SDK 35 (Android 15)
  - At least one in-between version
- [ ] Test scenarios:
  - Complete user workflows
  - Edge cases (empty data, large datasets)
  - Configuration changes (rotation, language change)
  - Interruptions (calls, notifications)
- [ ] Issues documented and fixed

**Dependencies**: #48, #55

---

### Issue #84: Conduct accessibility audit
**Labels**: `testing`, `accessibility`, `P1-high`
**Estimated Complexity**: Medium

**Description**:
Perform a thorough accessibility audit using automated and manual testing.

**Acceptance Criteria**:
- [ ] Run Accessibility Scanner on all screens
- [ ] Manual testing with TalkBack enabled
- [ ] All issues from scanner fixed
- [ ] All screens navigable with TalkBack
- [ ] WCAG 2.1 AA compliance verified
- [ ] Testing documented with results

**Dependencies**: #71

---

### Issue #85: Implement automated UI testing in CI
**Labels**: `testing`, `ci-cd`, `P2-medium`
**Estimated Complexity**: High

**Description**:
Run UI tests automatically in CI/CD pipeline.

**Acceptance Criteria**:
- [ ] GitHub Actions workflow runs instrumented tests
- [ ] Use Android emulator in CI (e.g., reactivecircus/android-emulator-runner)
- [ ] UI tests from #52, #59, #64 run in CI
- [ ] Test results uploaded as artifacts
- [ ] Failures block PR merge
- [ ] Workflow optimized for speed (caching, parallel execution)

**Dependencies**: #52, #59, #64, #4

---

### Issue #86: Implement screenshot testing
**Labels**: `testing`, `ui`, `P3-low`
**Estimated Complexity**: Medium

**Description**:
Add screenshot tests to catch visual regressions.

**Acceptance Criteria**:
- [ ] Screenshot testing library added (e.g., Paparazzi, Shot, or Compose Preview Screenshot)
- [ ] Screenshots captured for key screens and states
- [ ] Baseline screenshots committed to repo
- [ ] Tests fail on visual changes
- [ ] Documentation on updating baseline images
- [ ] Integrated into CI/CD

**Dependencies**: #48, #55, #4

---

### Issue #87: Perform stress testing with large datasets
**Labels**: `testing`, `performance`, `P2-medium`
**Estimated Complexity**: Medium

**Description**:
Test app performance with large numbers of tasks.

**Acceptance Criteria**:
- [ ] Create test scenario with 1000+ tasks
- [ ] Verify list scrolling performance (no jank)
- [ ] Verify database query performance
- [ ] Verify random task selection performance
- [ ] App remains responsive with large dataset
- [ ] Memory usage stays reasonable
- [ ] Pagination or virtualization implemented if needed

**Dependencies**: #48, #75

---

### Issue #88: Code review and refactoring pass
**Labels**: `code-quality`, `P1-high`
**Estimated Complexity**: High

**Description**:
Perform a comprehensive code review and refactoring before release.

**Acceptance Criteria**:
- [ ] All code reviewed for:
  - Consistency with architecture
  - Code duplication
  - Naming conventions
  - Documentation completeness
  - TODO comments resolved
- [ ] ktlint and detekt passing with no warnings
- [ ] Unused code removed
- [ ] Dependencies audited (remove unused)
- [ ] ProGuard/R8 rules reviewed
- [ ] Refactoring documented in commits

**Dependencies**: #5, #6

---

## Phase 10: Release Preparation

### Issue #89: Configure release build variant
**Labels**: `release`, `setup`, `P0-critical`
**Estimated Complexity**: Medium

**Description**:
Set up the release build variant with proper configuration.

**Acceptance Criteria**:
- [ ] Release build type configured in `build.gradle.kts`
- [ ] Minification enabled (R8)
- [ ] Obfuscation enabled
- [ ] ProGuard rules configured for Room, Hilt, Compose
- [ ] Debuggable set to false
- [ ] Logging disabled in release builds
- [ ] Test release build locally

**Dependencies**: #14

---

### Issue #90: Configure app signing
**Labels**: `release`, `security`, `P0-critical`
**Estimated Complexity**: Medium

**Description**:
Set up app signing for release builds.

**Acceptance Criteria**:
- [ ] Keystore generated for release signing
- [ ] Keystore stored securely (not in repo)
- [ ] Signing config in `build.gradle.kts` (using environment variables or CI secrets)
- [ ] Documentation on keystore management
- [ ] Backup of keystore created and stored securely
- [ ] GitHub Actions configured with signing secrets
- [ ] Test signed release build

**Dependencies**: #89

---

### Issue #91: Establish versioning strategy
**Labels**: `release`, `process`, `P1-high`
**Estimated Complexity**: Low

**Description**:
Define and implement semantic versioning for the app.

**Acceptance Criteria**:
- [ ] Versioning scheme documented (semantic versioning: MAJOR.MINOR.PATCH)
- [ ] versionCode and versionName in `build.gradle.kts`
- [ ] Initial version: 1.0.0 (versionCode: 1)
- [ ] Process documented for incrementing versions
- [ ] Version displayed in app (About section or settings)

**Dependencies**: None

---

### Issue #92: Write privacy policy
**Labels**: `release`, `legal`, `P0-critical`
**Estimated Complexity**: Low

**Description**:
Create a privacy policy for the app (required for Play Store).

**Acceptance Criteria**:
- [ ] Privacy policy document created
- [ ] States data collection (local only, no analytics in MVP)
- [ ] States no data sharing with third parties
- [ ] States data storage (local SQLite database)
- [ ] States user rights (data is local, can be cleared)
- [ ] Hosted publicly (GitHub Pages, website, or in app)
- [ ] Link added to Play Store listing

**Dependencies**: None

---

### Issue #93: Document open source licenses
**Labels**: `release`, `legal`, `P1-high`
**Estimated Complexity**: Low

**Description**:
Create a document listing all open source licenses used in the app.

**Acceptance Criteria**:
- [ ] File created: `LICENSES.md` or use OSS Licenses plugin
- [ ] All third-party libraries listed with their licenses
- [ ] Accessible from app (About section)
- [ ] Complies with license requirements (attribution, etc.)

**Dependencies**: None

---

### Issue #94: Choose and apply app license
**Labels**: `release`, `legal`, `P1-high`
**Estimated Complexity**: Low

**Description**:
Choose an open source license for the project.

**Acceptance Criteria**:
- [ ] License chosen (MIT or Apache 2.0 recommended)
- [ ] LICENSE file added to repository root
- [ ] License referenced in README
- [ ] Source file headers updated if required by license

**Dependencies**: None

---

### Issue #95: Create Play Store listing content
**Labels**: `release`, `marketing`, `P0-critical`
**Estimated Complexity**: Medium

**Description**:
Write all the text content needed for the Google Play Store listing.

**Acceptance Criteria**:
- [ ] App title (30 characters max): "Random Task"
- [ ] Short description (80 characters max)
- [ ] Full description (4000 characters max):
  - What the app does
  - Key features
  - How it helps users
- [ ] What's new (release notes for v1.0.0)
- [ ] Content reviewed for clarity and grammar
- [ ] Optimized for search (keywords)

**Dependencies**: None

---

### Issue #96: Create app screenshots for Play Store
**Labels**: `release`, `assets`, `P0-critical`
**Estimated Complexity**: Medium

**Description**:
Capture and create screenshots for the Play Store listing.

**Acceptance Criteria**:
- [ ] At least 2 screenshots (Play Store minimum)
- [ ] Recommended: 4-8 screenshots showing key features
- [ ] Screenshots from phone (required) and tablet (optional)
- [ ] Screenshots show:
  - Task list screen with tasks
  - Add task dialog
  - Random task screen
  - Completed task state
- [ ] High resolution (1080x1920 or higher)
- [ ] Captions/overlays added for clarity (optional but recommended)
- [ ] Screenshots formatted for Play Store requirements

**Dependencies**: #48, #55

---

### Issue #97: Design feature graphic and promo art
**Labels**: `release`, `assets`, `P2-medium`
**Estimated Complexity**: Medium

**Description**:
Create promotional graphics for the Play Store listing.

**Acceptance Criteria**:
- [ ] Feature graphic designed (1024x500 px, required for Play Store)
- [ ] Promo graphic designed (180x120 px, optional)
- [ ] Graphics showcase app's core concept (random task selection)
- [ ] On-brand with app colors and style
- [ ] No text or important content in outer 5% (safe zone)
- [ ] High quality, professional appearance

**Dependencies**: None

---

### Issue #98: Create demo video (optional)
**Labels**: `release`, `marketing`, `P3-low`
**Estimated Complexity**: Medium

**Description**:
Create a short demo video showing the app in action.

**Acceptance Criteria**:
- [ ] Video length: 30-120 seconds
- [ ] Shows key workflows:
  - Adding a task
  - Getting a random task
  - Completing a task
- [ ] High quality recording (1080p+)
- [ ] Uploaded to YouTube
- [ ] Link added to Play Store listing
- [ ] Link added to README

**Dependencies**: #48, #55

---

### Issue #99: Complete pre-launch checklist
**Labels**: `release`, `P0-critical`
**Estimated Complexity**: Low

**Description**:
Go through a comprehensive checklist before launching.

**Acceptance Criteria**:
- [ ] All tests passing (unit, instrumented, UI)
- [ ] No critical bugs or crashes
- [ ] Release build tested on real devices
- [ ] Privacy policy published and linked
- [ ] Play Store listing complete (title, description, screenshots, etc.)
- [ ] App icon finalized
- [ ] ProGuard rules tested (no runtime crashes)
- [ ] Version numbers set correctly
- [ ] Signing configured and tested
- [ ] Analytics/crash reporting configured (if using)
- [ ] All team members reviewed and approved

**Dependencies**: #89, #90, #92, #93, #94, #95, #96

---

## Phase 11: Launch & Post-Launch

### Issue #100: Create and test final release build
**Labels**: `release`, `P0-critical`
**Estimated Complexity**: Medium

**Description**:
Generate the final release APK/AAB and thoroughly test it.

**Acceptance Criteria**:
- [ ] Release build generated: `./gradlew bundleRelease`
- [ ] AAB file created for Play Store upload
- [ ] APK file created for direct distribution (optional)
- [ ] Release build signed with production keystore
- [ ] Test release build on multiple devices:
  - Install and launch successfully
  - All features work as expected
  - No crashes or errors
  - Performance is acceptable
- [ ] File sizes reasonable (APK < 10MB ideally)

**Dependencies**: #99

---

### Issue #101: Submit app to Google Play Console
**Labels**: `release`, `P0-critical`
**Estimated Complexity**: Medium

**Description**:
Upload the app to Google Play Console and complete the release.

**Acceptance Criteria**:
- [ ] Google Play Developer account created (one-time $25 fee)
- [ ] App created in Play Console
- [ ] Release created (Production, or Internal/Closed Testing first)
- [ ] AAB uploaded
- [ ] Store listing completed (all content from #95, #96, #97)
- [ ] Content rating questionnaire completed
- [ ] App content declarations completed (privacy, ads, etc.)
- [ ] Pricing set (Free)
- [ ] Countries selected for distribution
- [ ] Release submitted for review
- [ ] App approved and published (may take a few days)

**Dependencies**: #100

---

### Issue #102: Setup Firebase Crashlytics (optional)
**Labels**: `monitoring`, `P2-medium`
**Estimated Complexity**: Medium

**Description**:
Set up Firebase Crashlytics for crash reporting in production.

**Acceptance Criteria**:
- [ ] Firebase project created
- [ ] Firebase dependencies added (release builds only)
- [ ] google-services.json added to app (gitignored)
- [ ] Crashlytics initialized
- [ ] Test crash reported successfully
- [ ] Dashboard monitored after launch
- [ ] Crash-free users percentage tracked

**Dependencies**: #101

---

### Issue #103: Monitor launch metrics
**Labels**: `monitoring`, `P1-high`
**Estimated Complexity**: Low

**Description**:
Monitor key metrics after launch to ensure successful rollout.

**Acceptance Criteria**:
- [ ] Play Console statistics monitored:
  - Install/uninstall rates
  - Crash rate
  - ANR rate
  - User ratings
- [ ] Crashlytics monitored (if implemented)
- [ ] Address any critical issues within 24-48 hours
- [ ] User reviews monitored and responded to
- [ ] Data collected for first 2 weeks post-launch

**Dependencies**: #101

---

### Issue #104: Create v1.0.0 release notes and GitHub release
**Labels**: `release`, `documentation`, `P1-high`
**Estimated Complexity**: Low

**Description**:
Document the v1.0.0 release with release notes and GitHub release.

**Acceptance Criteria**:
- [ ] CHANGELOG.md created with v1.0.0 entry
- [ ] Release notes include:
  - Initial release announcement
  - MVP feature list
  - Known limitations
  - Future roadmap tease
- [ ] Git tag created: `v1.0.0`
- [ ] GitHub Release created with:
  - Release notes
  - APK attached (optional)
  - Link to Play Store listing
- [ ] Announcement made (social media, blog, etc.)

**Dependencies**: #101

---

### Issue #105: Plan post-MVP roadmap
**Labels**: `planning`, `future`, `P2-medium`
**Estimated Complexity**: Medium

**Description**:
Based on user feedback and MVP learnings, plan the next phase of development.

**Acceptance Criteria**:
- [ ] User feedback collected and analyzed
- [ ] Feature requests prioritized
- [ ] Post-MVP features defined (from `the-idea/project-roadmap.md`):
  - Task categories/tags (v1.1)
  - Task priority levels (v1.2)
  - Task scheduling (v1.3)
  - Cloud sync (v2.0)
  - Analytics (v2.1)
  - Notifications (v2.2)
- [ ] Technical debt identified and prioritized
- [ ] Next milestone defined (v1.1 or v2.0)
- [ ] GitHub issues created for next phase
- [ ] Roadmap communicated to users (if public)

**Dependencies**: #103, #104

---

## Summary Statistics

**Total Issues**: 105
**By Phase**:
- Phase 0 (Foundation): 12 issues
- Phase 1 (Architecture): 15 issues
- Phase 2 (Use Cases): 8 issues
- Phase 3 (UI Foundation): 7 issues
- Phase 4 (Task List): 10 issues
- Phase 5 (Random Task): 7 issues
- Phase 6 (Persistence): 5 issues
- Phase 7 (Polish): 12 issues
- Phase 8 (Observability): 4 issues
- Phase 9 (Testing): 8 issues
- Phase 10 (Release Prep): 11 issues
- Phase 11 (Launch): 6 issues

**By Priority**:
- P0 (Critical): ~45 issues
- P1 (High): ~30 issues
- P2 (Medium): ~20 issues
- P3 (Low): ~10 issues

**By Complexity**:
- Low: ~40 issues
- Medium: ~45 issues
- High: ~20 issues

---

## Next Steps

1. **Setup GitHub Repository** (Issue #1)
2. **Create all 105 issues in GitHub** using this document as reference
3. **Set up GitHub Projects board** with columns: Backlog, Ready, In Progress, Review, Done
4. **Create milestones** for each phase (Phase 0 through Phase 11)
5. **Apply labels** for easy filtering (setup, feature, testing, ui, database, etc.)
6. **Start Phase 0** foundation work before architecture implementation

---

*Generated: 2026-02-12*
*Project: Random Task Android App*
*Total Estimated Timeline: 12-15 weeks to v1.0.0 release*
