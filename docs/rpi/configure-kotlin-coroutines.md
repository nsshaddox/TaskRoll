# configure-kotlin-coroutines

**Implemented**: 2026-02-22
**Complexity**: simple (from research phase)

## What Changed

- Renamed `coroutinesTest` version key to shared `coroutines` (1.10.2) in version catalog
- Added `kotlinx-coroutines-core` and `kotlinx-coroutines-android` as explicit implementation dependencies
- Added `lifecycle-viewmodel-compose` dependency for `viewModelScope` support
- Created `CoroutineModule` Hilt module providing injectable dispatchers via `@Named` qualifiers (IO, Default, Main)
- Created `SampleViewModel` demonstrating `viewModelScope` + `stateIn` + injected dispatcher pattern
- Created `SampleViewModelTest` using `StandardTestDispatcher`, `Dispatchers.setMain`/`resetMain`, and Turbine

## Why

Kotlin Coroutines are the standard async mechanism for Android. While coroutines-test existed for testing, the production libraries were only available transitively. Explicit dependencies and injectable dispatchers via Hilt establish the foundation for testable ViewModels across the app.

## Key Files

- `gradle/libs.versions.toml` - Renamed version key, added 3 library aliases, updated coroutines-test ref
- `app/build.gradle.kts` - Added 3 implementation dependencies (coroutines-core, coroutines-android, viewmodel-compose)
- `app/src/main/java/com/nshaddox/randomtask/di/CoroutineModule.kt` - Hilt module providing named dispatchers
- `app/src/main/java/com/nshaddox/randomtask/ui/SampleViewModel.kt` - Sample ViewModel with viewModelScope and stateIn
- `app/src/test/java/com/nshaddox/randomtask/ui/SampleViewModelTest.kt` - ViewModel test with test dispatchers and Turbine

## Implementation Notes

- Followed existing `DatabaseModule.kt` pattern for the Hilt module (object, @Provides, @Singleton)
- Used `@Named` string qualifiers for dispatchers to keep injection simple
- SampleViewModel is intentionally minimal -- demonstrates the pattern for future feature ViewModels
- `FakeTaskRepository` from domain tests was reused for ViewModel test setup

## Verification

- [x] Build: `./gradlew assembleDebug` -- BUILD SUCCESSFUL
- [x] Tests: `./gradlew testDebugUnitTest` -- BUILD SUCCESSFUL (including SampleViewModelTest)
- [x] Manual: Version catalog has 4 coroutines entries, no duplicate version keys
