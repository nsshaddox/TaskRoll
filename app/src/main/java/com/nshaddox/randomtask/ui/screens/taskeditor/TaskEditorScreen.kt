package com.nshaddox.randomtask.ui.screens.taskeditor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nshaddox.randomtask.R
import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.SubTask
import com.nshaddox.randomtask.ui.components.DueDatePickerDialog
import com.nshaddox.randomtask.ui.components.ThemedCheckbox
import com.nshaddox.randomtask.ui.theme.Spacing
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * ViewModel-connected wrapper for editing an existing task.
 */
@Composable
fun EditTaskScreen(
    navController: NavController,
    viewModel: TaskEditorViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            navController.popBackStack()
        }
    }

    val errorResId = uiState.errorResId
    val resolvedErrorMessage = errorResId?.let { stringResource(it) }
    LaunchedEffect(resolvedErrorMessage) {
        if (resolvedErrorMessage != null) {
            snackbarHostState.showSnackbar(resolvedErrorMessage)
            viewModel.clearError()
        }
    }

    if (uiState.showDatePicker) {
        DueDatePickerDialog(
            initialDate = uiState.taskDueDate?.let { LocalDate.ofEpochDay(it) },
            onConfirm = { selectedDate ->
                viewModel.onDueDateChange(selectedDate?.toEpochDay())
                viewModel.hideDatePicker()
            },
            onDismiss = { viewModel.hideDatePicker() },
        )
    }

    TaskEditorScreen(
        taskTitle = uiState.taskTitle,
        taskDescription = uiState.taskDescription,
        taskPriority = uiState.taskPriority,
        taskDueDate = uiState.taskDueDate,
        taskCategory = uiState.taskCategory,
        isEditMode = true,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onPriorityChange = viewModel::onPriorityChange,
        onDueDateClick = viewModel::showDatePicker,
        onCategoryChange = viewModel::onCategoryChange,
        onSaveClick = viewModel::saveTask,
        onCancelClick = { navController.popBackStack() },
        snackbarHostState = snackbarHostState,
        subTasks = uiState.subTasks,
        isAddingSubTask = uiState.isAddingSubTask,
        newSubTaskTitle = uiState.newSubTaskTitle,
        onToggleSubTask = viewModel::toggleSubTask,
        onAddSubTask = viewModel::addSubTask,
        onDeleteSubTask = viewModel::deleteSubTask,
        onNewSubTaskTitleChange = viewModel::onNewSubTaskTitleChange,
        onShowAddSubTask = viewModel::showAddSubTask,
        onHideAddSubTask = viewModel::hideAddSubTask,
    )
}

/**
 * Task Editor Screen - Form for creating or editing a task with all fields.
 */
@Suppress("LongMethod", "LongParameterList")
@Composable
fun TaskEditorScreen(
    taskTitle: String,
    taskDescription: String = "",
    taskPriority: Priority = Priority.MEDIUM,
    taskDueDate: Long? = null,
    taskCategory: String = "",
    isEditMode: Boolean = false,
    onTitleChange: (String) -> Unit = {},
    onDescriptionChange: (String) -> Unit = {},
    onPriorityChange: (Priority) -> Unit = {},
    onDueDateClick: () -> Unit = {},
    onCategoryChange: (String) -> Unit = {},
    onSaveClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    subTasks: List<SubTask> = emptyList(),
    isAddingSubTask: Boolean = false,
    newSubTaskTitle: String = "",
    onToggleSubTask: (SubTask) -> Unit = {},
    onAddSubTask: () -> Unit = {},
    onDeleteSubTask: (Long) -> Unit = {},
    onNewSubTaskTitleChange: (String) -> Unit = {},
    onShowAddSubTask: () -> Unit = {},
    onHideAddSubTask: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .statusBarsPadding()
                        .padding(horizontal = Spacing.small, vertical = Spacing.small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onCancelClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_navigate_back),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = if (isEditMode) "Edit Task" else "New Task",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = Spacing.small),
                )
            }
        },
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(Spacing.medium)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            // Title field
            TextField(
                value = taskTitle,
                onValueChange = onTitleChange,
                placeholder = { Text(stringResource(R.string.edit_task_dialog_placeholder_title)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            // Description field
            TextField(
                value = taskDescription,
                onValueChange = onDescriptionChange,
                placeholder = { Text(stringResource(R.string.edit_task_dialog_placeholder_description)) },
                singleLine = false,
                minLines = 2,
                modifier = Modifier.fillMaxWidth(),
            )

            // Priority selector
            EditorPrioritySelector(
                selected = taskPriority,
                onSelect = onPriorityChange,
            )

            // Due date field
            EditorDueDateField(
                dueDate = taskDueDate,
                onClick = onDueDateClick,
            )

            // Category field
            TextField(
                value = taskCategory,
                onValueChange = onCategoryChange,
                placeholder = { Text(stringResource(R.string.edit_task_dialog_placeholder_category)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            // Subtasks section (only in edit mode)
            if (isEditMode) {
                EditorSubTaskSection(
                    subTasks = subTasks,
                    isAddingSubTask = isAddingSubTask,
                    newSubTaskTitle = newSubTaskTitle,
                    onToggleSubTask = onToggleSubTask,
                    onAddSubTask = onAddSubTask,
                    onDeleteSubTask = onDeleteSubTask,
                    onNewSubTaskTitleChange = onNewSubTaskTitleChange,
                    onShowAddSubTask = onShowAddSubTask,
                    onHideAddSubTask = onHideAddSubTask,
                )
            }

            Spacer(modifier = Modifier.height(Spacing.small))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.componentPadding),
            ) {
                OutlinedButton(
                    onClick = onCancelClick,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = onSaveClick,
                    modifier = Modifier.weight(1f),
                    enabled = taskTitle.isNotBlank(),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                ) {
                    Text(if (isEditMode) "Save" else "Create")
                }
            }

            if (taskTitle.isBlank()) {
                Text(
                    text = "Task title cannot be empty",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorPrioritySelector(
    selected: Priority,
    onSelect: (Priority) -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = Priority.entries
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
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
                Text(
                    when (option) {
                        Priority.LOW -> stringResource(R.string.edit_task_dialog_priority_low)
                        Priority.MEDIUM -> stringResource(R.string.edit_task_dialog_priority_medium)
                        Priority.HIGH -> stringResource(R.string.edit_task_dialog_priority_high)
                    },
                )
            }
        }
    }
}

@Composable
private fun EditorDueDateField(
    dueDate: Long?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
    ) {
        OutlinedTextField(
            value =
                dueDate?.let {
                    LocalDate.ofEpochDay(it).format(
                        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM),
                    )
                } ?: stringResource(R.string.edit_task_dialog_placeholder_due_date),
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text(stringResource(R.string.edit_task_dialog_field_due_date)) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Suppress("LongParameterList", "LongMethod")
@Composable
private fun EditorSubTaskSection(
    subTasks: List<SubTask>,
    isAddingSubTask: Boolean,
    newSubTaskTitle: String,
    onToggleSubTask: (SubTask) -> Unit,
    onAddSubTask: () -> Unit,
    onDeleteSubTask: (Long) -> Unit,
    onNewSubTaskTitleChange: (String) -> Unit,
    onShowAddSubTask: () -> Unit,
    onHideAddSubTask: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
    ) {
        Text(
            text = stringResource(R.string.subtasks_section_title),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )

        if (subTasks.isNotEmpty()) {
            val completedCount = subTasks.count { it.isCompleted }
            Text(
                text = stringResource(R.string.subtask_progress, completedCount, subTasks.size),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        subTasks.forEach { subTask ->
            EditorSubTaskItem(
                subTask = subTask,
                onToggleSubTask = onToggleSubTask,
                onDeleteSubTask = onDeleteSubTask,
            )
        }

        if (isAddingSubTask) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                OutlinedTextField(
                    value = newSubTaskTitle,
                    onValueChange = onNewSubTaskTitleChange,
                    placeholder = { Text(stringResource(R.string.subtask_placeholder)) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    textStyle = MaterialTheme.typography.bodyMedium,
                )
                IconButton(onClick = onAddSubTask) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(R.string.cd_confirm_subtask),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                IconButton(onClick = onHideAddSubTask) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.cd_cancel_subtask),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            TextButton(onClick = onShowAddSubTask) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.cd_add_subtask),
                    modifier = Modifier.padding(end = Spacing.extraSmall),
                )
                Text(stringResource(R.string.add_subtask))
            }
        }
    }
}

@Composable
private fun EditorSubTaskItem(
    subTask: SubTask,
    onToggleSubTask: (SubTask) -> Unit,
    onDeleteSubTask: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
    ) {
        ThemedCheckbox(
            checked = subTask.isCompleted,
            onCheckedChange = { onToggleSubTask(subTask) },
            contentDescription = stringResource(R.string.cd_toggle_subtask, subTask.title),
        )
        Text(
            text = subTask.title,
            style = MaterialTheme.typography.bodyMedium,
            color =
                if (subTask.isCompleted) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            textDecoration =
                if (subTask.isCompleted) {
                    TextDecoration.LineThrough
                } else {
                    TextDecoration.None
                },
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = { onDeleteSubTask(subTask.id) }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.cd_delete_subtask, subTask.title),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
