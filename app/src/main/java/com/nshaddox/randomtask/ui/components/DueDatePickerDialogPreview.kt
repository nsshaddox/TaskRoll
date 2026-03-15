package com.nshaddox.randomtask.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.nshaddox.randomtask.ui.theme.RandomTaskTheme

/**
 * Preview for DueDatePickerDialog with no initial date
 */
@Preview
@Composable
fun DueDatePickerDialogPreview() {
    RandomTaskTheme {
        DueDatePickerDialog(
            initialDate = null,
            onConfirm = {},
            onDismiss = {},
        )
    }
}
