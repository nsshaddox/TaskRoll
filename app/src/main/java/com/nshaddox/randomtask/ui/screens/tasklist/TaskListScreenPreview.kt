package com.nshaddox.randomtask.ui.screens.tasklist

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.nshaddox.randomtask.ui.preview.SampleData
import com.nshaddox.randomtask.ui.theme.RandomTaskTheme

/**
 * Preview for TaskListScreen with loaded tasks in light and dark modes
 */
@PreviewLightDark
@Composable
fun TaskListScreenPreview() {
    RandomTaskTheme {
        TaskListScreen(
            tasks = SampleData.sampleTasks
        )
    }
}

/**
 * Preview for TaskListScreen with empty state
 */
@Preview(showBackground = true)
@Composable
fun TaskListScreenEmptyPreview() {
    RandomTaskTheme {
        TaskListScreen(
            tasks = SampleData.emptyTaskList
        )
    }
}

/**
 * Preview for TaskListScreen with single task
 */
@Preview(showBackground = true)
@Composable
fun TaskListScreenSingleTaskPreview() {
    RandomTaskTheme {
        TaskListScreen(
            tasks = listOf(SampleData.singleTask)
        )
    }
}

/**
 * Preview for TaskListScreen in loading state
 */
@Preview(showBackground = true)
@Composable
fun TaskListScreenLoadingPreview() {
    RandomTaskTheme {
        TaskListScreen(
            tasks = emptyList(),
            isLoading = true
        )
    }
}

/**
 * Preview for TaskListScreen with error message
 */
@Preview(showBackground = true)
@Composable
fun TaskListScreenErrorPreview() {
    RandomTaskTheme {
        TaskListScreen(
            tasks = SampleData.sampleTasks,
            errorMessage = "Failed to load tasks"
        )
    }
}

/**
 * Preview for AddTaskDialog
 */
@Preview(showBackground = true)
@Composable
fun AddTaskDialogPreview() {
    RandomTaskTheme {
        AddTaskDialog(
            onConfirm = {},
            onDismiss = {}
        )
    }
}
