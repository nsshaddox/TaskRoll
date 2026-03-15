package com.nshaddox.randomtask.ui.screens.tasklist

import app.cash.turbine.test
import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.domain.usecase.AddTaskUseCase
import com.nshaddox.randomtask.domain.usecase.CompleteTaskUseCase
import com.nshaddox.randomtask.domain.usecase.DeleteTaskUseCase
import com.nshaddox.randomtask.domain.usecase.FakeTaskRepository
import com.nshaddox.randomtask.domain.usecase.GetTasksUseCase
import com.nshaddox.randomtask.domain.usecase.UpdateTaskUseCase
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
class TaskListViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeTaskRepository
    private lateinit var getTasksUseCase: GetTasksUseCase
    private lateinit var addTaskUseCase: AddTaskUseCase
    private lateinit var deleteTaskUseCase: DeleteTaskUseCase
    private lateinit var completeTaskUseCase: CompleteTaskUseCase
    private lateinit var updateTaskUseCase: UpdateTaskUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTaskRepository()
        getTasksUseCase = GetTasksUseCase(repository)
        addTaskUseCase = AddTaskUseCase(repository)
        deleteTaskUseCase = DeleteTaskUseCase(repository)
        completeTaskUseCase = CompleteTaskUseCase(repository)
        updateTaskUseCase = UpdateTaskUseCase(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() =
        TaskListViewModel(
            getTasksUseCase = getTasksUseCase,
            addTaskUseCase = addTaskUseCase,
            deleteTaskUseCase = deleteTaskUseCase,
            completeTaskUseCase = completeTaskUseCase,
            updateTaskUseCase = updateTaskUseCase,
            ioDispatcher = testDispatcher,
        )

    private fun createTask(
        id: Long = 0,
        title: String = "Test Task",
        isCompleted: Boolean = false,
    ) = Task(
        id = id,
        title = title,
        isCompleted = isCompleted,
        createdAt = 1000L,
        updatedAt = 1000L,
    )

    @Test
    fun `initial state is loading then transitions to empty task list`() =
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
    fun `initial state transitions to tasks from repository`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Task 1"))
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Initial loading state
                val initial = awaitItem()
                assertTrue(initial.isLoading)

                // Loaded state with tasks
                val loaded = awaitItem()
                assertFalse(loaded.isLoading)
                assertEquals(1, loaded.tasks.size)
                assertEquals("Task 1", loaded.tasks[0].title)
            }
        }

    @Test
    fun `addTask success updates task list via repository flow`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Initial loading
                awaitItem()
                // Loaded empty
                awaitItem()

                viewModel.addTask("New Task")

                // StateFlow may conflate the dialog-close and task-list-update emissions.
                // Consume items until we see the task list populated.
                val updated = awaitItem()
                if (updated.tasks.isEmpty()) {
                    // Dialog close emitted first; next emission has the task
                    val withTask = awaitItem()
                    assertEquals(1, withTask.tasks.size)
                    assertEquals("New Task", withTask.tasks[0].title)
                } else {
                    assertEquals(1, updated.tasks.size)
                    assertEquals("New Task", updated.tasks[0].title)
                }
            }
        }

    @Test
    fun `addTask with blank title sets error message`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Initial loading
                awaitItem()
                // Loaded empty
                awaitItem()

                viewModel.addTask("   ")

                val errorState = awaitItem()
                assertEquals("Task title cannot be blank", errorState.errorMessage)
            }
        }

    @Test
    fun `deleteTask success removes task from list`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Task to delete"))
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
    fun `deleteTask failure sets error message`() =
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
    fun `toggleTaskCompletion for incomplete task calls CompleteTaskUseCase`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Incomplete Task", isCompleted = false))
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Initial loading
                awaitItem()
                // Loaded with task
                val loaded = awaitItem()
                val incompleteTask = loaded.tasks[0]
                assertFalse(incompleteTask.isCompleted)

                viewModel.toggleTaskCompletion(incompleteTask)

                val updated = awaitItem()
                assertTrue(updated.tasks[0].isCompleted)
            }
        }

    @Test
    fun `toggleTaskCompletion for completed task calls UpdateTaskUseCase`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Completed Task", isCompleted = true))
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Initial loading
                awaitItem()
                // Loaded with task
                val loaded = awaitItem()
                val completedTask = loaded.tasks[0]
                assertTrue(completedTask.isCompleted)

                viewModel.toggleTaskCompletion(completedTask)

                val updated = awaitItem()
                assertFalse(updated.tasks[0].isCompleted)
            }
        }

    @Test
    fun `showAddDialog sets isAddDialogVisible to true`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()

            viewModel.uiState.test {
                val initial = awaitItem()
                assertFalse(initial.isAddDialogVisible)

                viewModel.showAddDialog()

                val dialogVisible = awaitItem()
                assertTrue(dialogVisible.isAddDialogVisible)
            }
        }

    @Test
    fun `hideAddDialog sets isAddDialogVisible to false`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            viewModel.showAddDialog()

            viewModel.uiState.test {
                val withDialog = awaitItem()
                assertTrue(withDialog.isAddDialogVisible)

                viewModel.hideAddDialog()

                val withoutDialog = awaitItem()
                assertFalse(withoutDialog.isAddDialogVisible)
            }
        }

    @Test
    fun `addTask with description passes description to task`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Initial loading
                awaitItem()
                // Loaded empty
                awaitItem()

                viewModel.addTask("Task With Desc", "A detailed description")

                // StateFlow may conflate the dialog-close and task-list-update emissions.
                val updated = awaitItem()
                if (updated.tasks.isEmpty()) {
                    val withTask = awaitItem()
                    assertEquals(1, withTask.tasks.size)
                    assertEquals("Task With Desc", withTask.tasks[0].title)
                    assertEquals("A detailed description", withTask.tasks[0].description)
                } else {
                    assertEquals(1, updated.tasks.size)
                    assertEquals("Task With Desc", updated.tasks[0].title)
                    assertEquals("A detailed description", updated.tasks[0].description)
                }
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

                // Trigger an error by adding a blank task
                viewModel.addTask("")

                val errorState = awaitItem()
                assertEquals("Task title cannot be blank", errorState.errorMessage)

                viewModel.clearError()

                val clearedState = awaitItem()
                assertNull(clearedState.errorMessage)
            }
        }
}
