package com.nshaddox.randomtask.ui.screens.completedtasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.domain.usecase.AddTaskUseCase
import com.nshaddox.randomtask.domain.usecase.CompleteTaskUseCase
import com.nshaddox.randomtask.domain.usecase.DeleteTaskUseCase
import com.nshaddox.randomtask.domain.usecase.GetCompletedTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
@Suppress("LongParameterList")
class CompletedTasksViewModel
    @Inject
    constructor(
        private val getCompletedTasksUseCase: GetCompletedTasksUseCase,
        private val deleteTaskUseCase: DeleteTaskUseCase,
        private val addTaskUseCase: AddTaskUseCase,
        private val completeTaskUseCase: CompleteTaskUseCase,
        @Named("IO") private val ioDispatcher: CoroutineDispatcher,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(CompletedTasksUiState())
        val uiState: StateFlow<CompletedTasksUiState> = _uiState.asStateFlow()

        init {
            viewModelScope.launch(ioDispatcher) {
                getCompletedTasksUseCase().collect { tasks ->
                    _uiState.update { it.copy(tasks = tasks, isLoading = false) }
                }
            }
        }

        fun deleteTask(task: Task) {
            viewModelScope.launch(ioDispatcher) {
                deleteTaskUseCase(task)
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(errorMessage = error.message ?: "Failed to delete task")
                        }
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
                        _uiState.update {
                            it.copy(errorMessage = error.message ?: "Failed to delete task")
                        }
                    }
            }
        }

        fun undoDelete() {
            val pending = _uiState.value.pendingDeleteTask ?: return
            _uiState.update { it.copy(pendingDeleteTask = null) }
            viewModelScope.launch(ioDispatcher) {
                restoreTask(pending)
            }
        }

        private suspend fun restoreTask(pending: Task) {
            val addResult =
                addTaskUseCase(
                    title = pending.title,
                    description = pending.description,
                    priority = pending.priority,
                    dueDate = pending.dueDate,
                    category = pending.category,
                )
            if (addResult.isFailure) {
                val msg = addResult.exceptionOrNull()?.message ?: "Failed to restore task"
                _uiState.update { it.copy(errorMessage = msg) }
                return
            }
            // Restored task must be re-completed since AddTaskUseCase creates incomplete tasks
            if (pending.isCompleted) {
                val newId = addResult.getOrThrow()
                val restoredTask =
                    pending.copy(
                        id = newId,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis(),
                    )
                val completeResult = completeTaskUseCase(restoredTask)
                if (completeResult.isFailure) {
                    val msg = completeResult.exceptionOrNull()?.message ?: "Failed to restore task"
                    _uiState.update { it.copy(errorMessage = msg) }
                }
            }
        }

        fun confirmDelete() {
            _uiState.update { it.copy(pendingDeleteTask = null) }
        }

        fun clearError() {
            _uiState.update { it.copy(errorMessage = null) }
        }
    }
