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
        RandomTaskScreen(
            selectedTask = SampleData.singleTask
        )
    }
}

/**
 * Preview for RandomTaskScreen with no task selected (empty state)
 */
@Preview(showBackground = true)
@Composable
fun RandomTaskScreenEmptyPreview() {
    RandomTaskTheme {
        RandomTaskScreen(
            selectedTask = null
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
        RandomTaskScreen(
            selectedTask = SampleData.sampleTask
        )
    }
}
