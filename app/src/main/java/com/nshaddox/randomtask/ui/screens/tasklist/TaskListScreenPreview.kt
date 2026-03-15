package com.nshaddox.randomtask.ui.screens.tasklist

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.SortOrder
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
            tasks = SampleData.sampleTaskUiModels,
            availableCategories = listOf("Work", "Personal"),
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
            tasks = emptyList(),
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
            tasks = listOf(SampleData.sampleTaskUiModel),
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
            isLoading = true,
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
            tasks = SampleData.sampleTaskUiModels,
            errorMessage = "Failed to load tasks",
        )
    }
}

/**
 * Preview for TaskListScreen with active filters
 */
@Preview(showBackground = true)
@Composable
fun TaskListScreenWithFiltersPreview() {
    RandomTaskTheme {
        TaskListScreen(
            tasks = SampleData.sampleTaskUiModels,
            searchQuery = "project",
            filterPriority = Priority.HIGH,
            sortOrder = SortOrder.PRIORITY_DESC,
            availableCategories = listOf("Work", "Personal"),
        )
    }
}

/**
 * Preview for TaskListItem in light and dark modes
 */
@PreviewLightDark
@Composable
fun TaskListItemPreview() {
    RandomTaskTheme {
        TaskListItem(
            task = SampleData.sampleTaskUiModel,
            onTaskClick = {},
            onCheckedChange = {},
            onEditClick = {},
            onDeleteClick = {},
        )
    }
}

/**
 * Preview for TaskListItem with a completed task
 */
@Preview(showBackground = true)
@Composable
fun TaskListItemCompletedPreview() {
    RandomTaskTheme {
        TaskListItem(
            task = SampleData.sampleTaskUiModels.first { it.isCompleted },
            onTaskClick = {},
            onCheckedChange = {},
            onEditClick = {},
            onDeleteClick = {},
        )
    }
}

/**
 * Preview for TaskListItem with overdue task
 */
@Preview(showBackground = true)
@Composable
fun TaskListItemOverduePreview() {
    RandomTaskTheme {
        TaskListItem(
            task = SampleData.sampleOverdueTaskUiModel,
            onTaskClick = {},
            onCheckedChange = {},
            onEditClick = {},
            onDeleteClick = {},
        )
    }
}

/**
 * Preview for EditTaskDialog in add mode
 */
@Preview(showBackground = true)
@Composable
fun AddTaskDialogPreview() {
    RandomTaskTheme {
        EditTaskDialog(
            task = null,
            onConfirm = { _, _, _, _, _ -> },
            onDismiss = {},
        )
    }
}
