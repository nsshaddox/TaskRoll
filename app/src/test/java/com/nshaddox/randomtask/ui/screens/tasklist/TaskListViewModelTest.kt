package com.nshaddox.randomtask.ui.screens.tasklist

import app.cash.turbine.test
import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.SortOrder
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
        priority: Priority = Priority.MEDIUM,
        dueDate: Long? = null,
        category: String? = null,
    ) = Task(
        id = id,
        title = title,
        isCompleted = isCompleted,
        createdAt = 1000L,
        updatedAt = 1000L,
        priority = priority,
        dueDate = dueDate,
        category = category,
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

    // === Task 1.0: Search query tests ===

    @Test
    fun `updateSearchQuery with matching query emits only matching tasks`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Buy groceries"))
            repository.addTask(createTask(title = "Walk the dog"))
            repository.addTask(createTask(title = "Buy new shoes"))
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Initial loading
                awaitItem()
                // Loaded with 3 tasks
                val loaded = awaitItem()
                assertEquals(3, loaded.tasks.size)

                viewModel.updateSearchQuery("Buy")
                advanceUntilIdle()

                val filtered = awaitItem()
                assertEquals(2, filtered.tasks.size)
                assertTrue(filtered.tasks.all { it.title.contains("Buy") })
            }
        }

    @Test
    fun `updateSearchQuery with blank string restores full unfiltered task list`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Buy groceries"))
            repository.addTask(createTask(title = "Walk the dog"))
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Initial loading
                awaitItem()
                // Loaded with 2 tasks
                val loaded = awaitItem()
                assertEquals(2, loaded.tasks.size)

                viewModel.updateSearchQuery("Buy")
                advanceUntilIdle()

                val filtered = awaitItem()
                assertEquals(1, filtered.tasks.size)

                viewModel.updateSearchQuery("")
                advanceUntilIdle()

                val restored = awaitItem()
                assertEquals(2, restored.tasks.size)
            }
        }

    @Test
    fun `updateSearchQuery updates searchQuery field in uiState`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Initial loading
                awaitItem()
                // Loaded empty
                awaitItem()

                viewModel.updateSearchQuery("test query")
                advanceUntilIdle()

                val updated = awaitItem()
                assertEquals("test query", updated.searchQuery)
            }
        }

    // === Task 2.0: Filter priority, filter category, compound filter tests ===

    @Test
    fun `setFilterPriority with a priority emits only tasks of that priority`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Low task", priority = Priority.LOW))
            repository.addTask(createTask(title = "High task", priority = Priority.HIGH))
            repository.addTask(createTask(title = "Another low", priority = Priority.LOW))
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Initial loading
                awaitItem()
                // Loaded with 3 tasks
                val loaded = awaitItem()
                assertEquals(3, loaded.tasks.size)

                viewModel.setFilterPriority(Priority.LOW)
                advanceUntilIdle()

                val filtered = awaitItem()
                assertEquals(2, filtered.tasks.size)
                assertTrue(filtered.tasks.all { it.priority == Priority.LOW })
            }
        }

    @Test
    fun `setFilterPriority null clears priority filter and restores full list`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Low task", priority = Priority.LOW))
            repository.addTask(createTask(title = "High task", priority = Priority.HIGH))
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Initial loading
                awaitItem()
                // Loaded with 2 tasks
                val loaded = awaitItem()
                assertEquals(2, loaded.tasks.size)

                viewModel.setFilterPriority(Priority.LOW)
                advanceUntilIdle()

                val filtered = awaitItem()
                assertEquals(1, filtered.tasks.size)

                viewModel.setFilterPriority(null)
                advanceUntilIdle()

                val restored = awaitItem()
                assertEquals(2, restored.tasks.size)
            }
        }

    @Test
    fun `setFilterCategory with a category emits only tasks of that category`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Work task", category = "Work"))
            repository.addTask(createTask(title = "Home task", category = "Home"))
            repository.addTask(createTask(title = "Another work", category = "Work"))
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Initial loading
                awaitItem()
                // Loaded with 3 tasks
                val loaded = awaitItem()
                assertEquals(3, loaded.tasks.size)

                viewModel.setFilterCategory("Work")
                advanceUntilIdle()

                val filtered = awaitItem()
                assertEquals(2, filtered.tasks.size)
                assertTrue(filtered.tasks.all { it.category == "Work" })
            }
        }

    @Test
    fun `setFilterCategory null clears category filter`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Work task", category = "Work"))
            repository.addTask(createTask(title = "Home task", category = "Home"))
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Initial loading
                awaitItem()
                // Loaded with 2 tasks
                val loaded = awaitItem()
                assertEquals(2, loaded.tasks.size)

                viewModel.setFilterCategory("Work")
                advanceUntilIdle()

                val filtered = awaitItem()
                assertEquals(1, filtered.tasks.size)

                viewModel.setFilterCategory(null)
                advanceUntilIdle()

                val restored = awaitItem()
                assertEquals(2, restored.tasks.size)
            }
        }

    @Test
    fun `combined priority and search stacks both filters`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Buy groceries", priority = Priority.HIGH))
            repository.addTask(createTask(title = "Buy shoes", priority = Priority.LOW))
            repository.addTask(createTask(title = "Walk the dog", priority = Priority.HIGH))
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Initial loading
                awaitItem()
                // Loaded with 3 tasks
                val loaded = awaitItem()
                assertEquals(3, loaded.tasks.size)

                viewModel.updateSearchQuery("Buy")
                advanceUntilIdle()

                val searchOnly = awaitItem()
                assertEquals(2, searchOnly.tasks.size)

                viewModel.setFilterPriority(Priority.HIGH)
                advanceUntilIdle()

                val combined = awaitItem()
                assertEquals(1, combined.tasks.size)
                assertEquals("Buy groceries", combined.tasks[0].title)
                assertEquals(Priority.HIGH, combined.tasks[0].priority)
            }
        }

    @Test
    fun `combined category and sort applies both dimensions`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Zebra task", category = "Work"))
            repository.addTask(createTask(title = "Apple task", category = "Work"))
            repository.addTask(createTask(title = "Mango task", category = "Home"))
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Initial loading
                awaitItem()
                // Loaded with 3 tasks
                val loaded = awaitItem()
                assertEquals(3, loaded.tasks.size)

                viewModel.setFilterCategory("Work")
                advanceUntilIdle()

                val filtered = awaitItem()
                assertEquals(2, filtered.tasks.size)

                viewModel.setSortOrder(SortOrder.TITLE_ASC)
                advanceUntilIdle()

                val sorted = awaitItem()
                assertEquals(2, sorted.tasks.size)
                assertEquals("Apple task", sorted.tasks[0].title)
                assertEquals("Zebra task", sorted.tasks[1].title)
            }
        }

    @Test
    fun `availableCategories is derived from full unfiltered task list`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Work task", category = "Work"))
            repository.addTask(createTask(title = "Home task", category = "Home"))
            repository.addTask(createTask(title = "No category task"))
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Initial loading
                awaitItem()
                // Loaded with 3 tasks
                val loaded = awaitItem()
                assertEquals(3, loaded.tasks.size)
                assertTrue(loaded.availableCategories.contains("Work"))
                assertTrue(loaded.availableCategories.contains("Home"))
                assertEquals(2, loaded.availableCategories.size)

                // Apply category filter; availableCategories should still contain all categories
                viewModel.setFilterCategory("Work")
                advanceUntilIdle()

                val filtered = awaitItem()
                assertEquals(1, filtered.tasks.size)
                // availableCategories still derived from full list
                assertTrue(filtered.availableCategories.contains("Work"))
                assertTrue(filtered.availableCategories.contains("Home"))
                assertEquals(2, filtered.availableCategories.size)
            }
        }

    // === Task 3.0: Sort order and addTask extended signature tests ===

    @Test
    fun `setSortOrder TITLE_ASC reorders tasks alphabetically`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Charlie"))
            repository.addTask(createTask(title = "Alpha"))
            repository.addTask(createTask(title = "Bravo"))
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Initial loading
                awaitItem()
                // Loaded with 3 tasks (default sort: CREATED_DATE_DESC)
                awaitItem()

                viewModel.setSortOrder(SortOrder.TITLE_ASC)
                advanceUntilIdle()

                val sorted = awaitItem()
                assertEquals("Alpha", sorted.tasks[0].title)
                assertEquals("Bravo", sorted.tasks[1].title)
                assertEquals("Charlie", sorted.tasks[2].title)
            }
        }

    @Test
    fun `setSortOrder PRIORITY_DESC reorders tasks by priority descending`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "Low task", priority = Priority.LOW))
            repository.addTask(createTask(title = "High task", priority = Priority.HIGH))
            repository.addTask(createTask(title = "Medium task", priority = Priority.MEDIUM))
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Initial loading
                awaitItem()
                // Loaded with 3 tasks
                awaitItem()

                viewModel.setSortOrder(SortOrder.PRIORITY_DESC)
                advanceUntilIdle()

                val sorted = awaitItem()
                assertEquals(Priority.HIGH, sorted.tasks[0].priority)
                assertEquals(Priority.MEDIUM, sorted.tasks[1].priority)
                assertEquals(Priority.LOW, sorted.tasks[2].priority)
            }
        }

    @Test
    fun `setSortOrder DUE_DATE_ASC places null dueDate tasks last`() =
        runTest(testDispatcher) {
            repository.addTask(createTask(title = "No due date", dueDate = null))
            repository.addTask(createTask(title = "Due soon", dueDate = 100L))
            repository.addTask(createTask(title = "Due later", dueDate = 200L))
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Initial loading
                awaitItem()
                // Loaded with 3 tasks
                awaitItem()

                viewModel.setSortOrder(SortOrder.DUE_DATE_ASC)
                advanceUntilIdle()

                val sorted = awaitItem()
                assertEquals("Due soon", sorted.tasks[0].title)
                assertEquals("Due later", sorted.tasks[1].title)
                assertEquals("No due date", sorted.tasks[2].title)
            }
        }

    @Test
    fun `addTask with priority dueDate and category persists those values`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Initial loading
                awaitItem()
                // Loaded empty
                awaitItem()

                viewModel.addTask(
                    title = "Important task",
                    description = "Do this",
                    priority = Priority.HIGH,
                    dueDate = 500L,
                    category = "Work",
                )
                advanceUntilIdle()

                // Consume items until we see the task
                var state = awaitItem()
                if (state.tasks.isEmpty()) {
                    state = awaitItem()
                }
                assertEquals(1, state.tasks.size)
                val task = state.tasks[0]
                assertEquals("Important task", task.title)
                assertEquals("Do this", task.description)
                assertEquals(Priority.HIGH, task.priority)
                assertEquals(500L, task.dueDate)
                assertEquals("Work", task.category)
            }
        }

    @Test
    fun `addTask without new parameters defaults to MEDIUM priority null dueDate null category`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Initial loading
                awaitItem()
                // Loaded empty
                awaitItem()

                viewModel.addTask(title = "Simple task")
                advanceUntilIdle()

                // Consume items until we see the task
                var state = awaitItem()
                if (state.tasks.isEmpty()) {
                    state = awaitItem()
                }
                assertEquals(1, state.tasks.size)
                val task = state.tasks[0]
                assertEquals("Simple task", task.title)
                assertEquals(Priority.MEDIUM, task.priority)
                assertNull(task.dueDate)
                assertNull(task.category)
            }
        }
}
