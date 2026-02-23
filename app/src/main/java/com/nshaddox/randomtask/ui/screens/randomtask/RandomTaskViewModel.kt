package com.nshaddox.randomtask.ui.screens.randomtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nshaddox.randomtask.domain.usecase.CompleteTaskUseCase
import com.nshaddox.randomtask.domain.usecase.GetRandomTaskUseCase
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
class RandomTaskViewModel @Inject constructor(
    private val getRandomTaskUseCase: GetRandomTaskUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow(RandomTaskUiState())
    val uiState: StateFlow<RandomTaskUiState> = _uiState.asStateFlow()

    fun loadRandomTask() {
        viewModelScope.launch(ioDispatcher) {
            loadRandomTaskInternal()
        }
    }

    fun completeTask() {
        val task = _uiState.value.currentTask ?: return
        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(isLoading = true, error = null) }
            completeTaskUseCase(task)
                .onSuccess { loadRandomTaskInternal() }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Failed to complete task")
                    }
                }
        }
    }

    fun skipTask() {
        viewModelScope.launch(ioDispatcher) {
            loadRandomTaskInternal()
        }
    }

    private suspend fun loadRandomTaskInternal() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        try {
            val task = getRandomTaskUseCase()
            _uiState.update {
                it.copy(
                    currentTask = task,
                    isLoading = false,
                    noTasksAvailable = task == null
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(isLoading = false, error = e.message ?: "Unknown error")
            }
        }
    }
}
