package com.nshaddox.randomtask.ui.screens.tasklist

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.SortOrder
import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.domain.usecase.AddTaskUseCase
import com.nshaddox.randomtask.domain.usecase.CompleteTaskUseCase
import com.nshaddox.randomtask.domain.usecase.DeleteTaskUseCase
import com.nshaddox.randomtask.domain.usecase.GetTasksByCategoryUseCase
import com.nshaddox.randomtask.domain.usecase.GetTasksByPriorityUseCase
import com.nshaddox.randomtask.domain.usecase.GetTasksUseCase
import com.nshaddox.randomtask.domain.usecase.SearchTasksUseCase
import com.nshaddox.randomtask.domain.usecase.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@Suppress("TooManyFunctions", "LongParameterList", "UnusedPrivateProperty")
@HiltViewModel
class TaskListViewModel
    @Inject
    constructor(
        private val getTasksUseCase: GetTasksUseCase,
        private val addTaskUseCase: AddTaskUseCase,
        private val deleteTaskUseCase: DeleteTaskUseCase,
        private val completeTaskUseCase: CompleteTaskUseCase,
        private val updateTaskUseCase: UpdateTaskUseCase,
        private val searchTasksUseCase: SearchTasksUseCase,
        private val getTasksByPriorityUseCase: GetTasksByPriorityUseCase,
        private val getTasksByCategoryUseCase: GetTasksByCategoryUseCase,
        private val dataStore: DataStore<Preferences>,
        @Named("IO") private val ioDispatcher: CoroutineDispatcher,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(TaskListUiState())
        val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

        private val searchQueryFlow = MutableStateFlow("")
        private val filterPriorityFlow = MutableStateFlow<Priority?>(null)
        private val filterCategoryFlow = MutableStateFlow<String?>(null)
        private val sortOrderFlow = MutableStateFlow(SortOrder.CREATED_DATE_DESC)

        init {
            viewModelScope.launch(ioDispatcher) {
                // Seed sortOrderFlow from persisted DataStore value before combine starts
                val preferences = dataStore.data.first()
                val persisted =
                    preferences[SORT_ORDER_KEY]?.let { stored ->
                        try {
                            enumValueOf<SortOrder>(stored)
                        } catch (_: IllegalArgumentException) {
                            SortOrder.CREATED_DATE_DESC
                        }
                    } ?: SortOrder.CREATED_DATE_DESC
                sortOrderFlow.value = persisted

                combine(
                    getTasksUseCase(),
                    searchQueryFlow,
                    filterPriorityFlow,
                    filterCategoryFlow,
                    sortOrderFlow,
                ) { rawTasks, query, priority, category, sort ->
                    // Derive available categories from the full unfiltered list
                    val availableCategories =
                        rawTasks
                            .mapNotNull { it.category }
                            .distinct()
                            .sorted()

                    // Apply search filter
                    var filtered =
                        if (query.isBlank()) {
                            rawTasks
                        } else {
                            rawTasks.filter { task ->
                                task.title.contains(query.trim(), ignoreCase = true) ||
                                    task.description?.contains(query.trim(), ignoreCase = true) == true
                            }
                        }

                    // Apply priority filter
                    if (priority != null) {
                        filtered = filtered.filter { it.priority == priority }
                    }

                    // Apply category filter
                    if (category != null) {
                        filtered = filtered.filter { it.category == category }
                    }

                    // Apply sort
                    val sorted = applySortOrder(filtered, sort)

                    CombineResult(sorted, availableCategories, query, priority, category, sort)
                }.collect { result ->
                    _uiState.update {
                        it.copy(
                            tasks = result.tasks,
                            availableCategories = result.availableCategories,
                            searchQuery = result.searchQuery,
                            filterPriority = result.filterPriority,
                            filterCategory = result.filterCategory,
                            sortOrder = result.sortOrder,
                            isLoading = false,
                        )
                    }
                }
            }
        }

        fun updateSearchQuery(query: String) {
            searchQueryFlow.value = query
        }

        fun setFilterPriority(priority: Priority?) {
            filterPriorityFlow.value = priority
        }

        fun setFilterCategory(category: String?) {
            filterCategoryFlow.value = category
        }

        fun setSortOrder(sortOrder: SortOrder) {
            sortOrderFlow.value = sortOrder
            viewModelScope.launch(ioDispatcher) {
                dataStore.edit { prefs ->
                    prefs[SORT_ORDER_KEY] = sortOrder.name
                }
            }
        }

        fun addTask(
            title: String,
            description: String? = null,
            priority: Priority = Priority.MEDIUM,
            dueDate: Long? = null,
            category: String? = null,
        ) {
            viewModelScope.launch(ioDispatcher) {
                addTaskUseCase(
                    title = title,
                    description = description,
                    priority = priority,
                    dueDate = dueDate,
                    category = category,
                ).onFailure { error ->
                    _uiState.update { it.copy(errorMessage = error.message ?: "Failed to add task") }
                }.onSuccess {
                    _uiState.update { it.copy(isAddDialogVisible = false) }
                }
            }
        }

        fun deleteTask(task: Task) {
            viewModelScope.launch(ioDispatcher) {
                deleteTaskUseCase(task)
                    .onFailure { error ->
                        _uiState.update { it.copy(errorMessage = error.message ?: "Failed to delete task") }
                    }
            }
        }

        fun deleteTaskWithUndo(task: Task) {
            viewModelScope.launch(ioDispatcher) {
                deleteTaskUseCase(task)
                    .onSuccess {
                        _uiState.update { it.copy(pendingDeleteTask = task) }
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(errorMessage = error.message ?: "Failed to delete task") }
                    }
            }
        }

        fun undoDelete() {
            val pending = _uiState.value.pendingDeleteTask ?: return
            _uiState.update { it.copy(pendingDeleteTask = null) }
            viewModelScope.launch(ioDispatcher) {
                addTaskUseCase(
                    title = pending.title,
                    description = pending.description,
                    priority = pending.priority,
                    dueDate = pending.dueDate,
                    category = pending.category,
                ).onFailure { error ->
                    _uiState.update { it.copy(errorMessage = error.message ?: "Failed to restore task") }
                }
            }
        }

        fun confirmDelete() {
            _uiState.update { it.copy(pendingDeleteTask = null) }
        }

        fun toggleTaskCompletion(task: Task) {
            viewModelScope.launch(ioDispatcher) {
                val result =
                    if (!task.isCompleted) {
                        completeTaskUseCase(task)
                    } else {
                        updateTaskUseCase(task.copy(isCompleted = false))
                    }
                result.onFailure { error ->
                    _uiState.update { it.copy(errorMessage = error.message ?: "Failed to update task") }
                }
            }
        }

        fun showAddDialog() {
            _uiState.update { it.copy(isAddDialogVisible = true) }
        }

        fun hideAddDialog() {
            _uiState.update { it.copy(isAddDialogVisible = false) }
        }

        fun showEditDialog(task: TaskUiModel) {
            _uiState.update { it.copy(isEditDialogVisible = true, editingTask = task) }
        }

        fun hideEditDialog() {
            _uiState.update { it.copy(isEditDialogVisible = false, editingTask = null) }
        }

        @Suppress("LongParameterList")
        fun editTask(
            taskId: Long,
            title: String,
            description: String? = null,
            priority: Priority = Priority.MEDIUM,
            dueDate: Long? = null,
            category: String? = null,
        ) {
            viewModelScope.launch(ioDispatcher) {
                // Fetch current task state from repository to preserve createdAt/updatedAt
                val currentTasks = getTasksUseCase().first()
                val existingTask = currentTasks.find { it.id == taskId }
                if (existingTask == null) {
                    _uiState.update {
                        it.copy(
                            errorMessage = "Task not found",
                            isEditDialogVisible = false,
                            editingTask = null,
                        )
                    }
                    return@launch
                }
                val updated =
                    existingTask.copy(
                        title = title,
                        description = description,
                        priority = priority,
                        dueDate = dueDate,
                        category = category,
                    )
                updateTaskUseCase(updated)
                    .onSuccess {
                        _uiState.update {
                            it.copy(isEditDialogVisible = false, editingTask = null)
                        }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(
                                errorMessage = error.message ?: "Failed to update task",
                                isEditDialogVisible = false,
                                editingTask = null,
                            )
                        }
                    }
            }
        }

        fun clearError() {
            _uiState.update { it.copy(errorMessage = null) }
        }

        private data class CombineResult(
            val tasks: List<Task>,
            val availableCategories: List<String>,
            val searchQuery: String,
            val filterPriority: Priority?,
            val filterCategory: String?,
            val sortOrder: SortOrder,
        )

        private fun applySortOrder(
            tasks: List<Task>,
            sortOrder: SortOrder,
        ): List<Task> =
            when (sortOrder) {
                SortOrder.CREATED_DATE_DESC -> tasks.sortedByDescending { it.createdAt }
                SortOrder.TITLE_ASC -> tasks.sortedBy { it.title.lowercase() }
                SortOrder.PRIORITY_DESC ->
                    tasks.sortedByDescending { task ->
                        when (task.priority) {
                            Priority.HIGH -> 2
                            Priority.MEDIUM -> 1
                            Priority.LOW -> 0
                        }
                    }
                SortOrder.DUE_DATE_ASC ->
                    tasks.sortedWith(compareBy(nullsLast()) { it.dueDate })
            }

        private companion object {
            val SORT_ORDER_KEY = stringPreferencesKey("sort_order")
        }
    }
