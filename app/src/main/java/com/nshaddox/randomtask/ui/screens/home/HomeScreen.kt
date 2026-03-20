@file:Suppress("LongMethod", "LongParameterList", "TooManyFunctions")

package com.nshaddox.randomtask.ui.screens.home

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nshaddox.randomtask.R
import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.domain.model.TaskMetrics
import com.nshaddox.randomtask.ui.components.ThemedCard
import com.nshaddox.randomtask.ui.components.ThemedPriorityBadge
import com.nshaddox.randomtask.ui.navigation.Screen
import com.nshaddox.randomtask.ui.screens.tasklist.EditTaskDialog
import com.nshaddox.randomtask.ui.theme.LocalAppThemeTokens
import com.nshaddox.randomtask.ui.theme.Spacing

/**
 * Stateful Home Screen that integrates with ViewModel and NavController.
 *
 * @param navController Navigation controller for navigating to other screens.
 * @param viewModel Hilt-injected ViewModel managing home screen state.
 */
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.taskCompleted) {
        if (uiState.taskCompleted) {
            snackbarHostState.showSnackbar("Task completed!")
            viewModel.resetTaskCompleted()
        }
    }

    HomeScreenContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onPickRandom = viewModel::pickRandom,
        onCompleteTask = viewModel::completeTask,
        onSkipTask = viewModel::skipTask,
        onToggleWeightedRandom = viewModel::toggleWeightedRandom,
        onAddTask = { title, description, priority, dueDate, category ->
            viewModel.addTask(title, description, priority, dueDate, category)
        },
        onNavigateToTaskList = { navController.navigate(Screen.TaskList.route) },
        onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
    )
}

/**
 * Stateless Home Screen content for preview support.
 */
@Composable
internal fun HomeScreenContent(
    uiState: HomeUiState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onPickRandom: () -> Unit = {},
    onCompleteTask: () -> Unit = {},
    onSkipTask: () -> Unit = {},
    onToggleWeightedRandom: () -> Unit = {},
    onAddTask: (String, String?, Priority, Long?, String?) -> Unit = { _, _, _, _, _ -> },
    onNavigateToTaskList: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            HomeTopBar(
                onNavigateToSettings = onNavigateToSettings,
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = Spacing.medium)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            // Hero Section: Random Task
            HeroSection(
                uiState = uiState,
                onPickRandom = onPickRandom,
                onCompleteTask = onCompleteTask,
                onSkipTask = onSkipTask,
                onToggleWeightedRandom = onToggleWeightedRandom,
            )

            // Quick Actions
            QuickActionsSection(
                onAddTask = { showAddDialog = true },
                onViewAllTasks = onNavigateToTaskList,
            )

            // Metrics Dashboard
            MetricsDashboard(metrics = uiState.metrics)

            Spacer(modifier = Modifier.height(Spacing.medium))
        }
    }

    if (showAddDialog) {
        EditTaskDialog(
            task = null,
            onConfirm = { title, description, priority, dueDate, category ->
                onAddTask(title, description, priority, dueDate?.toEpochDay(), category)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false },
        )
    }
}

@Composable
private fun HomeTopBar(
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = LocalAppThemeTokens.current
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .padding(horizontal = Spacing.small, vertical = Spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        val titleText = stringResource(R.string.home_screen_title)
        Text(
            text = if (tokens.useUppercaseTitles) titleText.uppercase() else titleText,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        IconButton(onClick = onNavigateToSettings) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.navigate_to_settings),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun HeroSection(
    uiState: HomeUiState,
    onPickRandom: () -> Unit,
    onCompleteTask: () -> Unit,
    onSkipTask: () -> Unit,
    onToggleWeightedRandom: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = LocalAppThemeTokens.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.componentPadding),
    ) {
        val heroTitle = stringResource(R.string.home_hero_title)
        Text(
            text = if (tokens.useUppercaseTitles) heroTitle.uppercase() else heroTitle,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        WeightedRandomToggle(
            useWeightedRandom = uiState.useWeightedRandom,
            onToggleWeightedRandom = onToggleWeightedRandom,
        )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth().height(Spacing.extraLarge * 4),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            uiState.error != null -> {
                ErrorContent(error = uiState.error, onRetry = onPickRandom)
            }
            uiState.noTasksAvailable -> {
                NoTasksContent()
            }
            uiState.currentTask != null -> {
                TaskHeroCard(
                    task = uiState.currentTask,
                    onCompleteTask = onCompleteTask,
                    onSkipTask = onSkipTask,
                    onPickAnother = onPickRandom,
                )
            }
            else -> {
                PickRandomPrompt(onPickRandom = onPickRandom)
            }
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
private fun TaskHeroCard(
    task: Task,
    onCompleteTask: () -> Unit,
    onSkipTask: () -> Unit,
    onPickAnother: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ThemedCard(
        priority = task.priority,
        modifier = modifier.fillMaxWidth(),
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
        modifier = Modifier.fillMaxWidth(),
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
            Text(stringResource(R.string.home_complete_task))
        }

        OutlinedButton(
            onClick = onSkipTask,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.home_skip_task))
        }

        FilledTonalButton(
            onClick = onPickAnother,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.padding(end = Spacing.small),
            )
            Text(stringResource(R.string.home_pick_another))
        }
    }
}

@Composable
private fun PickRandomPrompt(
    onPickRandom: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        Text(
            text = stringResource(R.string.home_ready_prompt),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = stringResource(R.string.home_ready_body),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(Spacing.small))
        Button(
            onClick = onPickRandom,
            modifier = Modifier.fillMaxWidth(),
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
            Text(stringResource(R.string.home_pick_random))
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
        modifier = modifier.fillMaxWidth(),
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
            Text(stringResource(R.string.home_retry))
        }
    }
}

@Composable
private fun NoTasksContent(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.home_no_tasks),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
private fun QuickActionsSection(
    onAddTask: () -> Unit,
    onViewAllTasks: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = LocalAppThemeTokens.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.componentPadding),
    ) {
        val sectionTitle = stringResource(R.string.home_quick_actions_title)
        Text(
            text = if (tokens.useUppercaseTitles) sectionTitle.uppercase() else sectionTitle,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.componentPadding),
        ) {
            OutlinedButton(
                onClick = onAddTask,
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.padding(end = Spacing.small),
                )
                Text(stringResource(R.string.home_add_task))
            }
            OutlinedButton(
                onClick = onViewAllTasks,
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = null,
                    modifier = Modifier.padding(end = Spacing.small),
                )
                Text(stringResource(R.string.home_view_all_tasks))
            }
        }
    }
}

@Composable
private fun MetricsDashboard(
    metrics: TaskMetrics,
    modifier: Modifier = Modifier,
) {
    val tokens = LocalAppThemeTokens.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.componentPadding),
    ) {
        val metricsTitle = stringResource(R.string.home_metrics_title)
        Text(
            text = if (tokens.useUppercaseTitles) metricsTitle.uppercase() else metricsTitle,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        ThemedCard(
            priority = Priority.MEDIUM,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.componentPadding),
            ) {
                MetricRow(
                    label = stringResource(R.string.home_metric_completed_today),
                    value = metrics.completedToday.toString(),
                )
                MetricRow(
                    label = stringResource(R.string.home_metric_completed_week),
                    value = metrics.completedThisWeek.toString(),
                )
                MetricRow(
                    label = stringResource(R.string.home_metric_streak),
                    value = stringResource(R.string.home_metric_streak_value, metrics.streak),
                )
                MetricRow(
                    label = stringResource(R.string.home_metric_remaining),
                    value = metrics.totalRemaining.toString(),
                )
                MetricRow(
                    label = stringResource(R.string.home_metric_completion_rate),
                    value =
                        stringResource(
                            R.string.home_metric_completion_rate_value,
                            (metrics.completionRate * PERCENT_MULTIPLIER).toInt(),
                        ),
                )
                MetricRow(
                    label = stringResource(R.string.home_metric_overdue),
                    value = metrics.overdueCount.toString(),
                )
            }
        }
    }
}

@Composable
private fun MetricRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

private const val PERCENT_MULTIPLIER = 100
