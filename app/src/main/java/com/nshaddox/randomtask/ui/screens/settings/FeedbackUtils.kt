package com.nshaddox.randomtask.ui.screens.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri

/**
 * Builds an [Intent.ACTION_SENDTO] intent targeting the device email client.
 *
 * @param emailAddress Recipient email address.
 * @param subject Pre-filled email subject line.
 * @param body Pre-filled email body text.
 * @return A configured [Intent] ready to launch.
 */
internal fun buildFeedbackEmailIntent(
    emailAddress: String,
    subject: String,
    body: String,
): Intent =
    Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$emailAddress")
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }

/**
 * Attempts to launch the given [intent] via the provided [launcher].
 * Returns `true` if the launch succeeded, `false` if no email app was found.
 *
 * @param intent The feedback email intent to launch.
 * @param launcher A function that calls `context.startActivity(intent)`.
 * @return `true` on success, `false` if [ActivityNotFoundException] was caught.
 */
internal fun safeLaunchFeedbackIntent(
    intent: Intent,
    launcher: (Intent) -> Unit,
): Boolean =
    try {
        launcher(intent)
        true
    } catch (_: ActivityNotFoundException) {
        false
    }
