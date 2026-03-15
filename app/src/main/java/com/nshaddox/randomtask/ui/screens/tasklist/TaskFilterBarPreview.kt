package com.nshaddox.randomtask.ui.screens.tasklist

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.SortOrder
import com.nshaddox.randomtask.ui.theme.RandomTaskTheme

/**
 * Preview for TaskFilterBar with default state
 */
@PreviewLightDark
@Composable
fun TaskFilterBarDefaultPreview() {
    RandomTaskTheme {
        TaskFilterBar(
            searchQuery = "",
            onSearchQueryChange = {},
            filterPriority = null,
            onPriorityFilterChange = {},
            filterCategory = null,
            onCategoryFilterChange = {},
            sortOrder = SortOrder.CREATED_DATE_DESC,
            onSortOrderChange = {},
            availableCategories = listOf("Work", "Personal", "Shopping"),
        )
    }
}

/**
 * Preview for TaskFilterBar with active search and priority filter
 */
@PreviewLightDark
@Composable
fun TaskFilterBarActiveFiltersPreview() {
    RandomTaskTheme {
        TaskFilterBar(
            searchQuery = "groceries",
            onSearchQueryChange = {},
            filterPriority = Priority.HIGH,
            onPriorityFilterChange = {},
            filterCategory = "Shopping",
            onCategoryFilterChange = {},
            sortOrder = SortOrder.PRIORITY_DESC,
            onSortOrderChange = {},
            availableCategories = listOf("Work", "Personal", "Shopping"),
        )
    }
}

/**
 * Preview for TaskFilterBar without categories
 */
@PreviewLightDark
@Composable
fun TaskFilterBarNoCategoriesPreview() {
    RandomTaskTheme {
        TaskFilterBar(
            searchQuery = "",
            onSearchQueryChange = {},
            filterPriority = null,
            onPriorityFilterChange = {},
            filterCategory = null,
            onCategoryFilterChange = {},
            sortOrder = SortOrder.CREATED_DATE_DESC,
            onSortOrderChange = {},
            availableCategories = emptyList(),
        )
    }
}
