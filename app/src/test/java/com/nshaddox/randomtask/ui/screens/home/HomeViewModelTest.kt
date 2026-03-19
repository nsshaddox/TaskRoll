package com.nshaddox.randomtask.ui.screens.home

import app.cash.turbine.test
import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.domain.usecase.AddTaskUseCase
import com.nshaddox.randomtask.domain.usecase.CompleteTaskUseCase
import com.nshaddox.randomtask.domain.usecase.FakeTaskRepository
import com.nshaddox.randomtask.domain.usecase.GetRandomTaskUseCase
import com.nshaddox.randomtask.domain.usecase.GetTaskMetricsUseCase
import com.nshaddox.randomtask.domain.usecase.GetWeightedRandomTaskUseCase
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeTaskRepository
    private lateinit var getRandomTaskUseCase: GetRandomTaskUseCase
    private lateinit var getWeightedRandomTaskUseCase: GetWeightedRandomTaskUseCase
    private lateinit var completeTaskUseCase: CompleteTaskUseCase
    private lateinit var addTaskUseCase: AddTaskUseCase
    private lateinit var getTaskMetricsUseCase: GetTaskMetricsUseCase
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTaskRepository()
        getRandomTaskUseCase = GetRandomTaskUseCase(repository)
        getWeightedRandomTaskUseCase = GetWeightedRandomTaskUseCase(repository)
        completeTaskUseCase = CompleteTaskUseCase(repository)
        addTaskUseCase = AddTaskUseCase(repository)
        getTaskMetricsUseCase = GetTaskMetricsUseCase(repository)
        viewModel =
            HomeViewModel(
                getRandomTaskUseCase = getRandomTaskUseCase,
                getWeightedRandomTaskUseCase = getWeightedRandomTaskUseCase,
                completeTaskUseCase = completeTaskUseCase,
                addTaskUseCase = addTaskUseCase,
                getTaskMetricsUseCase = getTaskMetricsUseCase,
                ioDispatcher = testDispatcher,
            )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has null task, not loading, no error`() =
        runTest(testDispatcher) {
            viewModel.uiState.test {
                val initial = awaitItem()
                assertNull(initial.currentTask)
                assertFalse(initial.isLoading)
                assertNull(initial.error)
                assertFalse(initial.noTasksAvailable)
                assertFalse(initial.useWeightedRandom)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `pickRandom returns a task when incomplete tasks exist`() =
        runTest(testDispatcher) {
            repository.addTask(Task(title = "Task 1", createdAt = 1000L, updatedAt = 1000L))

            viewModel.pickRandom()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertNotNull(state.currentTask)
            assertEquals("Task 1", state.currentTask!!.title)
            assertFalse(state.isLoading)
            assertFalse(state.noTasksAvailable)
        }

    @Test
    fun `pickRandom sets noTasksAvailable when no incomplete tasks exist`() =
        runTest(testDispatcher) {
            viewModel.pickRandom()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertNull(state.currentTask)
            assertFalse(state.isLoading)
            assertTrue(state.noTasksAvailable)
        }

    @Test
    fun `completeTask sets taskCompleted flag and clears currentTask on success`() =
        runTest(testDispatcher) {
            repository.addTask(Task(title = "Task 1", createdAt = 1000L, updatedAt = 1000L))

            viewModel.pickRandom()
            advanceUntilIdle()
            assertNotNull(viewModel.uiState.value.currentTask)

            viewModel.completeTask()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertTrue(state.taskCompleted)
            assertNull(state.currentTask)
        }

    @Test
    fun `completeTask does nothing when no current task`() =
        runTest(testDispatcher) {
            val initialState = viewModel.uiState.value

            viewModel.completeTask()
            advanceUntilIdle()

            assertEquals(initialState, viewModel.uiState.value)
        }

    @Test
    fun `completeTask sets error on failure`() =
        runTest(testDispatcher) {
            val task = Task(id = 999L, title = "Ghost Task", createdAt = 1000L, updatedAt = 1000L)
            repository.addTask(task)

            viewModel.pickRandom()
            advanceUntilIdle()
            assertNotNull(viewModel.uiState.value.currentTask)

            // Delete the task so completeTask (which calls updateTask) will fail
            repository.deleteTask(viewModel.uiState.value.currentTask!!)
            advanceUntilIdle()

            viewModel.completeTask()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertNotNull(state.error)
            assertFalse(state.taskCompleted)
        }

    @Test
    fun `skipTask loads a new random task`() =
        runTest(testDispatcher) {
            repository.addTask(Task(title = "Task 1", createdAt = 1000L, updatedAt = 1000L))

            viewModel.pickRandom()
            advanceUntilIdle()
            assertNotNull(viewModel.uiState.value.currentTask)

            viewModel.skipTask()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertNotNull(state.currentTask)
            assertFalse(state.isLoading)
        }

    @Test
    fun `skipTask sets noTasksAvailable when no tasks exist`() =
        runTest(testDispatcher) {
            viewModel.skipTask()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertNull(state.currentTask)
            assertFalse(state.isLoading)
            assertTrue(state.noTasksAvailable)
        }

    @Test
    fun `skipTask with weighted random loads a new task`() =
        runTest(testDispatcher) {
            repository.addTask(
                Task(
                    title = "Weighted Task",
                    createdAt = 1000L,
                    updatedAt = 1000L,
                    priority = Priority.HIGH,
                ),
            )

            viewModel.toggleWeightedRandom()
            assertTrue(viewModel.uiState.value.useWeightedRandom)

            viewModel.skipTask()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertNotNull(state.currentTask)
            assertEquals("Weighted Task", state.currentTask!!.title)
            assertFalse(state.isLoading)
        }

    @Test
    fun `toggleWeightedRandom flips from false to true`() =
        runTest(testDispatcher) {
            assertFalse(viewModel.uiState.value.useWeightedRandom)

            viewModel.toggleWeightedRandom()

            assertTrue(viewModel.uiState.value.useWeightedRandom)
        }

    @Test
    fun `toggleWeightedRandom called twice flips back to false`() =
        runTest(testDispatcher) {
            viewModel.toggleWeightedRandom()
            assertTrue(viewModel.uiState.value.useWeightedRandom)

            viewModel.toggleWeightedRandom()
            assertFalse(viewModel.uiState.value.useWeightedRandom)
        }

    @Test
    fun `pickRandom uses weighted use case when useWeightedRandom is true`() =
        runTest(testDispatcher) {
            repository.addTask(
                Task(
                    title = "High Task",
                    createdAt = 1000L,
                    updatedAt = 1000L,
                    priority = Priority.HIGH,
                ),
            )

            viewModel.toggleWeightedRandom()
            assertTrue(viewModel.uiState.value.useWeightedRandom)

            viewModel.pickRandom()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertNotNull(state.currentTask)
            assertEquals("High Task", state.currentTask!!.title)
        }

    @Test
    fun `addTask with valid title succeeds`() =
        runTest(testDispatcher) {
            viewModel.addTask("New Task", "Description")
            advanceUntilIdle()

            val tasks = repository.getAllTasksSnapshot()
            assertEquals(1, tasks.size)
            assertEquals("New Task", tasks[0].title)
            assertEquals("Description", tasks[0].description)
        }

    @Test
    fun `addTask with null description succeeds`() =
        runTest(testDispatcher) {
            viewModel.addTask("Task No Desc", null)
            advanceUntilIdle()

            val tasks = repository.getAllTasksSnapshot()
            assertEquals(1, tasks.size)
            assertEquals("Task No Desc", tasks[0].title)
            assertNull(tasks[0].description)
            assertNull(viewModel.uiState.value.error)
        }

    @Test
    fun `addTask with blank title sets error`() =
        runTest(testDispatcher) {
            viewModel.addTask("  ", null)
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertNotNull(state.error)
        }

    @Test
    fun `resetTaskCompleted clears the flag`() =
        runTest(testDispatcher) {
            repository.addTask(Task(title = "Task 1", createdAt = 1000L, updatedAt = 1000L))

            viewModel.pickRandom()
            advanceUntilIdle()
            viewModel.completeTask()
            advanceUntilIdle()
            assertTrue(viewModel.uiState.value.taskCompleted)

            viewModel.resetTaskCompleted()

            assertFalse(viewModel.uiState.value.taskCompleted)
        }

    @Test
    fun `metrics are loaded from repository`() =
        runTest(testDispatcher) {
            repository.addTask(Task(title = "Incomplete", createdAt = 1000L, updatedAt = 1000L))
            repository.addTask(
                Task(
                    title = "Complete",
                    isCompleted = true,
                    createdAt = 2000L,
                    updatedAt = 2000L,
                ),
            )

            advanceUntilIdle()

            val metrics = viewModel.uiState.value.metrics
            assertEquals(1, metrics.totalRemaining)
        }
}
