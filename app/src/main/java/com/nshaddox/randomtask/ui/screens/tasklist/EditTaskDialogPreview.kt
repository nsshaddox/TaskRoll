package com.nshaddox.randomtask.ui.screens.tasklist

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.ui.theme.RandomTaskTheme
import java.time.LocalDate

/**
 * Preview for EditTaskDialog in add mode (task = null)
 */
@PreviewLightDark
@Composable
fun EditTaskDialogAddModePreview() {
    RandomTaskTheme {
        EditTaskDialog(
            task = null,
            onConfirm = { _, _, _, _, _ -> },
            onDismiss = {},
        )
    }
}

/**
 * Preview for EditTaskDialog in edit mode (task pre-populated)
 */
@PreviewLightDark
@Composable
fun EditTaskDialogEditModePreview() {
    RandomTaskTheme {
        EditTaskDialog(
            task =
                TaskUiModel(
                    id = 1L,
                    title = "Review pull request",
                    description = "Check code coverage and approve if tests pass",
                    isCompleted = false,
                    createdAt = "Mar 1, 2026 9:00 AM",
                    updatedAt = "Mar 14, 2026 2:30 PM",
                    priority = Priority.HIGH,
                    priorityLabel = "High",
                    dueDateLabel = "Mar 20, 2026",
                    category = "Work",
                ),
            initialDueDate = LocalDate.of(2026, 3, 20),
            onConfirm = { _, _, _, _, _ -> },
            onDismiss = {},
        )
    }
}
