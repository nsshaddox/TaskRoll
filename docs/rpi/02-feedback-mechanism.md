# Send Feedback Mechanism

**Implemented**: 2026-03-18
**Complexity**: simple

## What Changed

- Added "Send Feedback" tappable row in Settings screen About section
- Implemented `FeedbackUtils.kt` with Intent builder and safe launcher for email client
- Extended `SettingsScreen` to pre-fill email with app version, OS version, and device model
- Added email strings to `strings.xml` (recipient, subject template, body template)
- Extended `build.gradle.kts` to enable unit test default values for Intent mocking

## Why

Enable users to provide feedback directly via their device email client without requiring in-app infrastructure or permissions beyond what's already needed for email functionality.

## Key Files

- `app/src/main/java/com/nshaddox/randomtask/ui/screens/settings/FeedbackUtils.kt` - Utility functions to build and safely launch email Intent
- `app/src/main/java/com/nshaddox/randomtask/ui/screens/settings/SettingsScreen.kt` - Added `onSendFeedback` callback and feedback row in About section
- `app/src/main/res/values/strings.xml` - Feedback strings with format placeholders
- `app/src/test/java/com/nshaddox/randomtask/ui/screens/settings/SettingsScreenFeedbackTest.kt` - Unit tests for Intent builder and safe launcher

## Implementation Notes

- Feedback launch is a pure UI side-effect wired in the stateful `SettingsScreen`, not the ViewModel (no state needed)
- Intent builder uses `ACTION_SENDTO` with `mailto:` URI to avoid requiring `INTERNET` permission
- `ActivityNotFoundException` caught gracefully when no email client available
- All UI strings use MaterialTheme tokens only — renders correctly in all three themes
- Test utility functions extracted to `FeedbackUtils` for testability without Compose test infrastructure

## Verification

- Tests: 6 passing (Uri parsing, Intent building, safe launch with success/failure paths)
- Quality: `./gradlew lintDebug testDebugUnitTest` passes clean
- Manual: Email client opens with pre-filled subject/body when feedback row tapped
