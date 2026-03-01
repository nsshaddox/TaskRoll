package com.nshaddox.randomtask.ui.screens.randomtask

import app.cash.turbine.test
import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.domain.repository.TaskRepository
import com.nshaddox.randomtask.domain.usecase.CompleteTaskUseCase
import com.nshaddox.randomtask.domain.usecase.FakeTaskRepository
import com.nshaddox.randomtask.domain.usecase.GetRandomTaskUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
class RandomTaskViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeTaskRepository
    private lateinit var getRandomTaskUseCase: GetRandomTaskUseCase
    private lateinit var completeTaskUseCase: CompleteTaskUseCase
    private lateinit var viewModel: RandomTaskViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTaskRepository()
        getRandomTaskUseCase = GetRandomTaskUseCase(repository)
        completeTaskUseCase = CompleteTaskUseCase(repository)
        viewModel = RandomTaskViewModel(getRandomTaskUseCase, completeTaskUseCase, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has null task, not loading, no error`() = runTest(testDispatcher) {
        viewModel.uiState.test {
            val initial = awaitItem()
            assertNull(initial.currentTask)
            assertFalse(initial.isLoading)
            assertNull(initial.error)
            assertFalse(initial.noTasksAvailable)
        }
    }

    @Test
    fun `loadRandomTask returns a task when incomplete tasks exist`() = runTest(testDispatcher) {
        repository.addTask(Task(title = "Task 1", createdAt = 1000L, updatedAt = 1000L))

        viewModel.loadRandomTask()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.currentTask)
        assertEquals("Task 1", state.currentTask!!.title)
        assertFalse(state.isLoading)
        assertFalse(state.noTasksAvailable)
    }

    @Test
    fun `loadRandomTask sets noTasksAvailable when no incomplete tasks exist`() = runTest(testDispatcher) {
        viewModel.loadRandomTask()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.currentTask)
        assertFalse(state.isLoading)
        assertTrue(state.noTasksAvailable)
    }

    @Test
    fun `completeTask sets taskCompleted flag to true on success`() = runTest(testDispatcher) {
        repository.addTask(Task(title = "Task 1", createdAt = 1000L, updatedAt = 1000L))

        viewModel.loadRandomTask()
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.currentTask)

        viewModel.completeTask()
        advanceUntilIdle()

        val afterComplete = viewModel.uiState.value
        assertFalse(afterComplete.isLoading)
        assertTrue(afterComplete.taskCompleted)
    }

    @Test
    fun `resetTaskCompleted clears the taskCompleted flag`() = runTest(testDispatcher) {
        repository.addTask(Task(title = "Task 1", createdAt = 1000L, updatedAt = 1000L))

        viewModel.loadRandomTask()
        advanceUntilIdle()

        viewModel.completeTask()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.taskCompleted)

        viewModel.resetTaskCompleted()

        assertFalse(viewModel.uiState.value.taskCompleted)
    }

    @Test
    fun `completeTask failure does not set taskCompleted flag`() = runTest(testDispatcher) {
        val errorRepository = object : TaskRepository {
            override fun getAllTasks(): Flow<List<Task>> = flow { emit(emptyList()) }
            override fun getIncompleteTasks(): Flow<List<Task>> = flow {
                emit(listOf(Task(id = 1, title = "Task 1", createdAt = 1000L, updatedAt = 1000L)))
            }
            override fun getTaskById(id: Long): Flow<Task?> = flow { emit(null) }
            override suspend fun addTask(task: Task): Result<Long> = Result.success(1L)
            override suspend fun updateTask(task: Task): Result<Unit> = Result.failure(RuntimeException("Update failed"))
            override suspend fun deleteTask(task: Task): Result<Unit> = Result.failure(RuntimeException("Delete failed"))
        }
        val errorGetUseCase = GetRandomTaskUseCase(errorRepository)
        val errorCompleteUseCase = CompleteTaskUseCase(errorRepository)
        val errorViewModel = RandomTaskViewModel(errorGetUseCase, errorCompleteUseCase, testDispatcher)

        errorViewModel.loadRandomTask()
        advanceUntilIdle()

        assertNotNull(errorViewModel.uiState.value.currentTask)

        errorViewModel.completeTask()
        advanceUntilIdle()

        val state = errorViewModel.uiState.value
        assertFalse(state.taskCompleted)
        assertNotNull(state.error)
    }

    @Test
    fun `completeTask does nothing when no current task`() = runTest(testDispatcher) {
        val initialState = viewModel.uiState.value

        viewModel.completeTask()
        advanceUntilIdle()

        // State should remain unchanged
        assertEquals(initialState, viewModel.uiState.value)
    }

    @Test
    fun `skipTask loads a new random task without completing current one`() = runTest(testDispatcher) {
        repository.addTask(Task(title = "Task 1", createdAt = 1000L, updatedAt = 1000L))

        viewModel.loadRandomTask()
        advanceUntilIdle()

        val loadedState = viewModel.uiState.value
        assertNotNull(loadedState.currentTask)

        viewModel.skipTask()
        advanceUntilIdle()

        val afterSkip = viewModel.uiState.value
        assertNotNull(afterSkip.currentTask)
        assertFalse(afterSkip.isLoading)
        // The task should still be incomplete since skip doesn't complete it
        assertFalse(afterSkip.currentTask!!.isCompleted)
    }

    @Test
    fun `error handling when GetRandomTaskUseCase throws an exception`() = runTest(testDispatcher) {
        val errorRepository = object : TaskRepository {
            override fun getAllTasks(): Flow<List<Task>> = flow { throw RuntimeException("DB error") }
            override fun getIncompleteTasks(): Flow<List<Task>> = flow { throw RuntimeException("DB error") }
            override fun getTaskById(id: Long): Flow<Task?> = flow { throw RuntimeException("DB error") }
            override suspend fun addTask(task: Task): Result<Long> = Result.failure(RuntimeException("DB error"))
            override suspend fun updateTask(task: Task): Result<Unit> = Result.failure(RuntimeException("DB error"))
            override suspend fun deleteTask(task: Task): Result<Unit> = Result.failure(RuntimeException("DB error"))
        }
        val errorUseCase = GetRandomTaskUseCase(errorRepository)
        val errorViewModel = RandomTaskViewModel(errorUseCase, completeTaskUseCase, testDispatcher)

        errorViewModel.loadRandomTask()
        advanceUntilIdle()

        val state = errorViewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
        assertEquals("DB error", state.error)
    }
}
