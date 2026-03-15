package com.nshaddox.randomtask.ui.screens.completedtasks

import app.cash.turbine.test
import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.domain.usecase.DeleteTaskUseCase
import com.nshaddox.randomtask.domain.usecase.FakeTaskRepository
import com.nshaddox.randomtask.domain.usecase.GetCompletedTasksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class CompletedTasksViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeTaskRepository
    private lateinit var getCompletedTasksUseCase: GetCompletedTasksUseCase
    private lateinit var deleteTaskUseCase: DeleteTaskUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTaskRepository()
        getCompletedTasksUseCase = GetCompletedTasksUseCase(repository)
        deleteTaskUseCase = DeleteTaskUseCase(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() =
        CompletedTasksViewModel(
            getCompletedTasksUseCase = getCompletedTasksUseCase,
            deleteTaskUseCase = deleteTaskUseCase,
            ioDispatcher = testDispatcher,
        )

    private fun createTask(
        id: Long = 0,
        title: String = "Test Task",
        isCompleted: Boolean = true,
    ) = Task(
        id = id,
        title = title,
        isCompleted = isCompleted,
        createdAt = 1000L,
        updatedAt = 1000L,
    )

    @Test
    fun `initial state is loading then transitions to empty list`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Initial state from MutableStateFlow default
                val initial = awaitItem()
                assertTrue(initial.isLoading)
                assertTrue(initial.tasks.isEmpty())

                // After init block collects from repository
                val loaded = awaitItem()
                assertFalse(loaded.isLoading)
                assertTrue(loaded.tasks.isEmpty())
            }
        }

    @Test
    fun `initial state transitions to populated completed tasks list`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Completed Task 1", isCompleted = true))
            repository.addTask(createTask(title = "Completed Task 2", isCompleted = true))
            // Add an incomplete task to verify it is filtered out
            repository.addTask(createTask(title = "Incomplete Task", isCompleted = false))
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Initial loading state
                val initial = awaitItem()
                assertTrue(initial.isLoading)

                // Loaded state with only completed tasks
                val loaded = awaitItem()
                assertFalse(loaded.isLoading)
                assertEquals(2, loaded.tasks.size)
                assertTrue(loaded.tasks.all { it.isCompleted })
            }
        }

    @Test
    fun `deleteTask success removes task via repository flow update`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Task to delete", isCompleted = true))
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Initial loading
                awaitItem()
                // Loaded with task
                val loaded = awaitItem()
                assertEquals(1, loaded.tasks.size)
                val taskToDelete = loaded.tasks[0]

                viewModel.deleteTask(taskToDelete)

                val afterDelete = awaitItem()
                assertTrue(afterDelete.tasks.isEmpty())
            }
        }

    @Test
    fun `deleteTask failure sets errorMessage`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Initial loading
                awaitItem()
                // Loaded empty
                awaitItem()

                viewModel.deleteTask(createTask(id = 999, title = "Nonexistent"))

                val errorState = awaitItem()
                assertEquals("Task not found", errorState.errorMessage)
            }
        }

    @Test
    fun `clearError resets errorMessage to null`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Initial loading
                awaitItem()
                // Loaded empty
                awaitItem()

                // Trigger an error by deleting a non-existent task
                viewModel.deleteTask(createTask(id = 999, title = "Nonexistent"))

                val errorState = awaitItem()
                assertEquals("Task not found", errorState.errorMessage)

                viewModel.clearError()

                val clearedState = awaitItem()
                assertNull(clearedState.errorMessage)
            }
        }
}
