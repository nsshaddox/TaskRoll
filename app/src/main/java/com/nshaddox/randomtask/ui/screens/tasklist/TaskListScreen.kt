package com.nshaddox.randomtask.ui.screens.tasklist

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nshaddox.randomtask.R
import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.SortOrder
import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.ui.components.PriorityBadge
import com.nshaddox.randomtask.ui.navigation.Screen
import com.nshaddox.randomtask.ui.theme.Spacing
import java.time.LocalDate

/**
 * ViewModel-connected TaskListScreen wrapper.
 *
 * Collects UI state from [TaskListViewModel] and delegates to the stateless composable.
 * Converts domain [Task] objects to [TaskUiModel] for display.
 *
 * @param navController Navigation controller for screen transitions.
 * @param viewModel The TaskListViewModel instance, provided by Hilt.
 */
@Suppress("LongMethod")
@Composable
fun TaskListScreen(
    navController: NavController,
    viewModel: TaskListViewModel = hiltViewModel(),
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

    val currentEpochDay = LocalDate.now().toEpochDay()
    val taskUiModels =
        remember(uiState.tasks, currentEpochDay) {
            uiState.tasks.map { it.toUiModel(currentEpochDay) }
        }

    if (uiState.isEditDialogVisible || uiState.isAddDialogVisible) {
        val editingTask = uiState.editingTask
        val initialDueDate =
            editingTask?.let { task ->
                uiState.tasks.find { it.id == task.id }?.dueDate?.let {
                    LocalDate.ofEpochDay(it)
                }
            }
        EditTaskDialog(
            task = editingTask,
            onConfirm = { title, description, priority, dueDate, category ->
                if (editingTask != null) {
                    viewModel.editTask(
                        taskId = editingTask.id,
                        title = title,
                        description = description,
                        priority = priority,
                        dueDate = dueDate?.toEpochDay(),
                        category = category,
                    )
                } else {
                    viewModel.addTask(
                        title = title,
                        description = description,
                        priority = priority,
                        dueDate = dueDate?.toEpochDay(),
                        category = category,
                    )
                    viewModel.hideAddDialog()
                }
            },
            onDismiss = {
                if (editingTask != null) {
                    viewModel.hideEditDialog()
                } else {
                    viewModel.hideAddDialog()
                }
            },
            initialDueDate = initialDueDate,
        )
    }

    // Undo-delete Snackbar
    val pendingDeleteTask = uiState.pendingDeleteTask
    val taskDeletedMessage = stringResource(R.string.snackbar_task_deleted)
    val undoLabel = stringResource(R.string.snackbar_undo_action)
    LaunchedEffect(pendingDeleteTask) {
        if (pendingDeleteTask != null) {
            val result =
                snackbarHostState.showSnackbar(
                    message = taskDeletedMessage,
                    actionLabel = undoLabel,
                    duration = SnackbarDuration.Short,
                )
            when (result) {
                SnackbarResult.ActionPerformed -> viewModel.undoDelete()
                SnackbarResult.Dismissed -> viewModel.confirmDelete()
            }
        }
    }

    TaskListScreen(
        tasks = taskUiModels,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        snackbarHostState = snackbarHostState,
        searchQuery = uiState.searchQuery,
        filterPriority = uiState.filterPriority,
        filterCategory = uiState.filterCategory,
        sortOrder = uiState.sortOrder,
        availableCategories = uiState.availableCategories,
        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
        onPriorityFilterChange = { viewModel.setFilterPriority(it) },
        onCategoryFilterChange = { viewModel.setFilterCategory(it) },
        onSortOrderChange = { viewModel.setSortOrder(it) },
        onClearError = { viewModel.clearError() },
        onTaskClick = {},
        onTaskCheckedChange = { taskUiModel, _ ->
            uiState.tasks.find { it.id == taskUiModel.id }?.let { task ->
                viewModel.toggleTaskCompletion(task)
            }
        },
        onDeleteTask = { taskUiModel ->
            uiState.tasks.find { it.id == taskUiModel.id }?.let { task ->
                viewModel.deleteTaskWithUndo(task)
            }
        },
        onEditTask = { taskUiModel -> viewModel.showEditDialog(taskUiModel) },
        onAddTask = { viewModel.showAddDialog() },
        onNavigateToRandomTask = { navController.navigate(Screen.RandomTask.route) },
        onNavigateToCompletedTasks = { navController.navigate(Screen.CompletedTasks.route) },
        onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
    )
}

/**
 * Task List Screen - Displays a list of tasks with CRUD operations, filter bar, and v2 fields.
 *
 * @param tasks List of task UI models to display.
 * @param isLoading Whether a loading operation is in progress.
 * @param errorMessage An optional error message to display.
 * @param snackbarHostState The snackbar host state for displaying messages.
 * @param searchQuery The current search text.
 * @param filterPriority The currently selected priority filter, or null for "All".
 * @param filterCategory The currently selected category filter, or null for "All".
 * @param sortOrder The current sort ordering.
 * @param availableCategories The list of categories for the filter dropdown.
 * @param onSearchQueryChange Called when the search text changes.
 * @param onPriorityFilterChange Called when a priority filter is selected.
 * @param onCategoryFilterChange Called when a category filter is selected.
 * @param onSortOrderChange Called when a sort order is selected.
 * @param onClearError Callback to clear the current error message.
 * @param onTaskClick Callback when a task is clicked.
 * @param onTaskCheckedChange Callback when task completion status changes.
 * @param onDeleteTask Callback when delete button or swipe-to-delete is triggered.
 * @param onEditTask Callback when edit button is clicked.
 * @param onAddTask Callback when FAB is clicked to add new task.
 * @param onNavigateToRandomTask Callback when random task navigation button is clicked.
 * @param onNavigateToCompletedTasks Callback when completed tasks history button is clicked.
 * @param onNavigateToSettings Callback when settings navigation button is clicked.
 * @param modifier Modifier for customization.
 */
@Suppress("LongParameterList", "LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    tasks: List<TaskUiModel>,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    searchQuery: String = "",
    filterPriority: Priority? = null,
    filterCategory: String? = null,
    sortOrder: SortOrder = SortOrder.CREATED_DATE_DESC,
    availableCategories: List<String> = emptyList(),
    onSearchQueryChange: (String) -> Unit = {},
    onPriorityFilterChange: (Priority?) -> Unit = {},
    onCategoryFilterChange: (String?) -> Unit = {},
    onSortOrderChange: (SortOrder) -> Unit = {},
    onClearError: () -> Unit = {},
    onTaskClick: (TaskUiModel) -> Unit = {},
    onTaskCheckedChange: (TaskUiModel, Boolean) -> Unit = { _, _ -> },
    onDeleteTask: (TaskUiModel) -> Unit = {},
    onEditTask: (TaskUiModel) -> Unit = {},
    onAddTask: () -> Unit = {},
    onNavigateToRandomTask: () -> Unit = {},
    onNavigateToCompletedTasks: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    modifier: Modifier = Modifier,
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
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                actions = {
                    IconButton(onClick = onNavigateToCompletedTasks) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = stringResource(R.string.cd_navigate_to_completed_tasks),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                    IconButton(onClick = onNavigateToRandomTask) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Random Task",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.navigate_to_settings),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTask) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task",
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier,
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else if (tasks.isEmpty() && !hasActiveFilters(searchQuery, filterPriority, filterCategory)) {
            EmptyTaskListContent(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
            )
        } else {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
            ) {
                TaskFilterBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = onSearchQueryChange,
                    filterPriority = filterPriority,
                    onPriorityFilterChange = onPriorityFilterChange,
                    filterCategory = filterCategory,
                    onCategoryFilterChange = onCategoryFilterChange,
                    sortOrder = sortOrder,
                    onSortOrderChange = onSortOrderChange,
                    availableCategories = availableCategories,
                )
                if (tasks.isEmpty()) {
                    EmptyTaskListContent(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .weight(1f),
                    )
                } else {
                    LazyColumn(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(horizontal = Spacing.medium),
                        verticalArrangement = Arrangement.spacedBy(Spacing.small),
                    ) {
                        items(tasks, key = { it.id }) { task ->
                            SwipeToDismissTaskItem(
                                task = task,
                                onTaskClick = { onTaskClick(task) },
                                onCheckedChange = { checked -> onTaskCheckedChange(task, checked) },
                                onEditClick = { onEditTask(task) },
                                onDeleteClick = { onDeleteTask(task) },
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * A task list item wrapped in a [SwipeToDismissBox] for swipe-to-delete.
 *
 * Swiping from end to start reveals a red background with a delete icon.
 * On full swipe, the [onDeleteClick] callback is invoked.
 *
 * @param task The task UI model to display.
 * @param onTaskClick Callback when the task card is clicked.
 * @param onCheckedChange Callback when the task checkbox state changes.
 * @param onEditClick Callback when the edit button is clicked.
 * @param onDeleteClick Callback when the item is swiped to delete or the delete button is clicked.
 * @param modifier Modifier for customization.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDismissTaskItem(
    task: TaskUiModel,
    onTaskClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dismissState =
        rememberSwipeToDismissBoxState(
            confirmValueChange = { value ->
                if (value == SwipeToDismissBoxValue.EndToStart) {
                    onDeleteClick()
                    true
                } else {
                    false
                }
            },
        )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color by animateColorAsState(
                targetValue =
                    when (dismissState.targetValue) {
                        SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                        else -> Color.Transparent
                    },
                label = "dismissBackground",
            )
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(color)
                        .padding(horizontal = Spacing.medium),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.cd_swipe_to_delete),
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        },
        modifier = modifier,
        enableDismissFromStartToEnd = false,
    ) {
        TaskListItem(
            task = task,
            onTaskClick = onTaskClick,
            onCheckedChange = onCheckedChange,
            onEditClick = onEditClick,
            onDeleteClick = onDeleteClick,
        )
    }
}

@Suppress("LongMethod")
@Composable
internal fun TaskListItem(
    task: TaskUiModel,
    onTaskClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onTaskClick,
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (task.isCompleted) {
                        MaterialTheme.colorScheme.surfaceVariant
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(Spacing.componentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = onCheckedChange,
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.extraSmall),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                    ) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.bodyLarge,
                            textDecoration =
                                if (task.isCompleted) {
                                    TextDecoration.LineThrough
                                } else {
                                    TextDecoration.None
                                },
                            color =
                                if (task.isCompleted) {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false),
                        )
                        PriorityBadge(priority = task.priority)
                    }
                    TaskMetadataRow(task = task)
                }
            }
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.cd_edit_task),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.cd_delete_task),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun TaskMetadataRow(task: TaskUiModel) {
    val hasDueDate = task.dueDateLabel != null
    val hasCategory = task.category != null

    if (!hasDueDate && !hasCategory) return

    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (hasDueDate) {
            Text(
                text = task.dueDateLabel!!,
                style = MaterialTheme.typography.bodySmall,
                color =
                    if (task.isOverdue) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
            )
        }
        if (hasCategory) {
            Text(
                text = task.category!!,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * Returns true when any filter is actively applied (search, priority, or category).
 */
private fun hasActiveFilters(
    searchQuery: String,
    filterPriority: Priority?,
    filterCategory: String?,
): Boolean = searchQuery.isNotEmpty() || filterPriority != null || filterCategory != null

@Composable
private fun EmptyTaskListContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            Text(
                text = "No tasks yet",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Tap + to add your first task",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
