package com.nshaddox.randomtask.ui.screens.tasklist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.ui.navigation.Screen

/**
 * ViewModel-connected TaskListScreen wrapper.
 *
 * Collects UI state from [TaskListViewModel] and delegates to the stateless composable.
 *
 * @param navController Navigation controller for screen transitions.
 * @param viewModel The TaskListViewModel instance, provided by Hilt.
 */
@Composable
fun TaskListScreen(
    navController: NavController,
    viewModel: TaskListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val taskCompleted by savedStateHandle
        ?.getStateFlow<Boolean>("task_completed", false)
        ?.collectAsState() ?: remember { mutableStateOf(false) }

    LaunchedEffect(taskCompleted) {
        if (taskCompleted) {
            snackbarHostState.showSnackbar("Task completed!")
            savedStateHandle?.set("task_completed", false)
        }
    }

    if (uiState.isAddDialogVisible) {
        AddTaskDialog(
            onConfirm = { title -> viewModel.addTask(title) },
            onDismiss = { viewModel.hideAddDialog() }
        )
    }

    TaskListScreen(
        tasks = uiState.tasks,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        snackbarHostState = snackbarHostState,
        onClearError = { viewModel.clearError() },
        onTaskClick = {},
        onTaskCheckedChange = { task, _ -> viewModel.toggleTaskCompletion(task) },
        onDeleteTask = { task -> viewModel.deleteTask(task) },
        onEditTask = { task -> navController.navigate(Screen.EditTask.createRoute(task.id)) },
        onAddTask = { viewModel.showAddDialog() },
        onNavigateToRandomTask = { navController.navigate(Screen.RandomTask.route) }
    )
}

/**
 * Task List Screen - Displays a list of tasks with CRUD operations
 *
 * @param tasks List of tasks to display
 * @param isLoading Whether a loading operation is in progress
 * @param errorMessage An optional error message to display
 * @param onClearError Callback to clear the current error message
 * @param onTaskClick Callback when a task is clicked
 * @param onTaskCheckedChange Callback when task completion status changes
 * @param onDeleteTask Callback when delete button is clicked
 * @param onEditTask Callback when edit button is clicked
 * @param onAddTask Callback when FAB is clicked to add new task
 * @param onNavigateToRandomTask Callback when random task navigation button is clicked
 * @param modifier Modifier for customization
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    tasks: List<Task>,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onClearError: () -> Unit = {},
    onTaskClick: (Task) -> Unit = {},
    onTaskCheckedChange: (Task, Boolean) -> Unit = { _, _ -> },
    onDeleteTask: (Task) -> Unit = {},
    onEditTask: (Task) -> Unit = {},
    onAddTask: () -> Unit = {},
    onNavigateToRandomTask: () -> Unit = {},
    modifier: Modifier = Modifier
) {

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            val result = snackbarHostState.showSnackbar(errorMessage)
            if (result == SnackbarResult.Dismissed || result == SnackbarResult.ActionPerformed) {
                onClearError()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Tasks") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = onNavigateToRandomTask) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Random Task",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTask) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task"
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (tasks.isEmpty()) {
            EmptyTaskListContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onTaskClick = { onTaskClick(task) },
                        onCheckedChange = { checked -> onTaskCheckedChange(task, checked) },
                        onEditClick = { onEditTask(task) },
                        onDeleteClick = { onDeleteTask(task) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskCard(
    task: Task,
    onTaskClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onTaskClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = onCheckedChange
                )
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (task.isCompleted)
                        TextDecoration.LineThrough
                    else
                        TextDecoration.None,
                    color = if (task.isCompleted)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Task",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Task",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun EmptyTaskListContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "No tasks yet",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Tap + to add your first task",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
