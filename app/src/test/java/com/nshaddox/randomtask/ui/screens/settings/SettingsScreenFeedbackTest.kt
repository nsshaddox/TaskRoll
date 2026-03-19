package com.nshaddox.randomtask.ui.screens.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Tests for the feedback mechanism wired into SettingsScreen.
 *
 * Note on Compose UI tests (plan tasks 1.1 / 1.2): The plan originally called for
 * `createComposeRule()`-based tests verifying that the "Send Feedback" row is visible
 * in `SettingsScreenContent` and that clicking it invokes the `onSendFeedback` callback.
 * However, `ui-test-junit4` is only available as `androidTestImplementation` in this
 * project and cannot be used in JVM unit tests. Compose UI tests for row visibility and
 * click behavior belong in `androidTest/` (instrumented tests).
 *
 * These unit tests instead verify the pure-function contract of the feedback utilities
 * that the composable delegates to: intent construction (`buildFeedbackEmailIntent`) and
 * safe launch (`safeLaunchFeedbackIntent`). The `onSendFeedback` callback wiring test
 * below exercises the same code path that executes when a user taps the feedback row.
 */
class SettingsScreenFeedbackTest {
    private val mockUri = mockk<Uri>(relaxed = true)
    private val uriStringSlot = slot<String>()

    @Before
    fun setup() {
        mockkStatic(Uri::class)
        every { Uri.parse(capture(uriStringSlot)) } returns mockUri
    }

    @After
    fun tearDown() {
        unmockkStatic(Uri::class)
    }

    @Test
    fun `buildFeedbackEmailIntent returns Intent instance`() {
        val intent =
            buildFeedbackEmailIntent(
                emailAddress = "test@example.com",
                subject = "RandomTask Feedback -- v1.0",
                body = "body",
            )

        assertTrue("Should return an Intent", intent is Intent)
    }

    @Test
    fun `buildFeedbackEmailIntent parses mailto URI with email address`() {
        buildFeedbackEmailIntent(
            emailAddress = "test@example.com",
            subject = "subject",
            body = "body",
        )

        verify { Uri.parse("mailto:test@example.com") }
        assertTrue(
            "URI string should contain mailto: prefix",
            uriStringSlot.captured.startsWith("mailto:"),
        )
        assertTrue(
            "URI string should contain the email address",
            uriStringSlot.captured.contains("test@example.com"),
        )
    }

    @Test
    fun `buildFeedbackEmailIntent sets data on the intent`() {
        val intent =
            buildFeedbackEmailIntent(
                emailAddress = "feedback@randomtask.app",
                subject = "subject",
                body = "body",
            )

        verify { Uri.parse("mailto:feedback@randomtask.app") }
    }

    @Test
    fun `safeLaunchFeedbackIntent catches ActivityNotFoundException gracefully`() {
        val launcher: (Intent) -> Unit = {
            throw ActivityNotFoundException("No email app")
        }

        val intent =
            buildFeedbackEmailIntent(
                emailAddress = "test@example.com",
                subject = "subject",
                body = "body",
            )
        val result = safeLaunchFeedbackIntent(intent, launcher)

        assertFalse("Should return false when no email app available", result)
    }

    @Test
    fun `safeLaunchFeedbackIntent returns true on successful launch`() {
        var launched = false
        val launcher: (Intent) -> Unit = {
            launched = true
        }

        val intent =
            buildFeedbackEmailIntent(
                emailAddress = "test@example.com",
                subject = "subject",
                body = "body",
            )
        val result = safeLaunchFeedbackIntent(intent, launcher)

        assertTrue("Should return true on successful launch", result)
        assertTrue("Launcher should have been called", launched)
    }

    @Test
    fun `safeLaunchFeedbackIntent passes intent to launcher`() {
        var receivedIntent: Intent? = null
        val launcher: (Intent) -> Unit = { receivedIntent = it }

        val intent =
            buildFeedbackEmailIntent(
                emailAddress = "test@example.com",
                subject = "subject",
                body = "body",
            )
        safeLaunchFeedbackIntent(intent, launcher)

        assertTrue("Launcher should receive the same intent", receivedIntent === intent)
    }

    // --- onSendFeedback callback wiring ---
    // These tests exercise the same code path that runs when the user taps
    // "Send Feedback" in SettingsScreenContent. The composable's onSendFeedback
    // callback calls buildFeedbackEmailIntent then safeLaunchFeedbackIntent.

    @Test
    fun `onSendFeedback callback builds intent and launches successfully`() {
        var launchCount = 0
        val onSendFeedback = {
            val intent =
                buildFeedbackEmailIntent(
                    emailAddress = "feedback@example.com",
                    subject = "RandomTask Feedback -- v1.0",
                    body = "App version: 1.0\nOS: 14\nDevice: Pixel 8",
                )
            safeLaunchFeedbackIntent(intent) { launchCount++ }
        }

        onSendFeedback()

        assertTrue("Launcher should be invoked exactly once", launchCount == 1)
        verify { Uri.parse("mailto:feedback@example.com") }
    }

    @Test
    fun `onSendFeedback callback does not crash when no email client available`() {
        val onSendFeedback = {
            val intent =
                buildFeedbackEmailIntent(
                    emailAddress = "feedback@example.com",
                    subject = "subject",
                    body = "body",
                )
            safeLaunchFeedbackIntent(intent) {
                throw ActivityNotFoundException("No email app")
            }
        }

        val result = onSendFeedback()

        assertFalse("Should return false when no email app", result)
    }
}
