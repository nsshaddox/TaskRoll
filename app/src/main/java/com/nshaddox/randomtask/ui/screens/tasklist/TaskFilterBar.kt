package com.nshaddox.randomtask.ui.screens.tasklist

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nshaddox.randomtask.R
import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.SortOrder
import com.nshaddox.randomtask.ui.theme.Spacing

/**
 * Filter/search bar for the task list screen.
 *
 * Provides search, priority filter chips, category filter dropdown,
 * and sort order dropdown. Designed to sit between the TopAppBar and
 * the task list.
 *
 * @param searchQuery The current search text.
 * @param onSearchQueryChange Called when the search text changes.
 * @param filterPriority The currently selected priority filter, or null for "All".
 * @param onPriorityFilterChange Called when a priority filter chip is selected.
 * @param filterCategory The currently selected category filter, or null for "All".
 * @param onCategoryFilterChange Called when a category is selected from the dropdown.
 * @param sortOrder The current sort ordering.
 * @param onSortOrderChange Called when a sort order is selected from the dropdown.
 * @param availableCategories The list of categories to show in the dropdown.
 * @param modifier Modifier for customization.
 */
@Suppress("LongParameterList")
@Composable
fun TaskFilterBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filterPriority: Priority?,
    onPriorityFilterChange: (Priority?) -> Unit,
    filterCategory: String?,
    onCategoryFilterChange: (String?) -> Unit,
    sortOrder: SortOrder,
    onSortOrderChange: (SortOrder) -> Unit,
    availableCategories: List<String>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.medium, vertical = Spacing.small),
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
    ) {
        SearchField(
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
        )
        PriorityFilterChips(
            filterPriority = filterPriority,
            onPriorityFilterChange = onPriorityFilterChange,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            if (availableCategories.isNotEmpty()) {
                CategoryDropdown(
                    filterCategory = filterCategory,
                    onCategoryFilterChange = onCategoryFilterChange,
                    availableCategories = availableCategories,
                    modifier = Modifier.weight(1f),
                )
            }
            SortDropdown(
                sortOrder = sortOrder,
                onSortOrderChange = onSortOrderChange,
                modifier =
                    if (availableCategories.isNotEmpty()) {
                        Modifier.weight(1f)
                    } else {
                        Modifier.fillMaxWidth()
                    },
            )
        }
    }
}

@Composable
private fun SearchField(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = { Text(stringResource(R.string.filter_bar_search_hint)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.filter_bar_clear_search),
                    )
                }
            }
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun PriorityFilterChips(
    filterPriority: Priority?,
    onPriorityFilterChange: (Priority?) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
    ) {
        FilterChip(
            selected = filterPriority == null,
            onClick = { onPriorityFilterChange(null) },
            label = { Text(stringResource(R.string.filter_bar_priority_all)) },
        )
        Priority.entries.forEach { priority ->
            FilterChip(
                selected = filterPriority == priority,
                onClick = { onPriorityFilterChange(priority) },
                label = { Text(stringResource(priorityLabelResId(priority))) },
            )
        }
    }
}

/**
 * Returns the string resource ID for the label of the given [priority].
 */
private fun priorityLabelResId(priority: Priority): Int =
    when (priority) {
        Priority.LOW -> R.string.priority_low
        Priority.MEDIUM -> R.string.priority_medium
        Priority.HIGH -> R.string.priority_high
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    filterCategory: String?,
    onCategoryFilterChange: (String?) -> Unit,
    availableCategories: List<String>,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = filterCategory ?: stringResource(R.string.filter_bar_category_all),
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier =
                Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.filter_bar_category_all)) },
                onClick = {
                    onCategoryFilterChange(null)
                    expanded = false
                },
            )
            availableCategories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category) },
                    onClick = {
                        onCategoryFilterChange(category)
                        expanded = false
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortDropdown(
    sortOrder: SortOrder,
    onSortOrderChange: (SortOrder) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = sortOrderLabel(sortOrder),
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier =
                Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            SortOrder.entries.forEach { order ->
                DropdownMenuItem(
                    text = { Text(sortOrderLabel(order)) },
                    onClick = {
                        onSortOrderChange(order)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun sortOrderLabel(sortOrder: SortOrder): String =
    when (sortOrder) {
        SortOrder.CREATED_DATE_DESC -> stringResource(R.string.sort_created_date_desc)
        SortOrder.DUE_DATE_ASC -> stringResource(R.string.sort_due_date_asc)
        SortOrder.PRIORITY_DESC -> stringResource(R.string.sort_priority_desc)
        SortOrder.TITLE_ASC -> stringResource(R.string.sort_title_asc)
    }
