package com.nshaddox.randomtask.ui.screens.completedtasks

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.ui.theme.RandomTaskTheme

/**
 * Preview for CompletedTasksScreen with populated list in light and dark modes.
 */
@PreviewLightDark
@Composable
fun CompletedTasksScreenPopulatedPreview() {
    RandomTaskTheme {
        CompletedTasksScreen(
            tasks = sampleCompletedTasks,
        )
    }
}

/**
 * Preview for CompletedTasksScreen with empty state.
 */
@Preview(showBackground = true)
@Composable
fun CompletedTasksScreenEmptyPreview() {
    RandomTaskTheme {
        CompletedTasksScreen(
            tasks = emptyList(),
        )
    }
}

/**
 * Preview for CompletedTasksScreen in loading state.
 */
@Preview(showBackground = true)
@Composable
fun CompletedTasksScreenLoadingPreview() {
    RandomTaskTheme {
        CompletedTasksScreen(
            tasks = emptyList(),
            isLoading = true,
        )
    }
}

private val sampleCompletedTasks =
    listOf(
        Task(
            id = 1L,
            title = "Setup development environment",
            isCompleted = true,
            createdAt = 1_700_000_000_000L,
            updatedAt = 1_700_100_000_000L,
            priority = Priority.LOW,
        ),
        Task(
            id = 2L,
            title = "Create project documentation",
            isCompleted = true,
            createdAt = 1_700_000_100_000L,
            updatedAt = 1_700_200_000_000L,
            priority = Priority.MEDIUM,
        ),
        Task(
            id = 3L,
            title = "Fix critical production bug",
            isCompleted = true,
            createdAt = 1_700_000_200_000L,
            updatedAt = 1_700_300_000_000L,
            priority = Priority.HIGH,
        ),
    )
