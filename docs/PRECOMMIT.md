# Pre-commit Hooks Guide

This guide covers the pre-commit hook configuration, usage, and troubleshooting for the Random Task application.

## Overview

Pre-commit hooks run automated checks before each commit to ensure code quality. They catch lint violations and test failures early, before changes reach the repository.

## Installation

### Quick Setup

```bash
./gradlew installGitHooks
```

This copies the hook script from `git-hooks/pre-commit` to `.git/hooks/pre-commit` and sets executable permissions.

### Manual Installation

```bash
cp git-hooks/pre-commit .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit
```

### Verification

```bash
# Check that the hook is installed
ls -la .git/hooks/pre-commit

# Test the hook manually
.git/hooks/pre-commit
```

## What the Hook Does

The pre-commit hook runs two checks sequentially:

```bash
#!/bin/bash
set -e

echo "Running pre-commit checks..."
./gradlew lintDebug --quiet
./gradlew testDebugUnitTest --quiet
echo "All checks passed! Proceeding with commit."
```

### 1. Android Lint (`lintDebug`)

- Checks for Android-specific issues (unused resources, accessibility, API compatibility)
- Validates XML layouts and Compose code
- Reports are generated at `app/build/reports/lint-results-debug.html`

### 2. Unit Tests (`testDebugUnitTest`)

- Runs all unit tests in `app/src/test/`
- Covers use cases, ViewModels, mappers, and state classes
- Reports are generated at `app/build/reports/tests/testDebugUnitTest/index.html`

If either check fails, the commit is aborted. Fix the issues and try again.

## Usage

### Normal Workflow

1. Make your changes
2. Stage files: `git add <files>`
3. Commit: `git commit -m "your message"`
4. Hook runs automatically — if checks pass, commit proceeds
5. If checks fail, fix the issues, re-stage, and commit again

### Skipping Hooks

```bash
# Skip all hooks (use sparingly)
git commit --no-verify -m "your message"
```

Use `--no-verify` only when:
- You're committing documentation-only changes
- You're in the middle of a rebase/merge and have already verified tests
- The hook is failing due to an environment issue, not a code issue

**Never skip hooks to avoid fixing lint or test failures.**

### Running Checks Manually

You can run the same checks the hook runs at any time:

```bash
# Run both checks (same as the hook)
./gradlew lintDebug testDebugUnitTest

# Run just lint
./gradlew lintDebug

# Run just tests
./gradlew testDebugUnitTest

# Run with verbose output for debugging
./gradlew lintDebug testDebugUnitTest --info
```

## Hook Configuration

### Source File

The hook script lives at `git-hooks/pre-commit` and is version-controlled. Any changes to the hook should be made to this file, not directly to `.git/hooks/pre-commit`.

### Gradle Task

The `installGitHooks` task is defined in the root `build.gradle.kts`:

```kotlin
tasks.register<Copy>("installGitHooks") {
    from("git-hooks/pre-commit")
    into(".git/hooks")
    filePermissions { unix("rwxr-xr-x") }
}
```

## Troubleshooting

### Hook Not Running

```bash
# Verify the hook exists and is executable
ls -la .git/hooks/pre-commit

# Reinstall if missing
./gradlew installGitHooks

# Check file permissions (should be -rwxr-xr-x)
chmod +x .git/hooks/pre-commit
```

### Lint Failures

```bash
# Run lint with full output to see the issue
./gradlew lintDebug

# Open the lint report for details
open app/build/reports/lint-results-debug.html
```

Common lint issues:
- Missing content descriptions on images/icons
- Hardcoded strings (use `@string/` resources)
- Unused resources or imports
- API compatibility issues with min SDK 24

### Test Failures

```bash
# Run tests with info-level logging
./gradlew testDebugUnitTest --info

# Run a specific failing test for focused debugging
./gradlew test --tests "com.nshaddox.randomtask.domain.usecase.AddTaskUseCaseTest"

# Open the test report
open app/build/reports/tests/testDebugUnitTest/index.html
```

Common test issues:
- Missing `Dispatchers.setMain()` in ViewModel tests
- Turbine `.test {}` block not awaiting all emissions
- Stale generated code — run `./gradlew clean` first

### Hook Is Slow

The hook runs lint and all unit tests, which can take 30-60 seconds. To speed things up:

- Ensure Gradle daemon is running (`./gradlew --status`)
- Use Gradle build cache (enabled by default)
- Close other resource-heavy applications during commits

### Hook Fails After Merge/Rebase

If the hook fails after pulling changes:

```bash
# Clean and rebuild to clear stale state
./gradlew clean

# Then try committing again
git add .
git commit -m "your message"
```

## CI Integration

The same checks run in the GitHub Actions CI pipeline (`.github/workflows/android-ci.yml`):

1. `lintDebug` — Same lint check as the hook
2. `testDebugUnitTest` — Same unit tests as the hook
3. `assembleDebug` — Additional build verification

This means the hook catches issues locally before they reach CI, providing faster feedback.

## Best Practices

1. **Never skip hooks to avoid fixing issues** — The hook exists to protect code quality
2. **Run checks manually before large commits** — Don't wait for the hook if you're unsure
3. **Keep the hook fast** — If new checks are added, balance thoroughness with speed
4. **Fix hook failures immediately** — Don't accumulate issues to fix later
5. **Update the hook via `git-hooks/pre-commit`** — Never edit `.git/hooks/` directly since it's not version-controlled
