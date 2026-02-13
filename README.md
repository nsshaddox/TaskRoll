# TaskRoll

**Roll the dice on your to-do list** 🎲

TaskRoll is a native Android app that helps you tackle your tasks by randomly selecting one from your list. Perfect for when you have multiple tasks but can't decide which one to start with.

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/License-TBD-lightgrey.svg)](LICENSE)

---

## 🎯 Features (MVP)

- **📝 Task Management**: Add, edit, delete, and view your tasks
- **🎲 Random Selection**: Get a random task from your incomplete tasks
- **✅ Task Completion**: Mark tasks as complete or skip to get another random task
- **💾 Local Persistence**: All data stored securely on your device with Room Database
- **🎨 Modern UI**: Built with Jetpack Compose and Material3

---

## 📸 Screenshots

_Coming soon - app is currently in development_

---

## 🛠️ Tech Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose with Material3
- **Architecture:** MVVM (Model-View-ViewModel) with Clean Architecture
- **Database:** Room (SQLite)
- **Dependency Injection:** Hilt
- **Async Operations:** Kotlin Coroutines & Flow
- **Navigation:** Navigation Component for Compose
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 35 (Android 15)

---

## 🏗️ Project Structure

```
app/src/main/java/com/nshaddox/randomtask/
├── data/
│   ├── local/              # Room database entities and DAOs
│   └── repository/         # Repository implementations
├── domain/
│   ├── model/              # Domain models
│   ├── repository/         # Repository interfaces
│   └── usecase/            # Business logic use cases
├── ui/
│   ├── screens/            # Compose screens (tasklist, randomtask)
│   └── theme/              # Material3 theme configuration
└── di/                     # Hilt dependency injection modules
```

---

## 📋 Requirements

- **Android Studio:** Hedgehog (2023.1.1) or later
- **JDK:** 17 or later
- **Minimum Android Version:** 7.0 (API 24)
- **Gradle:** 8.0+

---

## 🚀 Getting Started

### Clone the Repository

```bash
git clone https://github.com/nsshaddox/TaskRoll.git
cd TaskRoll
```

### Build the App

```bash
# Build debug variant
./gradlew assembleDebug

# Build release variant
./gradlew assembleRelease
```

### Run the App

1. Open the project in Android Studio
2. Connect an Android device or start an emulator
3. Click **Run** or use:

```bash
./gradlew installDebug
```

---

## 🧪 Testing

### Run Unit Tests

```bash
./gradlew test
```

### Run Instrumented Tests

```bash
# Requires connected device or running emulator
./gradlew connectedAndroidTest
```

### Run Specific Test Class

```bash
./gradlew test --tests com.nshaddox.randomtask.YourTestClass
```

### View Test Coverage

```bash
./gradlew jacocoTestReport
```

---

## 📚 Documentation

- **[CLAUDE.md](CLAUDE.md)** - AI assistant instructions and project guidelines
- **[GITHUB_ISSUES.md](GITHUB_ISSUES.md)** - Comprehensive roadmap with 105 issues across 11 phases
- **[the-idea/](the-idea/)** - Planning documents:
  - `core-requirements.md` - Functional and non-functional requirements
  - `technical-options.md` - Architecture decisions and technical stack rationale
  - `observability-strategy.md` - Development observability approach
  - `project-roadmap.md` - Phased implementation plan

---

## 🤝 Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines on how to contribute to this project.

**Quick Guidelines:**
- Follow the existing code style (enforced by ktlint)
- Write unit tests for new features
- Update documentation as needed
- Create detailed pull requests

---

## 📝 Development Status

🚧 **Currently in Development** - Phase 0 (Project Foundation & Setup)

### Roadmap Overview

- **Phase 0-1:** Project setup and architecture foundation ✅ (In Progress)
- **Phase 2-3:** Domain layer and UI foundation (Planned)
- **Phase 4-5:** Core screens implementation (Planned)
- **Phase 6-7:** Data persistence and polish (Planned)
- **Phase 8-9:** Observability and testing (Planned)
- **Phase 10-11:** Release preparation and launch (Planned)

See [GITHUB_ISSUES.md](GITHUB_ISSUES.md) for the complete 105-issue roadmap.

---

## 🧰 Useful Commands

```bash
# Clean build artifacts
./gradlew clean

# View all available Gradle tasks
./gradlew tasks

# Run lint checks
./gradlew lint

# Format code with ktlint
./gradlew ktlintFormat

# Run static analysis with detekt
./gradlew detekt
```

---

## 📄 License

This project is licensed under [LICENSE TO BE DETERMINED] - see the [LICENSE](LICENSE) file for details.

---

## 👤 Author

**Nick Shaddox**
- GitHub: [@nsshaddox](https://github.com/nsshaddox)

---

## 🙏 Acknowledgments

- Built with guidance from Claude Code (Anthropic)
- Inspired by the challenge of decision fatigue when choosing which task to tackle
- Community feedback and contributions

---

## 🗺️ Post-MVP Features

Future versions may include:
- **v1.1:** Task categories and tags
- **v1.2:** Task priority levels
- **v1.3:** Task scheduling and due dates
- **v2.0:** Cloud sync and backup
- **v2.1:** Statistics and analytics
- **v2.2:** Notifications and reminders

---

## ❓ FAQ

### Why "TaskRoll"?
Like rolling dice, the app randomly selects a task for you, removing the paralysis of choice when you have many things to do.

### Is my data private?
Yes! All task data is stored locally on your device using Room Database. We don't collect, share, or sync any data (in the MVP version).

### What Android versions are supported?
TaskRoll supports Android 7.0 (API 24) and above, covering 99%+ of active Android devices.

---

**Made with ❤️ and Kotlin**
