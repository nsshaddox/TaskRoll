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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nshaddox.randomtask.R
import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.ui.components.PriorityBadge
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
 *
 * Used to determine which composable content to render based on
 * [CompletedTasksUiState] values.
 */
sealed interface CompletedTasksContentState {
    /** A loading operation is in progress. */
    data object Loading : CompletedTasksContentState

    /** No completed tasks to display. */
    data object Empty : CompletedTasksContentState

    /** Completed tasks are available. */
    data class Populated(val tasks: List<Task>) : CompletedTasksContentState
}

/**
 * Determines the content state to render from the given screen parameters.
 *
 * Priority: loading takes precedence, then empty check, then populated.
 *
 * @param isLoading Whether a loading operation is in progress.
 * @param tasks The list of completed tasks.
 * @return The resolved [CompletedTasksContentState].
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
 *
 * Collects UI state from [CompletedTasksViewModel] and delegates to the stateless composable.
 *
 * @param navController Navigation controller for back navigation.
 * @param viewModel Hilt-injected ViewModel managing completed tasks state.
 */
@Composable
fun CompletedTasksScreen(
    navController: NavController,
    viewModel: CompletedTasksViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    CompletedTasksScreen(
        tasks = uiState.tasks,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        onDeleteTask = { task -> viewModel.deleteTask(task) },
        onNavigateBack = { navController.popBackStack() },
        onClearError = { viewModel.clearError() },
        snackbarHostState = snackbarHostState,
    )
}

/**
 * Stateless Completed Tasks Screen content.
 *
 * Displays a list of completed tasks with swipe-to-delete functionality,
 * a loading indicator, or an empty state message.
 *
 * @param tasks List of completed tasks to display.
 * @param isLoading Whether a loading operation is in progress.
 * @param errorMessage An optional error message to display via snackbar.
 * @param onDeleteTask Callback when a task is swiped to delete.
 * @param onNavigateBack Callback for back navigation.
 * @param onClearError Callback to clear the current error message.
 * @param snackbarHostState State for the snackbar host.
 * @param modifier Modifier for customization.
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
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.completed_tasks_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_navigate_back),
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
            )
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
                    CircularProgressIndicator()
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
 *
 * Wraps the task card in a [SwipeToDismissBox] that triggers the
 * [onDelete] callback when swiped from end to start.
 *
 * @param task The completed task to display.
 * @param onDelete Callback invoked when the item is swiped to dismiss.
 * @param modifier Modifier for customization.
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
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
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
                    )
                    Text(
                        text = formatCompletedDate(task.updatedAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                PriorityBadge(
                    priority = task.priority,
                    modifier = Modifier.padding(start = Spacing.small),
                )
            }
        }
    }
}

/**
 * Empty state content displayed when there are no completed tasks.
 *
 * @param modifier Modifier for customization.
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
