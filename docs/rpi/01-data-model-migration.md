# Data Model Extensions + Room Migration

**Implemented**: 2026-03-14 | **Complexity**: medium

## What Changed

- Created `Priority` enum (LOW, MEDIUM, HIGH) in domain layer
- Extended `Task` and `TaskEntity` with `priority`, `dueDate` (epoch-days), `category` fields
- Implemented mapper extensions to convert Priority enum ↔ String
- Bumped `AppDatabase` from version 1 to 2 with non-destructive `MIGRATION_1_2`
- Configured KSP schema export to `app/schemas/`
- Added instrumented migration test validating v1→v2 upgrade path

## Why

Foundation for v2.0 features. Non-destructive migration preserves user data during app updates while enabling new domain logic.

## Key Files

- `domain/model/Priority.kt` — Pure Kotlin enum; zero Android imports
- `domain/model/Task.kt` — Added three optional fields with defaults
- `data/local/TaskEntity.kt` — Added three `@ColumnInfo` columns with snake_case names
- `data/repository/TaskMappers.kt` — Extended bidirectional mapping for new fields
- `data/local/AppDatabase.kt` — Version 2, `exportSchema=true`, `MIGRATION_1_2`
- `di/DatabaseModule.kt` — Registered migration in database builder
- `data/local/MigrationTest.kt` — Instrumented test verifying migration

## Implementation Notes

- `dueDate` stored as `Long?` epoch-days to avoid `java.time.LocalDate` desugaring with minSdk=24
- `priority` stored as enum name String (no custom type converters)
- All fields have Kotlin defaults; existing code compiles unchanged
- Pre-existing rows acquire `priority='MEDIUM'` via NOT NULL DEFAULT; others NULL by default

## Verification

- Lint: PASSED — `./gradlew lintDebug`
- Tests: PASSED — `./gradlew testDebugUnitTest` (all 16 test files)
- Coverage: PASSED — `./gradlew jacocoTestCoverageVerification` (95% instruction coverage)
- Build: PASSED — `./gradlew assembleDebug`
- Migration test: Compiles successfully (requires device/emulator)
