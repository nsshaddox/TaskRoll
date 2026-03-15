package com.nshaddox.randomtask.ui.screens.tasklist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nshaddox.randomtask.R
import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.ui.components.DueDatePickerDialog
import com.nshaddox.randomtask.ui.theme.Spacing
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Dialog for adding or editing a task. When [task] is null the dialog is in "add" mode;
 * when non-null it is in "edit" mode with fields pre-populated from the task.
 *
 * @param task The task to edit, or null for add mode.
 * @param onConfirm Called with title, optional description, priority, optional due date,
 *   and optional category when the user taps the confirm button.
 * @param onDismiss Called when the user cancels or dismisses the dialog.
 * @param initialDueDate The raw due date for pre-population in edit mode. Null when not set.
 * @param modifier Modifier for customization.
 */
@Suppress("LongParameterList")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskDialog(
    task: TaskUiModel?,
    onConfirm: (String, String?, Priority, LocalDate?, String?) -> Unit,
    onDismiss: () -> Unit,
    initialDueDate: LocalDate? = null,
    modifier: Modifier = Modifier,
) {
    val isEditMode = task != null
    var title by remember { mutableStateOf(task?.title.orEmpty()) }
    var description by remember { mutableStateOf(task?.description.orEmpty()) }
    var priority by remember { mutableStateOf(task?.priority ?: Priority.MEDIUM) }
    var dueDate by remember { mutableStateOf(initialDueDate) }
    var category by remember { mutableStateOf(task?.category.orEmpty()) }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DueDatePickerDialog(
            initialDate = dueDate,
            onConfirm = { selectedDate ->
                dueDate = selectedDate
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { EditTaskDialogTitle(isEditMode) },
        text = {
            EditTaskDialogContent(
                title = title,
                onTitleChange = { title = it },
                description = description,
                onDescriptionChange = { description = it },
                priority = priority,
                onPriorityChange = { priority = it },
                dueDate = dueDate,
                onDueDateClick = { showDatePicker = true },
                category = category,
                onCategoryChange = { category = it },
            )
        },
        confirmButton = {
            EditTaskDialogConfirmButton(
                isEditMode = isEditMode,
                enabled = title.isNotBlank(),
                onClick = {
                    onConfirm(
                        title.trim(),
                        description.trim().ifBlank { null },
                        priority,
                        dueDate,
                        category.trim().ifBlank { null },
                    )
                },
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.edit_task_dialog_button_cancel))
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun EditTaskDialogTitle(isEditMode: Boolean) {
    Text(
        if (isEditMode) {
            stringResource(R.string.edit_task_dialog_title_edit)
        } else {
            stringResource(R.string.edit_task_dialog_title_add)
        },
    )
}

@Composable
private fun EditTaskDialogConfirmButton(
    isEditMode: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    TextButton(onClick = onClick, enabled = enabled) {
        Text(
            if (isEditMode) {
                stringResource(R.string.edit_task_dialog_button_save)
            } else {
                stringResource(R.string.edit_task_dialog_button_add)
            },
        )
    }
}

@Suppress("LongParameterList")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTaskDialogContent(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    priority: Priority,
    onPriorityChange: (Priority) -> Unit,
    dueDate: LocalDate?,
    onDueDateClick: () -> Unit,
    category: String,
    onCategoryChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
        TextField(
            value = title,
            onValueChange = onTitleChange,
            placeholder = { Text(stringResource(R.string.edit_task_dialog_placeholder_title)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        TextField(
            value = description,
            onValueChange = onDescriptionChange,
            placeholder = {
                Text(stringResource(R.string.edit_task_dialog_placeholder_description))
            },
            singleLine = false,
            minLines = 2,
            modifier = Modifier.fillMaxWidth(),
        )
        PrioritySelector(selected = priority, onSelect = onPriorityChange)
        DueDateField(dueDate = dueDate, onClick = onDueDateClick)
        TextField(
            value = category,
            onValueChange = onCategoryChange,
            placeholder = {
                Text(stringResource(R.string.edit_task_dialog_placeholder_category))
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrioritySelector(
    selected: Priority,
    onSelect: (Priority) -> Unit,
) {
    val options = Priority.entries
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = selected == option,
                onClick = { onSelect(option) },
                shape =
                    SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = options.size,
                    ),
            ) {
                Text(priorityLabel(option))
            }
        }
    }
}

@Composable
private fun priorityLabel(priority: Priority): String =
    when (priority) {
        Priority.LOW -> stringResource(R.string.edit_task_dialog_priority_low)
        Priority.MEDIUM -> stringResource(R.string.edit_task_dialog_priority_medium)
        Priority.HIGH -> stringResource(R.string.edit_task_dialog_priority_high)
    }

@Composable
private fun DueDateField(
    dueDate: LocalDate?,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
    ) {
        OutlinedTextField(
            value =
                dueDate?.format(
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM),
                ) ?: stringResource(R.string.edit_task_dialog_placeholder_due_date),
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text(stringResource(R.string.edit_task_dialog_field_due_date)) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
