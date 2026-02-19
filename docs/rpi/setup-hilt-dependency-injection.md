# setup-hilt-dependency-injection

**Implemented**: 2026-02-18
**Complexity**: medium (from research phase)

## What Changed

- Added Hilt `2.51.1` and KSP `2.0.21-1.0.28` to the version catalog with library and plugin entries
- Declared Hilt and KSP plugins in root `build.gradle.kts` with `apply false`
- Applied Hilt + KSP plugins and added three dependency entries in `app/build.gradle.kts`
- Created `RandomTaskApplication` annotated with `@HiltAndroidApp`
- Registered `RandomTaskApplication` in `AndroidManifest.xml`
- Added `@AndroidEntryPoint` to `MainActivity`
- Deleted `di/.gitkeep.kt` placeholder (package now has real purpose)

## Why

Hilt is the required DI foundation for the MVVM architecture — without it, ViewModels, repositories,
and Room modules cannot be injected. This unblocks Phase 2 features (Room #87, repository injection
#149–#151, ViewModel injection #97). KSP is used instead of KAPT for compatibility with Kotlin 2.0.21.

## Key Files

- `gradle/libs.versions.toml` — versions, libraries, and plugins for Hilt and KSP
- `build.gradle.kts` — root plugin declarations (`apply false`)
- `app/build.gradle.kts` — plugin application and dependency declarations (`ksp()` config)
- `app/src/main/java/com/nshaddox/randomtask/RandomTaskApplication.kt` — new `@HiltAndroidApp` class
- `app/src/main/AndroidManifest.xml` — `android:name=".RandomTaskApplication"` attribute
- `app/src/main/java/com/nshaddox/randomtask/MainActivity.kt` — `@AndroidEntryPoint` annotation

## Implementation Notes

- Used KSP (not KAPT) — required for Kotlin 2.0.21 and faster annotation processing
- Pinned `hilt-navigation-compose` to `1.2.0`; `1.3.0` has a breaking artifact split
- Chose Hilt `2.51.1` (stable) over `2.59.1` (less proven at time of implementation)
- No `@Module`/`@Provides` classes yet — that's a separate issue (#151)

## Verification

- [✓] Tests: N/A (pure configuration, no unit tests required)
- [✓] Quality: `./gradlew help` — BUILD SUCCESSFUL; `./gradlew assembleDebug` — BUILD SUCCESSFUL in 4s
- [✓] Manual: `hilt-android:2.51.1` confirmed in compile classpath via `:app:dependencies`
