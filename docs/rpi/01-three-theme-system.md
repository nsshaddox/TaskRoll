# Three-Theme System (Obsidian, Neo Brutalist, Vapor)

**Implemented**: 2026-03-16
**Complexity**: complex

## What Changed

- Replaced `AppTheme` enum with `ThemeVariant` (OBSIDIAN, NEO_BRUTALIST, VAPOR) in domain layer
- Created `AppThemeTokens` data class with custom tokens (priority colors, borders, shadows, uppercase flags)
- Added `LocalAppThemeTokens` and `LocalThemeVariant` CompositionLocals for theme-aware UI access
- Rewrote `Color.kt` and `Theme.kt` with three ColorSchemes and three token instances
- Created themed wrapper components: `ThemedCard`, `ThemedCheckbox`, `ThemedFAB`, `ThemedPriorityBadge`
- Updated all screens, dialogs, and filter bar to use themed components instead of raw Material3
- Updated Settings to persist and display `ThemeVariant` choices via `SettingsViewModel`

## Why

The app required a unified visual identity system supporting three distinct aesthetics (premium dark, bold light, pastel dark) while maintaining code-driven consistency. Neo Brutalist's structural differences (solid shadows, borders, uppercase) could not be expressed via a simple light/dark toggle. Centralizing tokens in `AppThemeTokens` enables screens to react to the active variant without scattered conditional logic.

## Key Files

- `domain/model/ThemeVariant.kt` — Pure Kotlin enum (zero Android imports)
- `ui/theme/AppThemeTokens.kt` — Token data class + three instances
- `ui/theme/LocalAppTheme.kt` — CompositionLocal providers
- `ui/theme/Theme.kt` — Three ColorSchemes + CompositionLocalProvider wiring
- `ui/theme/Color.kt` — Three palette groups (obsidian*, neoBrutalist*, vapor*)
- `ui/components/Themed{Card,Checkbox,FAB,PriorityBadge}.kt` — Per-variant wrapper components

## Implementation Notes

- Domain layer pure Kotlin; `Themed*` components read `LocalThemeVariant` and `LocalAppThemeTokens` to branch
- Color-selection logic extracted into pure testable helper functions
- DataStore migration automatic via `readEnum` default (OBSIDIAN)
- `Type.kt` single Typography retained; theme casing applied via `useUppercaseTitles` token

## Verification

- Tests: `./gradlew testDebugUnitTest` — BUILD SUCCESSFUL
- Quality: `./gradlew lintDebug` — BUILD SUCCESSFUL (zero issues)
- Manual: All screens render correctly in all three themes
