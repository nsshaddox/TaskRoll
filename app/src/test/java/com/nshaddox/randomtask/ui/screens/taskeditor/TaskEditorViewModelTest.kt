package com.nshaddox.randomtask.ui.screens.taskeditor

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.nshaddox.randomtask.R
import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.SubTask
import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.domain.usecase.AddSubTaskUseCase
import com.nshaddox.randomtask.domain.usecase.CompleteSubTaskUseCase
import com.nshaddox.randomtask.domain.usecase.DeleteSubTaskUseCase
import com.nshaddox.randomtask.domain.usecase.FakeSubTaskRepository
import com.nshaddox.randomtask.domain.usecase.FakeTaskRepository
import com.nshaddox.randomtask.domain.usecase.GetTaskWithSubTasksUseCase
import com.nshaddox.randomtask.domain.usecase.UncompleteSubTaskUseCase
import com.nshaddox.randomtask.domain.usecase.UpdateTaskUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskEditorViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeTaskRepository
    private lateinit var subTaskRepository: FakeSubTaskRepository
    private lateinit var updateTaskUseCase: UpdateTaskUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTaskRepository()
        subTaskRepository = FakeSubTaskRepository()
        updateTaskUseCase = UpdateTaskUseCase(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(taskId: Long = 1L): TaskEditorViewModel {
        val savedStateHandle = SavedStateHandle(mapOf("taskId" to taskId))
        return TaskEditorViewModel(
            savedStateHandle = savedStateHandle,
            repository = repository,
            updateTaskUseCase = updateTaskUseCase,
            getTaskWithSubTasksUseCase = GetTaskWithSubTasksUseCase(repository, subTaskRepository),
            addSubTaskUseCase = AddSubTaskUseCase(subTaskRepository),
            completeSubTaskUseCase = CompleteSubTaskUseCase(subTaskRepository),
            uncompleteSubTaskUseCase = UncompleteSubTaskUseCase(subTaskRepository),
            deleteSubTaskUseCase = DeleteSubTaskUseCase(subTaskRepository),
            ioDispatcher = testDispatcher,
        )
    }

    private fun createTask(
        id: Long = 0,
        title: String = "Test Task",
        description: String? = null,
        priority: Priority = Priority.MEDIUM,
        dueDate: Long? = null,
        category: String? = null,
    ) = Task(
        id = id,
        title = title,
        description = description,
        priority = priority,
        dueDate = dueDate,
        category = category,
        createdAt = 1000L,
        updatedAt = 1000L,
    )

    @Test
    fun `initial state loads task title from repository by ID`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "My Task"))

            val viewModel = createViewModel(taskId = 1L)

            viewModel.uiState.test {
                val initial = awaitItem()
                assertTrue(initial.isLoading)

                val loaded = awaitItem()
                assertFalse(loaded.isLoading)
                assertEquals("My Task", loaded.taskTitle)
                assertNull(loaded.errorMessage)
            }
        }

    @Test
    fun `initial state shows error when task not found`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel(taskId = 999L)

            viewModel.uiState.test {
                val initial = awaitItem()
                assertTrue(initial.isLoading)

                val loaded = awaitItem()
                assertFalse(loaded.isLoading)
                assertEquals(R.string.error_task_not_found, loaded.errorResId)
            }
        }

    @Test
    fun `onTitleChange updates taskTitle in state`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Original"))

            val viewModel = createViewModel(taskId = 1L)

            viewModel.uiState.test {
                awaitItem() // initial loading
                awaitItem() // loaded

                viewModel.onTitleChange("Updated Title")

                val updated = awaitItem()
                assertEquals("Updated Title", updated.taskTitle)
            }
        }

    @Test
    fun `saveTask with valid title sets isSaved to true`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Original"))

            val viewModel = createViewModel(taskId = 1L)

            viewModel.uiState.test {
                awaitItem() // initial loading
                awaitItem() // loaded

                viewModel.onTitleChange("New Title")
                awaitItem() // title changed

                viewModel.saveTask()

                val saved = awaitItem()
                assertTrue(saved.isSaved)
            }
        }

    @Test
    fun `saveTask with blank title sets errorResId`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Original"))

            val viewModel = createViewModel(taskId = 1L)

            viewModel.uiState.test {
                awaitItem() // initial loading
                awaitItem() // loaded

                viewModel.onTitleChange("   ")
                awaitItem() // title changed

                viewModel.saveTask()

                val errorState = awaitItem()
                assertEquals(R.string.error_save_task, errorState.errorResId)
                assertFalse(errorState.isSaved)
            }
        }

    @Test
    fun `saveTask calls UpdateTaskUseCase with correct task data`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Original"))

            val viewModel = createViewModel(taskId = 1L)

            viewModel.uiState.test {
                awaitItem() // initial loading
                awaitItem() // loaded

                viewModel.onTitleChange("Updated Title")
                awaitItem() // title changed

                viewModel.saveTask()
                awaitItem() // saved

                val storedTask = repository.getAllTasks().first().first()
                assertEquals("Updated Title", storedTask.title)
                assertEquals(1L, storedTask.id)
            }
        }

    // === Error injection tests using shouldFailMutations ===

    @Test
    fun `saveTask with shouldFailMutations sets errorResId`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Original"))

            val viewModel = createViewModel(taskId = 1L)

            viewModel.uiState.test {
                awaitItem() // initial loading
                awaitItem() // loaded

                viewModel.onTitleChange("Updated")
                awaitItem() // title changed

                repository.shouldFailMutations = true
                viewModel.saveTask()

                val errorState = awaitItem()
                assertEquals(R.string.error_save_task, errorState.errorResId)
                assertFalse(errorState.isSaved)
            }
        }

    @Test
    fun `clearError resets errorResId to null`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Original"))

            val viewModel = createViewModel(taskId = 1L)

            viewModel.uiState.test {
                awaitItem() // initial loading
                awaitItem() // loaded

                viewModel.onTitleChange("Updated")
                awaitItem() // title changed

                repository.shouldFailMutations = true
                viewModel.saveTask()

                val errorState = awaitItem()
                assertEquals(R.string.error_save_task, errorState.errorResId)

                viewModel.clearError()

                val clearedState = awaitItem()
                assertNull(clearedState.errorResId)
            }
        }

    // === Subtask tests ===

    @Test
    fun `init loads subtasks for task`() =
        runTest(testDispatcher) {
            val taskId = repository.addTask(createTask(title = "Task")).getOrThrow()
            subTaskRepository.addSubTask(
                SubTask(parentTaskId = taskId, title = "Sub 1", createdAt = 1000L, updatedAt = 1000L),
            )

            val viewModel = createViewModel(taskId = taskId)
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(1, state.subTasks.size)
            assertEquals("Sub 1", state.subTasks.first().title)
        }

    @Test
    fun `addSubTask adds subtask to list`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Task"))

            val viewModel = createViewModel(taskId = 1L)
            advanceUntilIdle()

            viewModel.onNewSubTaskTitleChange("New Sub")
            viewModel.addSubTask()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(1, state.subTasks.size)
            assertEquals("New Sub", state.subTasks.first().title)
            assertFalse(state.isAddingSubTask)
            assertEquals("", state.newSubTaskTitle)
        }

    @Test
    fun `addSubTask with blank title shows error`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Task"))

            val viewModel = createViewModel(taskId = 1L)
            advanceUntilIdle()

            viewModel.onNewSubTaskTitleChange("   ")
            viewModel.addSubTask()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(R.string.error_add_subtask, state.errorResId)
            assertTrue(state.subTasks.isEmpty())
        }

    @Test
    fun `deleteSubTask removes subtask`() =
        runTest(testDispatcher) {
            val taskId = repository.addTask(createTask(title = "Task")).getOrThrow()
            subTaskRepository.addSubTask(
                SubTask(parentTaskId = taskId, title = "Sub 1", createdAt = 1000L, updatedAt = 1000L),
            )

            val viewModel = createViewModel(taskId = taskId)
            advanceUntilIdle()

            assertEquals(1, viewModel.uiState.value.subTasks.size)

            viewModel.deleteSubTask(1L)
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.subTasks.isEmpty())
        }

    @Test
    fun `toggleSubTask toggles completion status`() =
        runTest(testDispatcher) {
            val taskId = repository.addTask(createTask(title = "Task")).getOrThrow()
            subTaskRepository.addSubTask(
                SubTask(parentTaskId = taskId, title = "Sub 1", createdAt = 1000L, updatedAt = 1000L),
            )

            val viewModel = createViewModel(taskId = taskId)
            advanceUntilIdle()

            val subTask = viewModel.uiState.value.subTasks.first()
            assertFalse(subTask.isCompleted)

            viewModel.toggleSubTask(subTask)
            advanceUntilIdle()

            val updated = viewModel.uiState.value.subTasks.first()
            assertTrue(updated.isCompleted)
        }

    @Test
    fun `showAddSubTask sets isAddingSubTask to true`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Task"))

            val viewModel = createViewModel(taskId = 1L)
            advanceUntilIdle()

            viewModel.showAddSubTask()

            assertTrue(viewModel.uiState.value.isAddingSubTask)
        }

    @Test
    fun `hideAddSubTask clears state`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Task"))

            val viewModel = createViewModel(taskId = 1L)
            advanceUntilIdle()

            viewModel.showAddSubTask()
            viewModel.onNewSubTaskTitleChange("Partial")

            viewModel.hideAddSubTask()

            assertFalse(viewModel.uiState.value.isAddingSubTask)
            assertEquals("", viewModel.uiState.value.newSubTaskTitle)
        }

    // === Task field tests ===

    @Test
    fun `init loads all task fields`() =
        runTest(testDispatcher) {
            repository.addTask(
                createTask(
                    title = "Full Task",
                    description = "A description",
                    priority = Priority.HIGH,
                    dueDate = 20000L,
                    category = "Work",
                ),
            )

            val viewModel = createViewModel(taskId = 1L)
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals("Full Task", state.taskTitle)
            assertEquals("A description", state.taskDescription)
            assertEquals(Priority.HIGH, state.taskPriority)
            assertEquals(20000L, state.taskDueDate)
            assertEquals("Work", state.taskCategory)
        }

    @Test
    fun `init loads empty strings for null description and category`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Task"))

            val viewModel = createViewModel(taskId = 1L)
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals("", state.taskDescription)
            assertEquals("", state.taskCategory)
            assertNull(state.taskDueDate)
        }

    @Test
    fun `onDescriptionChange updates description`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Task"))

            val viewModel = createViewModel(taskId = 1L)
            advanceUntilIdle()

            viewModel.onDescriptionChange("New description")

            assertEquals("New description", viewModel.uiState.value.taskDescription)
        }

    @Test
    fun `onPriorityChange updates priority`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Task"))

            val viewModel = createViewModel(taskId = 1L)
            advanceUntilIdle()

            viewModel.onPriorityChange(Priority.HIGH)

            assertEquals(Priority.HIGH, viewModel.uiState.value.taskPriority)
        }

    @Test
    fun `onDueDateChange updates due date`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Task"))

            val viewModel = createViewModel(taskId = 1L)
            advanceUntilIdle()

            viewModel.onDueDateChange(20500L)

            assertEquals(20500L, viewModel.uiState.value.taskDueDate)
        }

    @Test
    fun `onCategoryChange updates category`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Task"))

            val viewModel = createViewModel(taskId = 1L)
            advanceUntilIdle()

            viewModel.onCategoryChange("Personal")

            assertEquals("Personal", viewModel.uiState.value.taskCategory)
        }

    @Test
    fun `saveTask saves all fields to repository`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Original"))

            val viewModel = createViewModel(taskId = 1L)
            advanceUntilIdle()

            viewModel.onTitleChange("Updated Title")
            viewModel.onDescriptionChange("Updated Desc")
            viewModel.onPriorityChange(Priority.LOW)
            viewModel.onDueDateChange(19800L)
            viewModel.onCategoryChange("Personal")

            viewModel.saveTask()
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.isSaved)
            val storedTask = repository.getAllTasks().first().first()
            assertEquals("Updated Title", storedTask.title)
            assertEquals("Updated Desc", storedTask.description)
            assertEquals(Priority.LOW, storedTask.priority)
            assertEquals(19800L, storedTask.dueDate)
            assertEquals("Personal", storedTask.category)
        }

    @Test
    fun `saveTask converts blank description to null`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Task", description = "Old"))

            val viewModel = createViewModel(taskId = 1L)
            advanceUntilIdle()

            viewModel.onDescriptionChange("   ")

            viewModel.saveTask()
            advanceUntilIdle()

            val storedTask = repository.getAllTasks().first().first()
            assertNull(storedTask.description)
        }

    @Test
    fun `saveTask converts blank category to null`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Task", category = "Work"))

            val viewModel = createViewModel(taskId = 1L)
            advanceUntilIdle()

            viewModel.onCategoryChange("   ")

            viewModel.saveTask()
            advanceUntilIdle()

            val storedTask = repository.getAllTasks().first().first()
            assertNull(storedTask.category)
        }
}
