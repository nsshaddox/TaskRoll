# Testing Guide

This guide covers the testing strategy, patterns, and best practices for the Random Task application.

## Overview

Random Task enforces **Strict Test-Driven Development (TDD)** with a layered testing approach following the test pyramid principle. All feature implementations must follow the **Red-Green-Refactor** cycle:

1. **RED** — Write a failing test that defines the desired behavior
2. **GREEN** — Write the minimum code required to make the test pass
3. **REFACTOR** — Improve the code while keeping all tests green

**Never write production code without a failing test first.**

Tests validate behavior across domain logic, data access, ViewModels, and UI components.

## Test Structure

```
app/src/test/java/com/nshaddox/randomtask/
├── domain/usecase/
│   ├── FakeTaskRepository.kt          # In-memory test repository
│   ├── AddTaskUseCaseTest.kt
│   ├── UpdateTaskUseCaseTest.kt
│   ├── CompleteTaskUseCaseTest.kt
│   ├── DeleteTaskUseCaseTest.kt
│   ├── GetTasksUseCaseTest.kt
│   └── GetRandomTaskUseCaseTest.kt
├── data/repository/
│   ├── TaskMappersTest.kt
│   └── TaskRepositoryImplTest.kt
└── ui/screens/
    ├── tasklist/
    │   ├── TaskListViewModelTest.kt
    │   ├── TaskListUiStateTest.kt
    │   └── TaskUiMappersTest.kt
    ├── randomtask/
    │   ├── RandomTaskViewModelTest.kt
    │   └── RandomTaskUiStateTest.kt
    └── taskeditor/
        └── TaskEditorViewModelTest.kt

app/src/androidTest/java/com/nshaddox/randomtask/
├── ExampleInstrumentedTest.kt
└── HiltTestRunner.kt
```

## Test Pyramid

```
        ┌──────────────┐
        │   UI Tests   │  Compose UI Testing, Espresso
        │   (few)      │  Full component rendering
        ├──────────────┤
        │ Integration  │  Room DAO tests, Hilt testing
        │  (moderate)  │  Real database operations
        ├──────────────┤
        │  Unit Tests  │  JUnit 4, MockK, Turbine
        │   (many)     │  Use cases, ViewModels, mappers
        └──────────────┘
```

- **Unit tests** are fast, isolated, and make up the bulk of the suite
- **Integration tests** verify Room queries and Hilt wiring against real databases
- **UI tests** validate Compose component behavior and user interactions

## Testing Frameworks and Tools

| Tool | Purpose | Dependency |
|------|---------|------------|
| **JUnit 4** | Test runner and assertions | `junit:junit:4.13.2` |
| **MockK** | Kotlin-first mocking | `io.mockk:mockk:1.13.17` |
| **Turbine** | Flow testing utilities | `app.cash.turbine:turbine:1.2.1` |
| **Coroutines Test** | Test dispatcher and `runTest` | `kotlinx-coroutines-test:1.10.2` |
| **Hilt Testing** | DI in instrumented tests | `hilt-android-testing:2.51.1` |
| **Room Testing** | Migration testing | `room-testing:2.8.4` |
| **Compose UI Testing** | Compose component tests | `ui-test-junit4` (via BOM) |
| **Espresso** | Android UI assertions | `espresso-core:3.6.1` |
| **JaCoCo** | Code coverage reporting and verification | `jacoco:0.8.12` (Gradle plugin) |

## TDD Implementation Patterns

### Red-Green-Refactor Example

```kotlin
// 1. RED: Write failing test first
@Test
fun `invoke with valid title and priority returns success`() = runTest {
    val result = addTaskUseCase("Buy groceries", null, Priority.HIGH)
    assertTrue(result.isSuccess)
    // This test FAILS because addTaskUseCase doesn't accept Priority yet
}

// 2. GREEN: Write minimum code to pass
suspend operator fun invoke(
    title: String,
    description: String?,
    priority: Priority = Priority.MEDIUM
): Result<Long> {
    if (title.isBlank()) return Result.failure(IllegalArgumentException("Title required"))
    val task = Task(title = title, description = description, priority = priority, ...)
    return repository.addTask(task)
}

// 3. REFACTOR: Improve while keeping tests green
// Extract validation, improve naming, remove duplication — then re-run all tests
```

### TDD Quality Gates

| Phase | Gate |
|-------|------|
| **RED** | Test is written, test fails, failure reason is correct |
| **GREEN** | Test passes, only minimum code was written |
| **REFACTOR** | Code improved, all tests still pass, no duplication |

### Coverage Requirements

- **Minimum 90% line coverage** for new code
- **100% branch coverage** for critical business logic (use cases, validation)
- All edge cases must be explicitly tested

### JaCoCo Coverage Enforcement

Coverage thresholds are enforced automatically by `jacocoTestCoverageVerification`. The pre-commit hook and CI pipeline both run this check.

#### Per-Package Thresholds

| Package | Metric | Minimum | Notes |
|---------|--------|---------|-------|
| **Bundle (overall)** | Instruction | 80% | Adjusted for coroutine synthetic bytecode |
| `domain.*` | Instruction | 85% | Pure Kotlin business logic |
| `domain.usecase` | Branch | 70% | Kotlin coroutines generate unreachable synthetic branches |
| `data.*` | Instruction | 90% | Repository and mapper coverage |
| `ui.*` | Instruction | 90% | ViewModels, UiState, UI mappers |

#### Excluded from Coverage

The following classes are excluded from coverage metrics because they are generated code, framework boilerplate, or declarative UI that cannot be meaningfully unit-tested:

- **Hilt-generated**: `*_HiltComponents*`, `*_MembersInjector*`, `*_Factory*`, `*Module_Provide*`
- **Room-generated**: `*_Impl*`, `AppDatabase*`, `TaskDao`, `TaskEntity`
- **Compose-generated**: `ComposableSingletons*`, `*ScreenKt*`, `*DialogKt*`, `*Preview*`
- **Kotlin inline synthetics**: `*$$inlined$*` (Flow.map and other inline function artifacts)
- **Android entry points**: `MainActivity`, `RandomTaskApplication`
- **Declarative code**: DI modules, navigation, theme, preview data

#### Coroutine Coverage Limitation

JaCoCo reports phantom missed branches in Kotlin `suspend` functions because the Kotlin compiler generates a state machine with branches that are unreachable from normal test execution. This is a known JaCoCo limitation. The branch coverage threshold for `domain.usecase` is set to 70% to account for this. All reachable branches must still be tested.

## Unit Testing Patterns

### Use Case Tests

Use cases are tested with `FakeTaskRepository`, an in-memory implementation backed by `MutableStateFlow`:

```kotlin
class AddTaskUseCaseTest {
    private lateinit var fakeRepository: FakeTaskRepository
    private lateinit var addTaskUseCase: AddTaskUseCase

    @Before
    fun setup() {
        fakeRepository = FakeTaskRepository()
        addTaskUseCase = AddTaskUseCase(fakeRepository)
    }

    @Test
    fun `invoke with valid title returns success with task id`() = runTest {
        val result = addTaskUseCase("Buy groceries", "Milk and eggs")
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }

    @Test
    fun `invoke with blank title returns failure`() = runTest {
        val result = addTaskUseCase("  ", null)
        assertTrue(result.isFailure)
    }
}
```

**Key patterns:**
- `FakeTaskRepository` over mocks for domain tests — simulates real behavior
- `runTest` from coroutines-test for suspend function testing
- Test both success and failure paths
- Test edge cases (blank input, empty lists, null values)

### ViewModel Tests

ViewModels require dispatcher setup for coroutine testing:

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class TaskListViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: TaskListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // ... create ViewModel with test dependencies
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has loading true`() = runTest(testDispatcher) {
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isLoading)
        }
    }

    @Test
    fun `addTask with valid title updates task list`() = runTest(testDispatcher) {
        viewModel.uiState.test {
            skipItems(1) // skip initial state
            viewModel.addTask("New task", null)
            val state = awaitItem()
            assertTrue(state.tasks.any { it.title == "New task" })
        }
    }
}
```

**Key patterns:**
- `StandardTestDispatcher` for controlled async execution
- `Dispatchers.setMain()` / `resetMain()` in `@Before` / `@After`
- Turbine's `.test {}` block for `StateFlow` assertions
- `skipItems(n)` to skip initial state emissions
- `awaitItem()` to get next emission

### Mapper Tests

```kotlin
class TaskMappersTest {
    @Test
    fun `toDomain maps all fields correctly`() {
        val entity = TaskEntity(
            id = 1, title = "Test", description = "Desc",
            isCompleted = false, createdAt = 1000L, updatedAt = 2000L
        )
        val task = entity.toDomain()
        assertEquals("Test", task.title)
        assertEquals(1000L, task.createdAt)
    }

    @Test
    fun `toEntity maps all fields correctly`() {
        val task = Task(
            id = 1, title = "Test", description = null,
            isCompleted = true, createdAt = 1000L, updatedAt = 2000L
        )
        val entity = task.toEntity()
        assertEquals("Test", entity.title)
        assertNull(entity.description)
    }
}
```

### UiState Tests

Verify default state values:

```kotlin
class TaskListUiStateTest {
    @Test
    fun `default state has empty task list and loading true`() {
        val state = TaskListUiState()
        assertTrue(state.tasks.isEmpty())
        assertTrue(state.isLoading)
        assertNull(state.errorMessage)
    }
}
```

## Integration Testing

### Hilt Test Runner

A custom `HiltTestRunner` is configured for instrumented tests:

```kotlin
class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
```

This is registered in `app/build.gradle.kts`:
```kotlin
testInstrumentationRunner = "com.nshaddox.randomtask.HiltTestRunner"
```

### Room DAO Tests

When writing DAO tests, use `@HiltAndroidTest` with an in-memory database:

```kotlin
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TaskDaoTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var database: AppDatabase
    private lateinit var dao: TaskDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.taskDao()
    }

    @After
    fun tearDown() {
        database.close()
    }
}
```

### Migration Tests

When adding database migrations, test them with Room's `MigrationTestHelper`:

```kotlin
@RunWith(AndroidJUnit4::class)
class MigrationTest {
    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java
    )

    @Test
    fun migrate1To2() {
        // Create version 1 database
        helper.createDatabase(TEST_DB, 1).apply {
            execSQL("INSERT INTO tasks (title, is_completed, created_at, updated_at) VALUES ('Test', 0, 1000, 2000)")
            close()
        }

        // Run migration and validate
        val db = helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2)
        // ... verify data survived migration
    }
}
```

## Running Tests

### Command Line

```bash
# All unit tests
./gradlew test

# Specific test class
./gradlew test --tests com.nshaddox.randomtask.domain.usecase.AddTaskUseCaseTest

# Tests matching a pattern
./gradlew test --tests "*ViewModelTest"

# With detailed output
./gradlew test --info

# Instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Both lint and tests (same as pre-commit hook)
./gradlew lintDebug testDebugUnitTest

# Generate coverage report (HTML + XML)
./gradlew jacocoTestReport

# Verify coverage thresholds pass
./gradlew jacocoTestCoverageVerification

# Full pre-commit check (lint + tests + coverage)
./gradlew lintDebug testDebugUnitTest jacocoTestCoverageVerification
```

### Android Studio

- **Run single test**: Click the green play icon next to a test method
- **Run test class**: Click the green play icon next to the class declaration
- **Run all tests**: Right-click the `test/` directory → `Run Tests`
- **Debug tests**: Use breakpoints and the debug runner

### Test Reports

After running tests, reports are generated at:
- **Unit tests**: `app/build/reports/tests/testDebugUnitTest/index.html`
- **Test results**: `app/build/test-results/testDebugUnitTest/`
- **Coverage report**: `app/build/reports/jacoco/jacocoTestReport/html/index.html` (run `./gradlew jacocoTestReport` first)
- **Coverage XML**: `app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml`

## Test Data

### FakeTaskRepository

The test suite uses a `FakeTaskRepository` backed by `MutableStateFlow<List<Task>>` for unit tests:

- Simulates real repository behavior without Room
- Supports all `TaskRepository` interface methods
- Auto-generates IDs for inserted tasks
- Emits updates reactively via Flow

### SampleData (Previews)

`MockData.kt` provides `SampleData` object with pre-fabricated tasks for Compose previews:
- `sampleTask` — A basic task
- `sampleTasks` — A list of tasks in various states
- `emptyTaskList` — Empty list for empty-state previews
- `sampleTaskWithDescription` — Task with description populated

## Best Practices

### Test Organization

- **Mirror source structure**: Test files live in the same package path as the code they test
- **One test class per source class**: `AddTaskUseCase` → `AddTaskUseCaseTest`
- **Descriptive test names**: Use backtick syntax that reads as a specification:
  ```kotlin
  @Test
  fun `addTask with blank title returns failure`() { ... }
  ```

### Test Independence

- Each test must be able to run in isolation
- Use `@Before` to create fresh instances — never share mutable state between tests
- Use `@After` to clean up dispatchers and resources

### Assertion Patterns

- Use Kotlin's built-in `assertTrue`, `assertEquals`, `assertNull` for simple assertions
- Use Turbine's `awaitItem()` for Flow emissions — avoid `first()` which can miss updates
- Test `Result<T>` with `.isSuccess` / `.isFailure` and `.getOrNull()` / `.exceptionOrNull()`

### What to Test

| Layer | What to test | What NOT to test |
|-------|-------------|-----------------|
| **Use Cases** | Validation logic, error handling, delegation | Repository internals |
| **ViewModels** | State transitions, user action handling, error propagation | Compose UI rendering |
| **Mappers** | All field mappings, null handling, default values | — |
| **DAO** | Query correctness, insert/update/delete behavior | Room framework internals |
| **UI** | User interactions, state rendering, navigation triggers | ViewModel logic (tested separately) |

### Coroutine Testing Checklist

- [ ] `StandardTestDispatcher` created and used consistently
- [ ] `Dispatchers.setMain(testDispatcher)` in `@Before`
- [ ] `Dispatchers.resetMain()` in `@After`
- [ ] `runTest(testDispatcher)` wraps test body
- [ ] Turbine `.test {}` used for Flow assertions
- [ ] `testDispatcher.scheduler.advanceUntilIdle()` called when needed

## CI Integration

The GitHub Actions workflow (`.github/workflows/android-ci.yml`) runs:
1. `lintDebug` — Android lint checks
2. `testDebugUnitTest` — All unit tests
3. `jacocoTestReport` — Generate coverage report (HTML + XML)
4. `jacocoTestCoverageVerification` — Verify per-package coverage thresholds
5. `assembleDebug` — Debug build

Test results, lint reports, and coverage reports are uploaded as workflow artifacts with 7-day retention. The coverage report is uploaded with `if: always()` so it is available for debugging even when the verification step fails.

The pre-commit hook runs `lintDebug`, `testDebugUnitTest`, and `jacocoTestCoverageVerification` locally before each commit. See the **[Pre-commit Guide](PRECOMMIT.md)** for details.
