package com.nshaddox.randomtask.ui.screens.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.domain.usecase.AddTaskUseCase
import com.nshaddox.randomtask.domain.usecase.CompleteTaskUseCase
import com.nshaddox.randomtask.domain.usecase.DeleteTaskUseCase
import com.nshaddox.randomtask.domain.usecase.GetTasksUseCase
import com.nshaddox.randomtask.domain.usecase.UpdateTaskUseCase
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
class TaskListViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskListUiState())
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(ioDispatcher) {
            getTasksUseCase().collect { tasks ->
                _uiState.update { it.copy(tasks = tasks, isLoading = false) }
            }
        }
    }

    fun addTask(title: String) {
        val now = System.currentTimeMillis()
        val task = Task(title = title, createdAt = now, updatedAt = now)
        viewModelScope.launch(ioDispatcher) {
            addTaskUseCase(task)
                .onFailure { error ->
                    _uiState.update { it.copy(errorMessage = error.message ?: "Failed to add task") }
                }
                .onSuccess {
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

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch(ioDispatcher) {
            val result = if (!task.isCompleted) {
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

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
