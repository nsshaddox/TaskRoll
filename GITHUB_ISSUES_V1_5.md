# Random Task App — v1.5 Dev Tooling & Foundations GitHub Issues

**Milestone**: v1.5
**Total Issues**: 5
**Purpose**: Developer experience improvements and UI foundations to set up before v2.0 feature work.

| Group                          | Issues         | What it unlocks                        |
|--------------------------------|----------------|----------------------------------------|
| 1 — Code Quality Tooling      | #101, #110     | Consistent code style for v2.0 work    |
| 2 — Issue Templates           | #73, #79       | Better issue tracking for v2.0         |
| 3 — UI Foundations             | #143           | Consistent spacing across v2.0 screens |

---

## Group 1: Code Quality Tooling
*No dependencies. Should be done first so all v2.0 code is linted from the start.*

---

### Issue #101: Configure ktlint for code formatting
**Labels**: `v1.5`, `setup`, `code-quality`, `P1-high`
**Estimated Complexity**: Medium
**Branch**: `issue-101-ktlint`

**Description**:
Add ktlint for consistent Kotlin code formatting across the project. Setting this up before
v2.0 ensures all new code follows the same style from day one.

**Acceptance Criteria**:
- [ ] ktlint Gradle plugin added to `build.gradle.kts`
- [ ] Configuration file `.editorconfig` created with project standards
- [ ] Gradle task `./gradlew ktlintCheck` runs successfully
- [ ] Gradle task `./gradlew ktlintFormat` auto-fixes formatting issues
- [ ] ktlint integrated into CI/CD pipeline (GitHub Actions)
- [ ] Existing code passes ktlint checks

**Dependencies**: None

---

### Issue #110: Configure detekt for static analysis
**Labels**: `v1.5`, `setup`, `code-quality`, `P1-high`
**Estimated Complexity**: Medium
**Branch**: `issue-110-detekt`

**Description**:
Add detekt for static code analysis to catch potential bugs and code smells.
Having this in place before v2.0 prevents tech debt from accumulating across 33 new issues.

**Acceptance Criteria**:
- [ ] detekt Gradle plugin added to `build.gradle.kts`
- [ ] Configuration file `detekt.yml` created with project rules
- [ ] Gradle task `./gradlew detekt` runs successfully
- [ ] detekt integrated into CI/CD pipeline (GitHub Actions)
- [ ] Baseline file generated for existing code if needed
- [ ] Critical issues resolved

**Dependencies**: None

---

## Group 2: Issue Templates
*No dependencies. Quick wins for better v2.0 issue management.*

---

### Issue #73: Create bug report issue template
**Labels**: `v1.5`, `documentation`, `setup`, `P1-high`
**Estimated Complexity**: Low
**Branch**: `issue-73-bug-template`

**Description**:
Create a standardized bug report template to help provide necessary information when
reporting bugs during v2.0 development.

**Acceptance Criteria**:
- [ ] File created at `.github/ISSUE_TEMPLATE/bug_report.md`
- [ ] Template includes sections for:
  - Bug description
  - Steps to reproduce
  - Expected behavior
  - Actual behavior
  - Device/OS information
  - App version
  - Screenshots (optional)
- [ ] Template is available when creating new issues

**Dependencies**: None

---

### Issue #79: Create feature request issue template
**Labels**: `v1.5`, `documentation`, `setup`, `P1-high`
**Estimated Complexity**: Low
**Branch**: `issue-79-feature-template`

**Description**:
Create a standardized feature request template to help suggest new features with proper context.

**Acceptance Criteria**:
- [ ] File created at `.github/ISSUE_TEMPLATE/feature_request.md`
- [ ] Template includes sections for:
  - Feature description
  - Use case/problem it solves
  - Proposed solution
  - Alternative solutions considered
  - Additional context
- [ ] Template is available when creating new issues

**Dependencies**: None

---

## Group 3: UI Foundations
*No dependencies. Sets up consistent spacing before v2.0 UI work begins.*

---

### Issue #143: Create dimensions and spacing system
**Labels**: `v1.5`, `ui`, `theme`, `P2-medium`
**Estimated Complexity**: Low
**Branch**: `issue-143-spacing`

**Description**:
Define consistent spacing and dimension values for the UI. v2.0 introduces several new
screens and components — having a shared spacing system ensures visual consistency and
reduces hardcoded `.dp` values scattered across composables.

**Acceptance Criteria**:
- [ ] File created: `Dimensions.kt` in `ui.theme` package
- [ ] Object defining spacing values:
  ```kotlin
  object Spacing {
      val extraSmall = 4.dp
      val small = 8.dp
      val medium = 16.dp
      val large = 24.dp
      val extraLarge = 32.dp
  }
  ```
- [ ] Object defining common sizes (icon sizes, button heights, etc.)
- [ ] Existing composables updated to use `Spacing.*` instead of hardcoded values
- [ ] Used consistently across UI components

**Dependencies**: None

---

## Issues moved to v2.0

The following UI polish issues should be addressed as part of v2.0 feature work:

| Issue | Title                                    |
|-------|------------------------------------------|
| #84   | Implement loading states with shimmer effects |
| #92   | Add animations and transitions           |
| #96   | Create empty state illustrations         |
| #105  | Add haptic feedback                      |
