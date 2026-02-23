# add-navigation-compose-dependency

**Implemented**: 2026-02-22
**Complexity**: simple

## What Changed

- Added `androidx.navigation:navigation-compose:2.8.7` to the Gradle version catalog and app dependencies
- Enables direct use of `NavHost`, `composable()`, and `rememberNavController` APIs

## Why

The `hilt-navigation-compose:1.2.0` library was already declared but the core `navigation-compose` library was missing. Without it, Compose Navigation APIs are not directly accessible, blocking navigation route definitions (#122), NavGraph wiring (#128), and screen navigation (#119, #66). This is a foundation dependency for the entire navigation layer.

## Key Files

- `gradle/libs.versions.toml` - Added `navigationCompose = "2.8.7"` version and `navigation-compose` library entry
- `app/build.gradle.kts` - Added `implementation(libs.navigation.compose)` after hilt-navigation-compose

## Implementation Notes

- Version 2.8.7 chosen (latest stable); 2.9.x requires compileSdk 36, incompatible with project's compileSdk 35
- Follows existing version catalog pattern used by all other dependencies
- Three-line change total: one version entry, one library entry, one implementation line

## Verification

- [x] Build: `./gradlew assembleDebug` - BUILD SUCCESSFUL
- [x] Tests: `./gradlew test` - BUILD SUCCESSFUL (all unit tests pass)
- [x] Config: Version catalog and build file entries confirmed present
