package com.nshaddox.randomtask.ui.screens.settings

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nshaddox.randomtask.BuildConfig
import com.nshaddox.randomtask.R
import com.nshaddox.randomtask.domain.model.SortOrder
import com.nshaddox.randomtask.domain.model.ThemeVariant
import com.nshaddox.randomtask.ui.theme.Spacing

/**
 * Stateful Settings Screen that integrates with ViewModel and NavController.
 *
 * @param navController Navigation controller for back navigation.
 * @param viewModel Hilt-injected ViewModel managing settings state.
 */
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val feedbackEmailAddress = stringResource(R.string.feedback_email_address)
    val feedbackSubject = stringResource(R.string.feedback_email_subject, BuildConfig.VERSION_NAME)
    val feedbackBody =
        stringResource(
            R.string.feedback_email_body,
            BuildConfig.VERSION_NAME,
            Build.VERSION.RELEASE,
            Build.MODEL,
        )

    SettingsScreenContent(
        uiState = uiState,
        onThemeSelected = viewModel::setTheme,
        onSortOrderSelected = viewModel::setSortOrder,
        onBackClick = { navController.popBackStack() },
        onSendFeedback = {
            val intent =
                buildFeedbackEmailIntent(
                    emailAddress = feedbackEmailAddress,
                    subject = feedbackSubject,
                    body = feedbackBody,
                )
            safeLaunchFeedbackIntent(intent) { context.startActivity(it) }
        },
    )
}

/**
 * Stateless Settings Screen content for preview and testing support.
 *
 * @param uiState Current UI state for the settings screen.
 * @param onThemeSelected Callback when the user selects a theme option.
 * @param onSortOrderSelected Callback when the user selects a sort order option.
 * @param onBackClick Callback for back navigation.
 * @param modifier Modifier for customization.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreenContent(
    uiState: SettingsUiState,
    onThemeSelected: (ThemeVariant) -> Unit = {},
    onSortOrderSelected: (SortOrder) -> Unit = {},
    onBackClick: () -> Unit = {},
    onSendFeedback: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_back),
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
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.medium),
        ) {
            // Theme section
            SettingsSectionHeader(text = stringResource(R.string.settings_theme_section))
            Spacer(modifier = Modifier.height(Spacing.small))
            ThemeSelectionGroup(
                selectedTheme = uiState.appTheme,
                onThemeSelected = onThemeSelected,
            )

            Spacer(modifier = Modifier.height(Spacing.large))

            // Sort order section
            SettingsSectionHeader(text = stringResource(R.string.settings_sort_section))
            Spacer(modifier = Modifier.height(Spacing.small))
            SortOrderSelectionGroup(
                selectedSortOrder = uiState.sortOrder,
                onSortOrderSelected = onSortOrderSelected,
            )

            Spacer(modifier = Modifier.height(Spacing.large))

            // About section
            SettingsSectionHeader(text = stringResource(R.string.settings_about_section))
            Spacer(modifier = Modifier.height(Spacing.small))
            AboutSection(onSendFeedback = onSendFeedback)
        }
    }
}

@Composable
private fun AboutSection(
    onSendFeedback: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "${stringResource(R.string.settings_version_label)}: ${BuildConfig.VERSION_NAME}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(Spacing.small))
        Text(
            text = stringResource(R.string.feedback_send_label),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onSendFeedback)
                    .padding(vertical = Spacing.componentPadding),
        )
    }
}

@Composable
private fun SettingsSectionHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier,
    )
}

@Composable
private fun ThemeSelectionGroup(
    selectedTheme: ThemeVariant,
    onThemeSelected: (ThemeVariant) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.selectableGroup()) {
        ThemeVariant.entries.forEach { theme ->
            RadioOptionRow(
                text = theme.displayName(),
                selected = theme == selectedTheme,
                onClick = { onThemeSelected(theme) },
            )
        }
    }
}

@Composable
private fun SortOrderSelectionGroup(
    selectedSortOrder: SortOrder,
    onSortOrderSelected: (SortOrder) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.selectableGroup()) {
        SortOrder.entries.forEach { sortOrder ->
            RadioOptionRow(
                text = sortOrder.displayName(),
                selected = sortOrder == selectedSortOrder,
                onClick = { onSortOrderSelected(sortOrder) },
            )
        }
    }
}

@Composable
private fun RadioOptionRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .selectable(
                    selected = selected,
                    onClick = onClick,
                    role = Role.RadioButton,
                )
                .padding(vertical = Spacing.componentPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = Spacing.medium),
        )
    }
}

/**
 * Returns the user-facing display name for this [ThemeVariant] value.
 */
@Composable
private fun ThemeVariant.displayName(): String =
    when (this) {
        ThemeVariant.OBSIDIAN -> stringResource(R.string.theme_obsidian)
        ThemeVariant.NEO_BRUTALIST -> stringResource(R.string.theme_neo_brutalist)
        ThemeVariant.VAPOR -> stringResource(R.string.theme_vapor)
    }

/**
 * Returns the user-facing display name for this [SortOrder] value.
 */
@Composable
private fun SortOrder.displayName(): String =
    when (this) {
        SortOrder.CREATED_DATE_DESC -> stringResource(R.string.sort_created_date_desc)
        SortOrder.DUE_DATE_ASC -> stringResource(R.string.sort_due_date_asc)
        SortOrder.PRIORITY_DESC -> stringResource(R.string.sort_priority_desc)
        SortOrder.TITLE_ASC -> stringResource(R.string.sort_title_asc)
    }
