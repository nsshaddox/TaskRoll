package com.nshaddox.randomtask.ui.screens.randomtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nshaddox.randomtask.R
import com.nshaddox.randomtask.domain.model.SubTask
import com.nshaddox.randomtask.domain.usecase.AddSubTaskUseCase
import com.nshaddox.randomtask.domain.usecase.CompleteSubTaskUseCase
import com.nshaddox.randomtask.domain.usecase.CompleteTaskUseCase
import com.nshaddox.randomtask.domain.usecase.GetRandomTaskUseCase
import com.nshaddox.randomtask.domain.usecase.GetTaskWithSubTasksUseCase
import com.nshaddox.randomtask.domain.usecase.GetWeightedRandomTaskUseCase
import com.nshaddox.randomtask.domain.usecase.UncompleteSubTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@Suppress("TooManyFunctions", "LongParameterList")
@HiltViewModel
class RandomTaskViewModel
    @Inject
    constructor(
        private val getRandomTaskUseCase: GetRandomTaskUseCase,
        private val completeTaskUseCase: CompleteTaskUseCase,
        private val getWeightedRandomTaskUseCase: GetWeightedRandomTaskUseCase,
        private val getTaskWithSubTasksUseCase: GetTaskWithSubTasksUseCase,
        private val addSubTaskUseCase: AddSubTaskUseCase,
        private val completeSubTaskUseCase: CompleteSubTaskUseCase,
        private val uncompleteSubTaskUseCase: UncompleteSubTaskUseCase,
        @Named("IO") private val ioDispatcher: CoroutineDispatcher,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(RandomTaskUiState())
        val uiState: StateFlow<RandomTaskUiState> = _uiState.asStateFlow()

        private val _useWeightedRandom = MutableStateFlow(false)
        val useWeightedRandom: StateFlow<Boolean> = _useWeightedRandom.asStateFlow()

        private var subTaskCollectionJob: Job? = null

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

        fun addSubTask() {
            val taskId = _uiState.value.currentTask?.id ?: return
            val title = _uiState.value.newSubTaskTitle
            viewModelScope.launch(ioDispatcher) {
                addSubTaskUseCase(parentTaskId = taskId, title = title)
                    .onSuccess {
                        _uiState.update {
                            it.copy(isAddingSubTask = false, newSubTaskTitle = "")
                        }
                    }
                    .onFailure { _ ->
                        _uiState.update {
                            it.copy(errorResId = R.string.error_add_subtask)
                        }
                    }
            }
        }

        fun toggleSubTask(subTask: SubTask) {
            viewModelScope.launch(ioDispatcher) {
                val result =
                    if (subTask.isCompleted) {
                        uncompleteSubTaskUseCase(subTask)
                    } else {
                        completeSubTaskUseCase(subTask)
                    }
                result.onFailure { _ ->
                    _uiState.update {
                        it.copy(errorResId = R.string.error_toggle_subtask)
                    }
                }
            }
        }

        fun onNewSubTaskTitleChange(title: String) {
            _uiState.update { it.copy(newSubTaskTitle = title) }
        }

        fun showAddSubTask() {
            _uiState.update { it.copy(isAddingSubTask = true) }
        }

        fun hideAddSubTask() {
            _uiState.update { it.copy(isAddingSubTask = false, newSubTaskTitle = "") }
        }

        private suspend fun loadRandomTaskInternal() {
            _uiState.update {
                it.copy(isLoading = true, error = null, errorResId = null, subTasks = emptyList())
            }
            subTaskCollectionJob?.cancel()
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
                if (task != null) {
                    startCollectingSubTasks(task.id)
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

        private fun startCollectingSubTasks(taskId: Long) {
            subTaskCollectionJob =
                viewModelScope.launch(ioDispatcher) {
                    getTaskWithSubTasksUseCase(taskId).collect { taskWithSubTasks ->
                        _uiState.update {
                            it.copy(subTasks = taskWithSubTasks?.subTasks ?: emptyList())
                        }
                    }
                }
        }
    }
