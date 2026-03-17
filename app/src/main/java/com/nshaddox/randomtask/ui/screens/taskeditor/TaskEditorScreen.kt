package com.nshaddox.randomtask.ui.screens.taskeditor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nshaddox.randomtask.ui.theme.Spacing

/**
 * ViewModel-connected wrapper for editing an existing task.
 *
 * Collects UI state from [TaskEditorViewModel] and delegates to the stateless [TaskEditorScreen].
 * Navigates back when the task is saved successfully.
 *
 * @param navController Navigation controller for screen transitions.
 * @param viewModel The TaskEditorViewModel instance, provided by Hilt.
 */
@Composable
fun EditTaskScreen(
    navController: NavController,
    viewModel: TaskEditorViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            navController.popBackStack()
        }
    }

    TaskEditorScreen(
        taskTitle = uiState.taskTitle,
        isEditMode = true,
        onTitleChange = viewModel::onTitleChange,
        onSaveClick = viewModel::saveTask,
        onCancelClick = { navController.popBackStack() },
    )
}

/**
 * Task Editor Screen - Form for creating or editing a task
 *
 * @param taskTitle Current title of the task being edited
 * @param isEditMode True if editing existing task, false if creating new task
 * @param onTitleChange Callback when task title text changes
 * @param onSaveClick Callback when save button is clicked
 * @param onCancelClick Callback when cancel/back button is clicked
 * @param modifier Modifier for customization
 */
@Suppress("LongMethod")
@Composable
fun TaskEditorScreen(
    taskTitle: String,
    isEditMode: Boolean = false,
    onTitleChange: (String) -> Unit = {},
    onSaveClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .statusBarsPadding()
                        .padding(horizontal = Spacing.small, vertical = Spacing.small),
            ) {
                IconButton(onClick = onCancelClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
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
                    .padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            Text(
                text =
                    if (isEditMode) {
                        "Edit the task details below"
                    } else {
                        "Enter the task details below"
                    },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            OutlinedTextField(
                value = taskTitle,
                onValueChange = onTitleChange,
                label = { Text("Task Title") },
                placeholder = { Text("e.g., Complete project documentation") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 2,
                maxLines = 4,
            )

            Spacer(modifier = Modifier.height(Spacing.small))

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
