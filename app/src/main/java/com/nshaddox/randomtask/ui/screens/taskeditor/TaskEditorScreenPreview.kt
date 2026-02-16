package com.nshaddox.randomtask.ui.screens.taskeditor

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.nshaddox.randomtask.ui.theme.RandomTaskTheme

/**
 * Preview for TaskEditorScreen in "New Task" mode with light and dark themes
 */
@PreviewLightDark
@Composable
fun TaskEditorScreenNewTaskPreview() {
    RandomTaskTheme {
        TaskEditorScreen(
            taskTitle = "",
            isEditMode = false
        )
    }
}

/**
 * Preview for TaskEditorScreen in "Edit Task" mode with existing content
 */
@Preview(showBackground = true)
@Composable
fun TaskEditorScreenEditModePreview() {
    RandomTaskTheme {
        TaskEditorScreen(
            taskTitle = "Complete project wireframes and mockups",
            isEditMode = true
        )
    }
}

/**
 * Preview for TaskEditorScreen with partial input
 */
@Preview(showBackground = true)
@Composable
fun TaskEditorScreenPartialInputPreview() {
    RandomTaskTheme {
        TaskEditorScreen(
            taskTitle = "Write unit tests for",
            isEditMode = false
        )
    }
}

/**
 * Preview for TaskEditorScreen with long text
 */
@Preview(showBackground = true)
@Composable
fun TaskEditorScreenLongTextPreview() {
    RandomTaskTheme {
        TaskEditorScreen(
            taskTitle = "Review all pull requests, merge approved changes, update documentation, and notify the team about the latest deployment",
            isEditMode = true
        )
    }
}
