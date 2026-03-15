package com.nshaddox.randomtask.ui.screens.taskeditor

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.domain.usecase.FakeTaskRepository
import com.nshaddox.randomtask.domain.usecase.UpdateTaskUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
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
    private lateinit var updateTaskUseCase: UpdateTaskUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTaskRepository()
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
            ioDispatcher = testDispatcher,
        )
    }

    private fun createTask(
        id: Long = 0,
        title: String = "Test Task",
    ) = Task(
        id = id,
        title = title,
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
                assertEquals("Task not found", loaded.errorMessage)
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
    fun `saveTask with blank title sets error message`() =
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
                assertEquals("Task title cannot be blank", errorState.errorMessage)
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
}
