package com.nshaddox.randomtask.ui.screens.randomtask

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.nshaddox.randomtask.ui.preview.SampleData
import com.nshaddox.randomtask.ui.theme.RandomTaskTheme

/**
 * Preview for RandomTaskScreen with a selected task in light and dark modes
 */
@PreviewLightDark
@Composable
fun RandomTaskScreenWithTaskPreview() {
    RandomTaskTheme {
        RandomTaskScreenContent(
            uiState = RandomTaskUiState(currentTask = SampleData.singleTask),
        )
    }
}

/**
 * Preview for RandomTaskScreen with no task selected (empty/initial state)
 */
@Preview(showBackground = true)
@Composable
fun RandomTaskScreenEmptyPreview() {
    RandomTaskTheme {
        RandomTaskScreenContent(
            uiState = RandomTaskUiState(),
        )
    }
}

/**
 * Preview for RandomTaskScreen with a shorter task title
 */
@Preview(showBackground = true)
@Composable
fun RandomTaskScreenShortTitlePreview() {
    RandomTaskTheme {
        RandomTaskScreenContent(
            uiState = RandomTaskUiState(currentTask = SampleData.sampleTask),
        )
    }
}

/**
 * Preview for RandomTaskScreen in loading state
 */
@Preview(showBackground = true)
@Composable
fun RandomTaskScreenLoadingPreview() {
    RandomTaskTheme {
        RandomTaskScreenContent(
            uiState = RandomTaskUiState(isLoading = true),
        )
    }
}

/**
 * Preview for RandomTaskScreen in error state
 */
@Preview(showBackground = true)
@Composable
fun RandomTaskScreenErrorPreview() {
    RandomTaskTheme {
        RandomTaskScreenContent(
            uiState = RandomTaskUiState(error = "Something went wrong"),
        )
    }
}

/**
 * Preview for RandomTaskScreen with no tasks available
 */
@Preview(showBackground = true)
@Composable
fun RandomTaskScreenNoTasksPreview() {
    RandomTaskTheme {
        RandomTaskScreenContent(
            uiState = RandomTaskUiState(noTasksAvailable = true),
        )
    }
}

/**
 * Preview for RandomTaskScreen with a task that has a description
 */
@Preview(showBackground = true)
@Composable
fun RandomTaskScreenWithDescriptionPreview() {
    RandomTaskTheme {
        RandomTaskScreenContent(
            uiState = RandomTaskUiState(currentTask = SampleData.sampleTaskWithDescription),
        )
    }
}
