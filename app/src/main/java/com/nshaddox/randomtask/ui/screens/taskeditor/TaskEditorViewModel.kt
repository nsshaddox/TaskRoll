package com.nshaddox.randomtask.ui.screens.taskeditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.domain.repository.TaskRepository
import com.nshaddox.randomtask.domain.usecase.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

data class TaskEditorUiState(
    val taskTitle: String = "",
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class TaskEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: TaskRepository,
    private val updateTaskUseCase: UpdateTaskUseCase,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val taskId: Long = checkNotNull(savedStateHandle.get<Long>("taskId"))

    private val _uiState = MutableStateFlow(TaskEditorUiState())
    val uiState: StateFlow<TaskEditorUiState> = _uiState.asStateFlow()

    private var loadedTask: Task? = null

    init {
        viewModelScope.launch(ioDispatcher) {
            val task = repository.getTaskById(taskId).firstOrNull()
            if (task != null) {
                loadedTask = task
                _uiState.update { it.copy(taskTitle = task.title, isLoading = false) }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Task not found")
                }
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        _uiState.update { it.copy(taskTitle = newTitle) }
    }

    fun saveTask() {
        val task = loadedTask ?: return
        val currentTitle = _uiState.value.taskTitle
        val updatedTask = task.copy(title = currentTitle)

        viewModelScope.launch(ioDispatcher) {
            updateTaskUseCase(updatedTask)
                .onSuccess {
                    _uiState.update { it.copy(isSaved = true) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(errorMessage = error.message ?: "Failed to save task")
                    }
                }
        }
    }
}
