# Weighted Random Task Selection

**Feature**: 14-weighted-random
**Date**: 2026-03-14
**Complexity**: Medium
**Source Issues**: GH#231

## What Changed

- Created `GetWeightedRandomTaskUseCase` with priority weighting (HIGH 3x, MEDIUM 2x, LOW 1x)
- Added toggle in `RandomTaskViewModel` to switch between uniform and weighted random selection
- Added "Prioritise high-priority tasks" Switch toggle in `RandomTaskScreen`

## Why

Users wanted the option to bias random task selection toward higher-priority tasks.

## Key Files

| File | Change Type |
|------|-------------|
| `domain/usecase/GetWeightedRandomTaskUseCase.kt` | Created |
| `ui/screens/randomtask/RandomTaskViewModel.kt` | Modified |
| `ui/screens/randomtask/RandomTaskScreen.kt` | Modified |

## Implementation Notes

- Use case is pure Kotlin (zero Android imports) — mirrors `GetRandomTaskUseCase` structure
- `buildWeightedPool` is `internal` (not private) to enable deterministic pool-structure tests
- Toggle is a separate `StateFlow<Boolean>` — `RandomTaskUiState` unchanged

## Verification

- [x] 6 use case tests + 5 ViewModel toggle tests
- [x] `GetRandomTaskUseCase` byte-for-byte unchanged
