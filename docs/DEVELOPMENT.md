# Development Guide

This guide covers development setup, workflow, and contribution guidelines for the Random Task application.

## Prerequisites

- **Android Studio** (latest stable) or IntelliJ IDEA with Android plugin
- **JDK 17** or later
- **Android SDK** with API 35 installed
- **Git** for version control
- **Android device or emulator** running API 24+ for testing

## Quick Start

```bash
git clone <repository-url>
cd RandomTask

# Install pre-commit hooks
./gradlew installGitHooks

# Build the app
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Install on connected device/emulator
./gradlew installDebug
```

## Build Commands

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Clean build
./gradlew clean assembleDebug

# Run all unit tests
./gradlew test

# Run specific test class
./gradlew test --tests com.nshaddox.randomtask.domain.usecase.AddTaskUseCaseTest

# Run tests matching a pattern
./gradlew test --tests "*ViewModelTest"

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Run Android lint
./gradlew lintDebug

# View all available Gradle tasks
./gradlew tasks
```

## Development Workflow

### Strict TDD Requirement

**MANDATORY**: All development must follow the **Red-Green-Refactor** cycle. Never write production code without a failing test first.

#### 1. RED Phase — Write Failing Test

- Write a test that defines the desired behavior
- Ensure the test fails for the correct reason (not a compilation error)
- Test should be specific and focused on one behavior

#### 2. GREEN Phase — Make Test Pass

- Write the **minimum** code required to make the test pass
- No extra functionality beyond what the test requires
- Focus on making the test pass quickly

#### 3. REFACTOR Phase — Improve Code

- Improve code structure while keeping all tests green
- Eliminate duplication and improve readability
- Run all tests to confirm nothing broke

### Feature Development Process

1. **Create a feature branch** from `main`
2. **Understand requirements** — Read the relevant GitHub issue
3. **Design tests** — Write comprehensive failing tests (RED phase)
4. **TDD implementation** — Follow Red-Green-Refactor cycle for each behavior
5. **Run all checks** — `./gradlew lintDebug testDebugUnitTest`
6. **Commit** — Pre-commit hooks validate automatically
7. **Open a pull request** against `main`

### Branch Naming

Follow the convention: `issue-{number}-{short-description}`

Examples:
- `issue-201-priority-enum`
- `issue-204-db-migration`
- `issue-216-list-viewmodel`

### Commit Messages

Use descriptive commit messages that explain the *why*, not just the *what*:

```
feat(domain): add Priority enum and mapper support

Add Priority enum (LOW, MEDIUM, HIGH) to the domain model with
bidirectional mapper support in TaskMappers.kt. Default value
is MEDIUM for backwards compatibility.

Closes #201
```

Prefix conventions:
- `feat` — New feature
- `fix` — Bug fix
- `refactor` — Code restructuring without behavior change
- `test` — Adding or updating tests
- `docs` — Documentation changes
- `chore` — Build, CI, or tooling changes

### Quality Gates

#### Before Writing Code

- [ ] Test is written and failing (RED phase)
- [ ] Test clearly defines expected behavior
- [ ] Test covers edge cases and error conditions

#### Before Commit

- [ ] All unit tests pass (`./gradlew test`)
- [ ] Lint checks pass (`./gradlew lintDebug`)
- [ ] Code coverage meets standards (>90% for new code)
- [ ] Pre-commit hook passes (runs both automatically)
- [ ] No hardcoded strings in UI (use string resources)

#### Before Merge

- [ ] Tests written before implementation (TDD verified)
- [ ] PR reviewed
- [ ] CI pipeline passes
- [ ] No unresolved review comments
- [ ] Documentation updated if needed

## Project Structure

```
RandomTask/
├── app/
│   ├── build.gradle.kts          # App module build config
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   └── java/com/nshaddox/randomtask/
│       │       ├── MainActivity.kt
│       │       ├── RandomTaskApplication.kt
│       │       ├── data/           # Room entities, DAO, repository impl, mappers
│       │       ├── di/             # Hilt modules
│       │       ├── domain/         # Task model, repository interface, use cases
│       │       └── ui/             # Screens, ViewModels, navigation, theme
│       ├── test/                   # Unit tests (JUnit 4, MockK, Turbine)
│       └── androidTest/            # Instrumented tests (Hilt, Room, Compose)
├── build.gradle.kts               # Root build config with plugins
├── gradle/libs.versions.toml      # Version catalog
├── git-hooks/                     # Pre-commit hook scripts
├── docs/                          # Project documentation
├── the-idea/                      # Planning documents and roadmap
└── CLAUDE.md                      # AI agent guidance
```

## IDE Setup

### Android Studio

1. **Open project**: `File → Open` and select the `RandomTask/` root directory
2. **Sync Gradle**: Android Studio will auto-sync; click "Sync Now" if prompted
3. **Run configuration**: `app` run configuration is created automatically
4. **Emulator**: Create an AVD with API 24+ via `Tools → Device Manager`

### Useful IDE Settings

- **Enable auto-import**: `Settings → Editor → General → Auto Import → Optimize imports on the fly`
- **Format on save**: `Settings → Tools → Actions on Save → Reformat code`
- **KSP generated sources**: Should be auto-detected; if not, mark `build/generated/ksp` as generated sources root

## Dependency Management

All dependencies are managed via the Gradle version catalog at `gradle/libs.versions.toml`:

```toml
[versions]
kotlin = "2.0.21"
composeBom = "2026.02.00"
hilt = "2.51.1"
room = "2.8.4"
# ... etc
```

To add a new dependency:
1. Add the version to `[versions]`
2. Add the library to `[libraries]`
3. Reference it in `build.gradle.kts` as `libs.your.library`

## Database

### Room Database

- Database name: `random_task_db`
- Located in app's internal storage
- Current version: 1

### Schema Changes

When modifying entities:
1. Increment `@Database(version = N)` in `AppDatabase`
2. Add a `Migration(N-1, N)` with the appropriate `ALTER TABLE` SQL
3. Register the migration in `DatabaseModule` via `.addMigrations()`
4. Test the migration with Room's migration testing utilities

### Inspecting the Database

Use Android Studio's **Database Inspector** (`View → Tool Windows → App Inspection`) to browse the live database on a running device/emulator.

## Hilt Dependency Injection

### Module Organization

| Module | What it provides |
|--------|------------------|
| `DatabaseModule` | `AppDatabase`, `TaskDao` |
| `RepositoryModule` | `TaskRepository` (binds impl) |
| `CoroutineModule` | Named dispatchers (`IO`, `Default`, `Main`) |

### Adding a New Dependency

1. Create or update the appropriate Hilt `@Module`
2. Use `@Provides` for concrete instances or `@Binds` for interface-to-impl bindings
3. Scope with `@Singleton` for app-wide singletons
4. Inject via constructor injection (prefer over field injection)

## Troubleshooting

### Build Fails

```bash
# Clean and rebuild
./gradlew clean assembleDebug

# Invalidate caches in Android Studio
File → Invalidate Caches → Invalidate and Restart
```

### KSP/Hilt Errors

- Ensure all `@HiltViewModel` classes have `@Inject constructor`
- Check that `@AndroidEntryPoint` is on `MainActivity`
- Run `./gradlew clean` to clear stale generated code

### Room Schema Errors

- Entity changes require a migration or database version bump
- Check that `@ColumnInfo` names match your SQL in migrations
- Use `fallbackToDestructiveMigration()` only during development (never in production)

### Test Failures

- Run with `--info` for detailed output: `./gradlew test --info`
- Check that `Dispatchers.setMain()` is called in `@Before` for ViewModel tests
- Ensure Turbine's `test {}` block awaits all expected emissions

### Pre-commit Hook Issues

See the **[Pre-commit Guide](PRECOMMIT.md)** for hook troubleshooting.
