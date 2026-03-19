package com.nshaddox.randomtask.ui.screens.randomtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nshaddox.randomtask.R
import com.nshaddox.randomtask.domain.usecase.CompleteTaskUseCase
import com.nshaddox.randomtask.domain.usecase.GetRandomTaskUseCase
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

@HiltViewModel
class RandomTaskViewModel
    @Inject
    constructor(
        private val getRandomTaskUseCase: GetRandomTaskUseCase,
        private val completeTaskUseCase: CompleteTaskUseCase,
        private val getWeightedRandomTaskUseCase: GetWeightedRandomTaskUseCase,
        @Named("IO") private val ioDispatcher: CoroutineDispatcher,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(RandomTaskUiState())
        val uiState: StateFlow<RandomTaskUiState> = _uiState.asStateFlow()

        private val _useWeightedRandom = MutableStateFlow(false)
        val useWeightedRandom: StateFlow<Boolean> = _useWeightedRandom.asStateFlow()

        fun loadRandomTask() {
            viewModelScope.launch(ioDispatcher) {
                loadRandomTaskInternal()
            }
        }

        fun completeTask() {
            val task = _uiState.value.currentTask ?: return
            viewModelScope.launch(ioDispatcher) {
                _uiState.update { it.copy(isLoading = true, error = null, errorResId = null) }
                completeTaskUseCase(task)
                    .onSuccess {
                        _uiState.update { it.copy(isLoading = false, taskCompleted = true) }
                    }
                    .onFailure { _ ->
                        _uiState.update {
                            it.copy(isLoading = false, errorResId = R.string.error_complete_task)
                        }
                    }
            }
        }

        fun resetTaskCompleted() {
            _uiState.update { it.copy(taskCompleted = false) }
        }

        fun clearError() {
            _uiState.update { it.copy(error = null, errorResId = null) }
        }

        fun toggleWeightedRandom() {
            _useWeightedRandom.update { !it }
        }

        fun skipTask() {
            viewModelScope.launch(ioDispatcher) {
                loadRandomTaskInternal()
            }
        }

        private suspend fun loadRandomTaskInternal() {
            _uiState.update { it.copy(isLoading = true, error = null, errorResId = null) }
            try {
                val task =
                    if (_useWeightedRandom.value) {
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
            } catch (
                @Suppress("SwallowedException", "TooGenericExceptionCaught")
                e: Exception,
            ) {
                _uiState.update {
                    it.copy(isLoading = false, error = null, errorResId = R.string.error_load_random_task)
                }
            }
        }
    }
