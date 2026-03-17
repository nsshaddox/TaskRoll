package com.nshaddox.randomtask.ui.screens.randomtask

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nshaddox.randomtask.R
import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.ui.components.ThemedCard
import com.nshaddox.randomtask.ui.components.ThemedPriorityBadge
import com.nshaddox.randomtask.ui.theme.Spacing

/**
 * Stateful Random Task Screen that integrates with ViewModel and NavController.
 *
 * @param navController Navigation controller for back navigation
 * @param viewModel Hilt-injected ViewModel managing random task state
 */
@Composable
fun RandomTaskScreen(
    navController: NavController,
    viewModel: RandomTaskViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val useWeightedRandom by viewModel.useWeightedRandom.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadRandomTask()
    }

    LaunchedEffect(uiState.taskCompleted) {
        if (uiState.taskCompleted) {
            navController.previousBackStackEntry?.savedStateHandle?.set("task_completed", true)
            navController.popBackStack()
            viewModel.resetTaskCompleted()
        }
    }

    RandomTaskScreenContent(
        uiState = uiState,
        onSelectRandom = viewModel::loadRandomTask,
        onCompleteTask = viewModel::completeTask,
        onSkipTask = viewModel::skipTask,
        onBackClick = { navController.popBackStack() },
        useWeightedRandom = useWeightedRandom,
        onToggleWeightedRandom = viewModel::toggleWeightedRandom,
    )
}

/**
 * Stateless Random Task Screen content for preview support.
 */
@Suppress("LongParameterList")
@Composable
internal fun RandomTaskScreenContent(
    uiState: RandomTaskUiState,
    onSelectRandom: () -> Unit = {},
    onCompleteTask: () -> Unit = {},
    onSkipTask: () -> Unit = {},
    onBackClick: () -> Unit = {},
    useWeightedRandom: Boolean = false,
    onToggleWeightedRandom: () -> Unit = {},
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
                        .padding(horizontal = Spacing.small, vertical = Spacing.small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = "Random Task",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
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
        ) {
            WeightedRandomToggle(
                useWeightedRandom = useWeightedRandom,
                onToggleWeightedRandom = onToggleWeightedRandom,
            )
            RandomTaskContent(
                uiState = uiState,
                onSelectRandom = onSelectRandom,
                onCompleteTask = onCompleteTask,
                onSkipTask = onSkipTask,
                onBackClick = onBackClick,
            )
        }
    }
}

@Composable
private fun WeightedRandomToggle(
    useWeightedRandom: Boolean,
    onToggleWeightedRandom: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.weighted_random_toggle_label),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Switch(
            checked = useWeightedRandom,
            onCheckedChange = { onToggleWeightedRandom() },
        )
    }
}

@Composable
private fun RandomTaskContent(
    uiState: RandomTaskUiState,
    onSelectRandom: () -> Unit,
    onCompleteTask: () -> Unit,
    onSkipTask: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            uiState.error != null -> {
                ErrorContent(
                    error = uiState.error,
                    onRetry = onSelectRandom,
                )
            }
            uiState.noTasksAvailable -> {
                NoTasksAvailableContent(
                    onBackClick = onBackClick,
                )
            }
            uiState.currentTask != null -> {
                SelectedTaskContent(
                    task = uiState.currentTask,
                    onSelectRandom = onSelectRandom,
                    onCompleteTask = onCompleteTask,
                    onSkipTask = onSkipTask,
                )
            }
            else -> {
                NoTaskSelectedContent(
                    onSelectRandom = onSelectRandom,
                )
            }
        }
    }
}

@Composable
private fun NoTaskSelectedContent(
    onSelectRandom: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        Text(
            text = "Ready to tackle a task?",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = "Tap the button below to randomly select a task from your list",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(Spacing.medium))
        Button(
            onClick = onSelectRandom,
            modifier = Modifier.fillMaxWidth(0.7f),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.padding(end = Spacing.small),
            )
            Text("Pick Random Task")
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error,
        )
        OutlinedButton(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
private fun NoTasksAvailableContent(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        Text(
            text = "No tasks available. Add some tasks first!",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
        )
        OutlinedButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                modifier = Modifier.padding(end = Spacing.small),
            )
            Text("Go Back")
        }
    }
}

@Composable
private fun SelectedTaskContent(
    task: Task,
    onSelectRandom: () -> Unit,
    onCompleteTask: () -> Unit,
    onSkipTask: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.large),
    ) {
        Text(
            text = "Your Task:",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        ThemedCard(
            priority = task.priority,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    ThemedPriorityBadge(priority = task.priority)
                }
                if (task.description != null) {
                    Spacer(modifier = Modifier.height(Spacing.componentPadding))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.small))

        Column(
            modifier = Modifier.fillMaxWidth(0.8f),
            verticalArrangement = Arrangement.spacedBy(Spacing.componentPadding),
        ) {
            Button(
                onClick = onCompleteTask,
                modifier = Modifier.fillMaxWidth(),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    modifier = Modifier.padding(end = Spacing.small),
                )
                Text("Complete Task")
            }

            OutlinedButton(
                onClick = onSkipTask,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.padding(end = Spacing.small),
                )
                Text("Skip Task")
            }

            FilledTonalButton(
                onClick = onSelectRandom,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.padding(end = Spacing.small),
                )
                Text("Pick Another")
            }
        }
    }
}
