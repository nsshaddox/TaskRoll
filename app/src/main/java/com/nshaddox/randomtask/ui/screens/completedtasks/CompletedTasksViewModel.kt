package com.nshaddox.randomtask.ui.screens.completedtasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nshaddox.randomtask.domain.model.Task
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
class CompletedTasksViewModel
    @Inject
    constructor(
        private val getCompletedTasksUseCase: GetCompletedTasksUseCase,
        private val deleteTaskUseCase: DeleteTaskUseCase,
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

        fun clearError() {
            _uiState.update { it.copy(errorMessage = null) }
        }
    }
