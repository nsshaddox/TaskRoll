# Architecture Overview

This document describes the package structure and layer responsibilities for the RandomTask Android app. It is intended as a quick reference for contributors. For deeper context on requirements and technical decisions, see the cross-references at the bottom of this file.

---

## Layer Responsibilities

### data

The data layer is the single source of truth for all persisted information. It owns Room database entities and DAOs (`data/local`) as well as concrete repository implementations (`data/repository`) that translate between the database and domain models.

### domain

The domain layer contains the business logic of the app and has no dependency on Android framework classes. It defines repository interfaces (`domain/repository`), domain model classes (`domain/model`), and use cases (`domain/usecase`) that encapsulate individual business operations invoked by ViewModels.

### ui

The ui layer contains all Jetpack Compose screens and their associated ViewModels. Each screen lives in its own sub-package under `ui/screens`, and shared theme definitions (colors, typography, shapes) live in `ui/theme`.

### di

The di layer holds Hilt modules that wire together the dependency graph. It is the only layer that imports from both the data and domain layers simultaneously, keeping all binding declarations in one place.

---

## Package Map

All packages are rooted at `com.nshaddox.randomtask`.

| Package | Description |
|---|---|
| `data.local` | Room database entities and DAOs for local SQLite storage |
| `data.repository` | Concrete implementations of domain repository interfaces (e.g., `TaskRepositoryImpl`) |
| `domain.model` | Plain Kotlin data classes representing domain concepts (e.g., `Task`) |
| `domain.repository` | Repository interfaces consumed by use cases and ViewModels (e.g., `TaskRepository`) |
| `domain.usecase` | Single-responsibility use case classes that encapsulate business operations |
| `di` | Hilt modules that provide and bind dependencies across all layers |
| `ui.theme` | Compose `MaterialTheme` configuration: colors, typography, and shapes |
| `ui.screens.tasklist` | Task list screen composable and its ViewModel |
| `ui.screens.randomtask` | Random task selection screen composable and its ViewModel |
| `ui.screens.taskeditor` | Task add/edit screen composable and its ViewModel |

---

## Cross-References

This document intentionally avoids duplicating content that is maintained elsewhere. Consult the following files for deeper context:

- **[CLAUDE.md](./CLAUDE.md)** — Build commands, technical stack, MVVM principles, and project conventions for contributors
- **[the-idea/core-requirements.md](./the-idea/core-requirements.md)** — Functional and non-functional requirements that drive the architecture
- **[the-idea/technical-options.md](./the-idea/technical-options.md)** — Rationale for choosing Room, Hilt, Compose, and the clean architecture layer split
