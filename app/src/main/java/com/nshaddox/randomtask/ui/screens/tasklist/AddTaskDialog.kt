package com.nshaddox.randomtask.ui.screens.tasklist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.nshaddox.randomtask.ui.theme.Spacing

/**
 * Dialog for adding a new task.
 *
 * @param onConfirm Called with the task title and optional description when the user taps "Add".
 * @param onDismiss Called when the user cancels or dismisses the dialog.
 * @param modifier Modifier for customization.
 */
@Composable
fun AddTaskDialog(
    onConfirm: (String, String?) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var text by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("Task title") },
                    singleLine = true,
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Description (optional)") },
                    singleLine = false,
                    minLines = 2,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(text.trim(), description.trim().ifBlank { null }) },
                enabled = text.isNotBlank(),
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        modifier = modifier,
    )
}
