package com.nshaddox.randomtask.ui.screens.taskeditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nshaddox.randomtask.R
import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.SubTask
import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.domain.repository.TaskRepository
import com.nshaddox.randomtask.domain.usecase.AddSubTaskUseCase
import com.nshaddox.randomtask.domain.usecase.CompleteSubTaskUseCase
import com.nshaddox.randomtask.domain.usecase.DeleteSubTaskUseCase
import com.nshaddox.randomtask.domain.usecase.GetTaskWithSubTasksUseCase
import com.nshaddox.randomtask.domain.usecase.UncompleteSubTaskUseCase
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
    val taskDescription: String = "",
    val taskPriority: Priority = Priority.MEDIUM,
    val taskDueDate: Long? = null,
    val taskCategory: String = "",
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val errorMessage: String? = null,
    val errorResId: Int? = null,
    val subTasks: List<SubTask> = emptyList(),
    val isAddingSubTask: Boolean = false,
    val newSubTaskTitle: String = "",
    val showDatePicker: Boolean = false,
)

@Suppress("TooManyFunctions", "LongParameterList")
@HiltViewModel
class TaskEditorViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val repository: TaskRepository,
        private val updateTaskUseCase: UpdateTaskUseCase,
        private val getTaskWithSubTasksUseCase: GetTaskWithSubTasksUseCase,
        private val addSubTaskUseCase: AddSubTaskUseCase,
        private val completeSubTaskUseCase: CompleteSubTaskUseCase,
        private val uncompleteSubTaskUseCase: UncompleteSubTaskUseCase,
        private val deleteSubTaskUseCase: DeleteSubTaskUseCase,
        @Named("IO") private val ioDispatcher: CoroutineDispatcher,
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
                    _uiState.update {
                        it.copy(
                            taskTitle = task.title,
                            taskDescription = task.description.orEmpty(),
                            taskPriority = task.priority,
                            taskDueDate = task.dueDate,
                            taskCategory = task.category.orEmpty(),
                            isLoading = false,
                        )
                    }
                    startCollectingSubTasks()
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, errorResId = R.string.error_task_not_found)
                    }
                }
            }
        }

        fun onTitleChange(newTitle: String) {
            _uiState.update { it.copy(taskTitle = newTitle) }
        }

        fun onDescriptionChange(newDescription: String) {
            _uiState.update { it.copy(taskDescription = newDescription) }
        }

        fun onPriorityChange(newPriority: Priority) {
            _uiState.update { it.copy(taskPriority = newPriority) }
        }

        fun onDueDateChange(newDueDate: Long?) {
            _uiState.update { it.copy(taskDueDate = newDueDate) }
        }

        fun onCategoryChange(newCategory: String) {
            _uiState.update { it.copy(taskCategory = newCategory) }
        }

        fun showDatePicker() {
            _uiState.update { it.copy(showDatePicker = true) }
        }

        fun hideDatePicker() {
            _uiState.update { it.copy(showDatePicker = false) }
        }

        fun clearError() {
            _uiState.update { it.copy(errorMessage = null, errorResId = null) }
        }

        fun saveTask() {
            val task = loadedTask ?: return
            val state = _uiState.value
            val updatedTask =
                task.copy(
                    title = state.taskTitle,
                    description = state.taskDescription.ifBlank { null },
                    priority = state.taskPriority,
                    dueDate = state.taskDueDate,
                    category = state.taskCategory.ifBlank { null },
                )

            viewModelScope.launch(ioDispatcher) {
                updateTaskUseCase(updatedTask)
                    .onSuccess {
                        _uiState.update { it.copy(isSaved = true) }
                    }
                    .onFailure { _ ->
                        _uiState.update {
                            it.copy(errorResId = R.string.error_save_task)
                        }
                    }
            }
        }

        fun addSubTask() {
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

        fun deleteSubTask(subTaskId: Long) {
            viewModelScope.launch(ioDispatcher) {
                deleteSubTaskUseCase(subTaskId)
            }
        }

        fun toggleSubTask(subTask: SubTask) {
            viewModelScope.launch(ioDispatcher) {
                if (subTask.isCompleted) {
                    uncompleteSubTaskUseCase(subTask)
                } else {
                    completeSubTaskUseCase(subTask)
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

        private fun startCollectingSubTasks() {
            viewModelScope.launch(ioDispatcher) {
                getTaskWithSubTasksUseCase(taskId).collect { taskWithSubTasks ->
                    _uiState.update {
                        it.copy(subTasks = taskWithSubTasks?.subTasks ?: emptyList())
                    }
                }
            }
        }
    }
