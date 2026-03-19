package com.nshaddox.randomtask.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nshaddox.randomtask.domain.usecase.AddTaskUseCase
import com.nshaddox.randomtask.domain.usecase.CompleteTaskUseCase
import com.nshaddox.randomtask.domain.usecase.GetRandomTaskUseCase
import com.nshaddox.randomtask.domain.usecase.GetTaskMetricsUseCase
import com.nshaddox.randomtask.domain.usecase.GetWeightedRandomTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@Suppress("TooGenericExceptionCaught")
@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val getRandomTaskUseCase: GetRandomTaskUseCase,
        private val getWeightedRandomTaskUseCase: GetWeightedRandomTaskUseCase,
        private val completeTaskUseCase: CompleteTaskUseCase,
        private val addTaskUseCase: AddTaskUseCase,
        private val getTaskMetricsUseCase: GetTaskMetricsUseCase,
        @Named("IO") private val ioDispatcher: CoroutineDispatcher,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(HomeUiState())
        val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

        init {
            viewModelScope.launch(ioDispatcher) {
                getTaskMetricsUseCase().collect { metrics ->
                    _uiState.update { it.copy(metrics = metrics) }
                }
            }
        }

        fun pickRandom() {
            viewModelScope.launch(ioDispatcher) {
                _uiState.update { it.copy(isLoading = true, error = null) }
                try {
                    val task =
                        if (_uiState.value.useWeightedRandom) {
                            getWeightedRandomTaskUseCase()
                        } else {
                            getRandomTaskUseCase()
                        }
                    _uiState.update {
                        it.copy(
                            currentTask = task,
                            isLoading = false,
                            noTasksAvailable = task == null,
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Unknown error")
                    }
                }
            }
        }

        fun completeTask() {
            val task = _uiState.value.currentTask ?: return
            viewModelScope.launch(ioDispatcher) {
                _uiState.update { it.copy(isLoading = true, error = null) }
                completeTaskUseCase(task)
                    .onSuccess {
                        _uiState.update { it.copy(isLoading = false, taskCompleted = true, currentTask = null) }
                    }
                    .onFailure { e ->
                        _uiState.update {
                            it.copy(isLoading = false, error = e.message ?: "Failed to complete task")
                        }
                    }
            }
        }

        fun skipTask() {
            viewModelScope.launch(ioDispatcher) {
                _uiState.update { it.copy(isLoading = true, error = null) }
                try {
                    val task =
                        if (_uiState.value.useWeightedRandom) {
                            getWeightedRandomTaskUseCase()
                        } else {
                            getRandomTaskUseCase()
                        }
                    _uiState.update {
                        it.copy(
                            currentTask = task,
                            isLoading = false,
                            noTasksAvailable = task == null,
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Unknown error")
                    }
                }
            }
        }

        fun toggleWeightedRandom() {
            _uiState.update { it.copy(useWeightedRandom = !it.useWeightedRandom) }
        }

        fun addTask(
            title: String,
            description: String?,
        ) {
            viewModelScope.launch(ioDispatcher) {
                addTaskUseCase(title, description)
                    .onFailure { e ->
                        _uiState.update {
                            it.copy(error = e.message ?: "Failed to add task")
                        }
                    }
            }
        }

        fun resetTaskCompleted() {
            _uiState.update { it.copy(taskCompleted = false) }
        }
    }
