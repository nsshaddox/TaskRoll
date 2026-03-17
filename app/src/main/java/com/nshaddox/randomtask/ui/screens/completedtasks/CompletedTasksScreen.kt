package com.nshaddox.randomtask.ui.screens.completedtasks

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.ui.components.ThemedCard
import com.nshaddox.randomtask.ui.components.ThemedPriorityBadge
import com.nshaddox.randomtask.ui.theme.Spacing
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Formats an epoch millisecond timestamp into a human-readable date string.
 *
 * Uses the system default time zone and a "MMM dd, yyyy" pattern
 * (e.g., "Nov 14, 2023"). Compatible with API 24+ (no java.time dependency).
 *
 * @param epochMillis The timestamp in epoch milliseconds.
 * @param locale The locale to use for formatting. Defaults to [Locale.getDefault].
 * @return A formatted date string.
 */
fun formatCompletedDate(
    epochMillis: Long,
    locale: Locale = Locale.getDefault(),
): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy", locale)
    return formatter.format(Date(epochMillis))
}

/**
 * Represents the visual content state of the Completed Tasks screen.
 */
sealed interface CompletedTasksContentState {
    data object Loading : CompletedTasksContentState

    data object Empty : CompletedTasksContentState

    data class Populated(val tasks: List<Task>) : CompletedTasksContentState
}

/**
 * Determines the content state to render from the given screen parameters.
 */
fun resolveContentState(
    isLoading: Boolean,
    tasks: List<Task>,
): CompletedTasksContentState =
    when {
        isLoading -> CompletedTasksContentState.Loading
        tasks.isEmpty() -> CompletedTasksContentState.Empty
        else -> CompletedTasksContentState.Populated(tasks)
    }

/**
 * Stateful Completed Tasks Screen that integrates with ViewModel and NavController.
 */
@Composable
fun CompletedTasksScreen(
    navController: NavController,
    viewModel: CompletedTasksViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

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

    CompletedTasksScreen(
        tasks = uiState.tasks,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        onDeleteTask = { task -> viewModel.deleteTaskWithUndo(task) },
        onNavigateBack = { navController.popBackStack() },
        onClearError = { viewModel.clearError() },
        snackbarHostState = snackbarHostState,
    )
}

/**
 * Stateless Completed Tasks Screen content.
 */
@Suppress("LongParameterList", "LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompletedTasksScreen(
    tasks: List<Task>,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onDeleteTask: (Task) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onClearError: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            snackbarHostState.showSnackbar(errorMessage)
            onClearError()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = Spacing.small, vertical = Spacing.small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_navigate_back),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = stringResource(R.string.completed_tasks_screen_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier,
    ) { innerPadding ->
        when (val contentState = resolveContentState(isLoading, tasks)) {
            is CompletedTasksContentState.Loading -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            is CompletedTasksContentState.Empty -> {
                EmptyCompletedTasksContent(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                )
            }

            is CompletedTasksContentState.Populated -> {
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = Spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(Spacing.small),
                ) {
                    items(contentState.tasks, key = { it.id }) { task ->
                        CompletedTaskItem(
                            task = task,
                            onDelete = { onDeleteTask(task) },
                        )
                    }
                }
            }
        }
    }
}

/**
 * A single completed task item with swipe-to-delete support.
 */
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompletedTaskItem(
    task: Task,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dismissState =
        rememberSwipeToDismissBoxState(
            confirmValueChange = { value ->
                if (value == SwipeToDismissBoxValue.EndToStart) {
                    onDelete()
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
                        SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
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
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        },
        modifier = modifier,
        enableDismissFromStartToEnd = false,
    ) {
        ThemedCard(
            priority = task.priority,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.extraSmall),
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textDecoration = TextDecoration.LineThrough,
                    )
                    Text(
                        text = formatCompletedDate(task.updatedAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                ThemedPriorityBadge(
                    priority = task.priority,
                    modifier = Modifier.padding(start = Spacing.small),
                )
            }
        }
    }
}

/**
 * Empty state content displayed when there are no completed tasks.
 */
@Composable
private fun EmptyCompletedTasksContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            Text(
                text = stringResource(R.string.completed_tasks_empty_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(R.string.completed_tasks_empty_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
