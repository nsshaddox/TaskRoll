# setup-testing-infrastructure

**Implemented**: 2026-02-18
**Complexity**: medium (from research phase)

## What Changed

- Added testing library versions and aliases to Gradle version catalog (MockK, Turbine, coroutines-test, AndroidX Test Core, Room Testing, Hilt Testing)
- Added testImplementation, androidTestImplementation, and kspAndroidTest dependencies to app build file
- Created custom HiltTestRunner for instrumented tests using HiltTestApplication
- Replaced placeholder ExampleUnitTest with sample tests using MockK, coroutines-test, and Turbine
- Replaced placeholder ExampleInstrumentedTest with Hilt + Room in-memory DB sample test
- Added META-INF packaging excludes to resolve duplicate license file conflicts

## Why

Testing infrastructure is a prerequisite for all feature development tests (Issues #26, #27, #35, #52, #59, #64). This establishes the foundational libraries and patterns so future feature tests can focus on business logic rather than configuration.

## Key Files

- `gradle/libs.versions.toml` - Added 4 version entries and 7 library aliases for testing dependencies
- `app/build.gradle.kts` - Added 9 dependency lines (test/androidTest/kspAndroidTest), custom test runner, packaging excludes
- `app/src/androidTest/java/com/nshaddox/randomtask/HiltTestRunner.kt` - New custom AndroidJUnitRunner for Hilt testing
- `app/src/test/java/com/nshaddox/randomtask/ExampleUnitTest.kt` - Sample unit test demonstrating MockK + Turbine + runTest
- `app/src/androidTest/java/com/nshaddox/randomtask/ExampleInstrumentedTest.kt` - Sample instrumented test with Hilt + Room in-memory DB

## Implementation Notes

- Used KSP (not kapt) for Hilt testing compiler, consistent with existing project configuration
- MockK version 1.13.17, Turbine 1.2.1, coroutines-test 1.10.2, AndroidX Test Core 1.7.0
- Room Testing and Hilt Testing versions match their runtime counterparts (2.8.4 and 2.51.1)

## Verification

- [x] Tests: `./gradlew testDebugUnitTest` -- BUILD SUCCESSFUL
- [x] Quality: `./gradlew assembleDebug` -- BUILD SUCCESSFUL
- [x] AndroidTest compilation: `./gradlew assembleDebugAndroidTest` -- BUILD SUCCESSFUL
