# setup-room-database-foundation

**Implemented**: 2026-02-18
**Complexity**: medium (from research phase)

## What Changed

- Added `room = "2.8.4"` to the version catalog with `room-runtime`, `room-ktx`, and `room-compiler` library entries
- Added `implementation(libs.room.runtime)`, `implementation(libs.room.ktx)`, and `ksp(libs.room.compiler)` to `app/build.gradle.kts`
- Created `AppDatabase.kt` — abstract `RoomDatabase` subclass annotated with `@Database`
- Created `TaskEntity.kt` — minimal stub entity (see Implementation Notes)
- Created `DatabaseModule.kt` in new `di/` package — Hilt `@Module` providing `AppDatabase` as a `@Singleton`

## Why

Room is the required local persistence layer for storing and querying tasks. Without it, no task data
can survive app restarts. This unblocks `TaskEntity` (#111), `TaskDao` (#136), and the repository
layer (#149–#151). Wiring it through Hilt ensures the database instance is shared and testable.

## Key Files

- `gradle/libs.versions.toml` — `room = "2.8.4"` version + three library entries
- `app/build.gradle.kts` — Room `implementation` deps and `ksp(libs.room.compiler)`
- `app/src/main/java/com/nshaddox/randomtask/data/local/AppDatabase.kt` — abstract `RoomDatabase` with `@Database` annotation
- `app/src/main/java/com/nshaddox/randomtask/data/local/TaskEntity.kt` — minimal stub entity (replaced by #111)
- `app/src/main/java/com/nshaddox/randomtask/di/DatabaseModule.kt` — `@Singleton`-scoped `AppDatabase` provider

## Implementation Notes

- Used `ksp()` (not `kapt`) — matches existing KSP 2.0.21-1.0.28 setup used for Hilt
- Room 2.8.4 KSP rejects `entities = []`; a minimal `TaskEntity` stub was created to satisfy the processor — issue #111 should replace it with the full entity definition
- `exportSchema = false` — no schema export directory needed for MVP
- Database named `"random_task_db"` — matches project naming convention
- No `fallbackToDestructiveMigration()` needed at version 1

## Verification

- [✓] Tests: N/A (no Room tests in scope; covered by #152, #153)
- [✓] Quality: `./gradlew lintDebug` — BUILD SUCCESSFUL; `./gradlew test` — BUILD SUCCESSFUL
- [✓] Build: `./gradlew assembleDebug` — BUILD SUCCESSFUL; KSP generated Room and Hilt components without errors
