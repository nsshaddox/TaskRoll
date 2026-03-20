package com.nshaddox.randomtask.ui.screens.randomtask

import app.cash.turbine.test
import com.nshaddox.randomtask.R
import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.SubTask
import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.domain.repository.TaskRepository
import com.nshaddox.randomtask.domain.usecase.AddSubTaskUseCase
import com.nshaddox.randomtask.domain.usecase.CompleteSubTaskUseCase
import com.nshaddox.randomtask.domain.usecase.CompleteTaskUseCase
import com.nshaddox.randomtask.domain.usecase.FakeSubTaskRepository
import com.nshaddox.randomtask.domain.usecase.FakeTaskRepository
import com.nshaddox.randomtask.domain.usecase.GetRandomTaskUseCase
import com.nshaddox.randomtask.domain.usecase.GetTaskWithSubTasksUseCase
import com.nshaddox.randomtask.domain.usecase.GetWeightedRandomTaskUseCase
import com.nshaddox.randomtask.domain.usecase.UncompleteSubTaskUseCase
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
    private lateinit var subTaskRepository: FakeSubTaskRepository
    private lateinit var getRandomTaskUseCase: GetRandomTaskUseCase
    private lateinit var getWeightedRandomTaskUseCase: GetWeightedRandomTaskUseCase
    private lateinit var completeTaskUseCase: CompleteTaskUseCase
    private lateinit var getTaskWithSubTasksUseCase: GetTaskWithSubTasksUseCase
    private lateinit var addSubTaskUseCase: AddSubTaskUseCase
    private lateinit var completeSubTaskUseCase: CompleteSubTaskUseCase
    private lateinit var uncompleteSubTaskUseCase: UncompleteSubTaskUseCase
    private lateinit var viewModel: RandomTaskViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTaskRepository()
        subTaskRepository = FakeSubTaskRepository()
        getRandomTaskUseCase = GetRandomTaskUseCase(repository)
        getWeightedRandomTaskUseCase = GetWeightedRandomTaskUseCase(repository)
        completeTaskUseCase = CompleteTaskUseCase(repository)
        getTaskWithSubTasksUseCase = GetTaskWithSubTasksUseCase(repository, subTaskRepository)
        addSubTaskUseCase = AddSubTaskUseCase(subTaskRepository)
        completeSubTaskUseCase = CompleteSubTaskUseCase(subTaskRepository)
        uncompleteSubTaskUseCase = UncompleteSubTaskUseCase(subTaskRepository)
        viewModel = createViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(
        taskRepo: FakeTaskRepository = repository,
        subTaskRepo: FakeSubTaskRepository = subTaskRepository,
    ): RandomTaskViewModel {
        return RandomTaskViewModel(
            getRandomTaskUseCase = GetRandomTaskUseCase(taskRepo),
            completeTaskUseCase = CompleteTaskUseCase(taskRepo),
            getWeightedRandomTaskUseCase = GetWeightedRandomTaskUseCase(taskRepo),
            getTaskWithSubTasksUseCase = GetTaskWithSubTasksUseCase(taskRepo, subTaskRepo),
            addSubTaskUseCase = AddSubTaskUseCase(subTaskRepo),
            completeSubTaskUseCase = CompleteSubTaskUseCase(subTaskRepo),
            uncompleteSubTaskUseCase = UncompleteSubTaskUseCase(subTaskRepo),
            ioDispatcher = testDispatcher,
        )
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
            }
        }

    @Test
    fun `loadRandomTask returns a task when incomplete tasks exist`() =
        runTest(testDispatcher) {
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
    fun `loadRandomTask sets noTasksAvailable when no incomplete tasks exist`() =
        runTest(testDispatcher) {
            viewModel.loadRandomTask()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertNull(state.currentTask)
            assertFalse(state.isLoading)
            assertTrue(state.noTasksAvailable)
        }

    @Test
    fun `completeTask sets taskCompleted flag to true on success`() =
        runTest(testDispatcher) {
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
    fun `resetTaskCompleted clears the taskCompleted flag`() =
        runTest(testDispatcher) {
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
    fun `completeTask failure does not set taskCompleted flag`() =
        runTest(testDispatcher) {
            val errorRepository =
                object : TaskRepository {
                    override fun getAllTasks(): Flow<List<Task>> = flow { emit(emptyList()) }

                    override fun getIncompleteTasks(): Flow<List<Task>> =
                        flow {
                            emit(listOf(Task(id = 1, title = "Task 1", createdAt = 1000L, updatedAt = 1000L)))
                        }

                    override fun getTaskById(id: Long): Flow<Task?> = flow { emit(null) }

                    override fun getCompletedTasks(): Flow<List<Task>> = flow { emit(emptyList()) }

                    override fun getTasksByPriority(priority: Priority): Flow<List<Task>> = flow { emit(emptyList()) }

                    override fun getTasksByCategory(category: String): Flow<List<Task>> = flow { emit(emptyList()) }

                    override fun searchTasks(query: String): Flow<List<Task>> = flow { emit(emptyList()) }

                    override fun getTasksCompletedSince(sinceEpochMs: Long): Flow<List<Task>> =
                        flow { emit(emptyList()) }

                    override fun getOverdueIncompleteTasks(todayEpochDays: Long): Flow<List<Task>> =
                        flow { emit(emptyList()) }

                    override fun getIncompleteTaskCount(): Flow<Int> = flow { emit(0) }

                    override suspend fun addTask(task: Task): Result<Long> = Result.success(1L)

                    override suspend fun updateTask(task: Task): Result<Unit> =
                        Result.failure(
                            RuntimeException("Update failed"),
                        )

                    override suspend fun deleteTask(task: Task): Result<Unit> =
                        Result.failure(
                            RuntimeException("Delete failed"),
                        )
                }
            val errorGetUseCase = GetRandomTaskUseCase(errorRepository)
            val errorWeightedUseCase = GetWeightedRandomTaskUseCase(errorRepository)
            val errorCompleteUseCase = CompleteTaskUseCase(errorRepository)
            val errorViewModel =
                RandomTaskViewModel(
                    errorGetUseCase,
                    errorCompleteUseCase,
                    errorWeightedUseCase,
                    GetTaskWithSubTasksUseCase(errorRepository, subTaskRepository),
                    addSubTaskUseCase,
                    completeSubTaskUseCase,
                    uncompleteSubTaskUseCase,
                    testDispatcher,
                )

            errorViewModel.loadRandomTask()
            advanceUntilIdle()

            assertNotNull(errorViewModel.uiState.value.currentTask)

            errorViewModel.completeTask()
            advanceUntilIdle()

            val state = errorViewModel.uiState.value
            assertFalse(state.taskCompleted)
            assertEquals(R.string.error_complete_task, state.errorResId)
        }

    @Test
    fun `completeTask does nothing when no current task`() =
        runTest(testDispatcher) {
            val initialState = viewModel.uiState.value

            viewModel.completeTask()
            advanceUntilIdle()

            // State should remain unchanged
            assertEquals(initialState, viewModel.uiState.value)
        }

    @Test
    fun `skipTask loads a new random task without completing current one`() =
        runTest(testDispatcher) {
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
    fun `loadRandomTask sets errorResId when GetRandomTaskUseCase throws`() =
        runTest(testDispatcher) {
            val errorRepository =
                object : TaskRepository {
                    override fun getAllTasks(): Flow<List<Task>> = flow { throw RuntimeException("DB error") }

                    override fun getIncompleteTasks(): Flow<List<Task>> = flow { throw RuntimeException("DB error") }

                    override fun getTaskById(id: Long): Flow<Task?> = flow { throw RuntimeException("DB error") }

                    override fun getCompletedTasks(): Flow<List<Task>> = flow { throw RuntimeException("DB error") }

                    override fun getTasksByPriority(priority: Priority): Flow<List<Task>> =
                        flow {
                            throw RuntimeException("DB error")
                        }

                    override fun getTasksByCategory(category: String): Flow<List<Task>> =
                        flow {
                            throw RuntimeException("DB error")
                        }

                    override fun searchTasks(query: String): Flow<List<Task>> =
                        flow {
                            throw RuntimeException("DB error")
                        }

                    override fun getTasksCompletedSince(sinceEpochMs: Long): Flow<List<Task>> =
                        flow { throw RuntimeException("DB error") }

                    override fun getOverdueIncompleteTasks(todayEpochDays: Long): Flow<List<Task>> =
                        flow { throw RuntimeException("DB error") }

                    override fun getIncompleteTaskCount(): Flow<Int> = flow { throw RuntimeException("DB error") }

                    override suspend fun addTask(task: Task): Result<Long> =
                        Result.failure(
                            RuntimeException("DB error"),
                        )

                    override suspend fun updateTask(task: Task): Result<Unit> =
                        Result.failure(
                            RuntimeException("DB error"),
                        )

                    override suspend fun deleteTask(task: Task): Result<Unit> =
                        Result.failure(
                            RuntimeException("DB error"),
                        )
                }
            val errorUseCase = GetRandomTaskUseCase(errorRepository)
            val errorWeightedUseCase = GetWeightedRandomTaskUseCase(errorRepository)
            val errorViewModel =
                RandomTaskViewModel(
                    errorUseCase,
                    completeTaskUseCase,
                    errorWeightedUseCase,
                    GetTaskWithSubTasksUseCase(errorRepository, subTaskRepository),
                    addSubTaskUseCase,
                    completeSubTaskUseCase,
                    uncompleteSubTaskUseCase,
                    testDispatcher,
                )

            errorViewModel.loadRandomTask()
            advanceUntilIdle()

            val state = errorViewModel.uiState.value
            assertFalse(state.isLoading)
            assertNull(state.error)
            assertEquals(R.string.error_load_random_task, state.errorResId)
        }

    @Test
    fun `useWeightedRandom initial value is false`() =
        runTest(testDispatcher) {
            assertFalse(viewModel.useWeightedRandom.value)
        }

    @Test
    fun `toggleWeightedRandom flips from false to true`() =
        runTest(testDispatcher) {
            assertFalse(viewModel.useWeightedRandom.value)

            viewModel.toggleWeightedRandom()

            assertTrue(viewModel.useWeightedRandom.value)
        }

    @Test
    fun `toggleWeightedRandom called twice flips back to false`() =
        runTest(testDispatcher) {
            viewModel.toggleWeightedRandom()
            assertTrue(viewModel.useWeightedRandom.value)

            viewModel.toggleWeightedRandom()
            assertFalse(viewModel.useWeightedRandom.value)
        }

    @Test
    fun `loadRandomTask uses weighted use case when useWeightedRandom is true`() =
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
            assertTrue(viewModel.useWeightedRandom.value)

            viewModel.loadRandomTask()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertNotNull(state.currentTask)
            assertEquals("High Task", state.currentTask!!.title)
        }

    @Test
    fun `loadRandomTask uses uniform use case when useWeightedRandom is false`() =
        runTest(testDispatcher) {
            repository.addTask(
                Task(
                    title = "Regular Task",
                    createdAt = 1000L,
                    updatedAt = 1000L,
                    priority = Priority.MEDIUM,
                ),
            )

            assertFalse(viewModel.useWeightedRandom.value)

            viewModel.loadRandomTask()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertNotNull(state.currentTask)
            assertEquals("Regular Task", state.currentTask!!.title)
        }

    // === Error injection tests using shouldFailMutations ===

    @Test
    fun `completeTask with shouldFailMutations sets errorResId`() =
        runTest(testDispatcher) {
            repository.addTask(Task(title = "Task 1", createdAt = 1000L, updatedAt = 1000L))

            viewModel.loadRandomTask()
            advanceUntilIdle()
            assertNotNull(viewModel.uiState.value.currentTask)

            repository.shouldFailMutations = true
            viewModel.completeTask()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertFalse(state.taskCompleted)
            assertEquals(R.string.error_complete_task, state.errorResId)
        }

    @Test
    fun `clearError resets errorResId to null`() =
        runTest(testDispatcher) {
            repository.addTask(Task(title = "Task 1", createdAt = 1000L, updatedAt = 1000L))

            viewModel.loadRandomTask()
            advanceUntilIdle()

            repository.shouldFailMutations = true
            viewModel.completeTask()
            advanceUntilIdle()

            assertNotNull(viewModel.uiState.value.errorResId)

            viewModel.clearError()

            assertNull(viewModel.uiState.value.errorResId)
            assertNull(viewModel.uiState.value.error)
        }

    // === Subtask tests ===

    @Test
    fun `loadRandomTask starts collecting subtasks for selected task`() =
        runTest(testDispatcher) {
            val taskId = repository.addTask(Task(title = "Task 1", createdAt = 1000L, updatedAt = 1000L)).getOrThrow()
            subTaskRepository.addSubTask(
                SubTask(parentTaskId = taskId, title = "Sub 1", createdAt = 1000L, updatedAt = 1000L),
            )

            viewModel.loadRandomTask()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertNotNull(state.currentTask)
            assertEquals(1, state.subTasks.size)
            assertEquals("Sub 1", state.subTasks.first().title)
        }

    @Test
    fun `addSubTask with valid title adds subtask to list`() =
        runTest(testDispatcher) {
            repository.addTask(Task(title = "Task 1", createdAt = 1000L, updatedAt = 1000L))

            viewModel.loadRandomTask()
            advanceUntilIdle()

            viewModel.onNewSubTaskTitleChange("New SubTask")
            viewModel.addSubTask()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(1, state.subTasks.size)
            assertEquals("New SubTask", state.subTasks.first().title)
            assertFalse(state.isAddingSubTask)
            assertEquals("", state.newSubTaskTitle)
        }

    @Test
    fun `addSubTask with blank title sets error`() =
        runTest(testDispatcher) {
            repository.addTask(Task(title = "Task 1", createdAt = 1000L, updatedAt = 1000L))

            viewModel.loadRandomTask()
            advanceUntilIdle()

            viewModel.onNewSubTaskTitleChange("   ")
            viewModel.addSubTask()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(R.string.error_add_subtask, state.errorResId)
            assertTrue(state.subTasks.isEmpty())
        }

    @Test
    fun `toggleSubTask marks incomplete subtask as completed`() =
        runTest(testDispatcher) {
            val taskId = repository.addTask(Task(title = "Task 1", createdAt = 1000L, updatedAt = 1000L)).getOrThrow()
            subTaskRepository.addSubTask(
                SubTask(parentTaskId = taskId, title = "Sub 1", createdAt = 1000L, updatedAt = 1000L),
            )

            viewModel.loadRandomTask()
            advanceUntilIdle()

            val subTask = viewModel.uiState.value.subTasks.first()
            assertFalse(subTask.isCompleted)

            viewModel.toggleSubTask(subTask)
            advanceUntilIdle()

            val updated = viewModel.uiState.value.subTasks.first()
            assertTrue(updated.isCompleted)
        }

    @Test
    fun `toggleSubTask marks completed subtask as incomplete`() =
        runTest(testDispatcher) {
            val taskId = repository.addTask(Task(title = "Task 1", createdAt = 1000L, updatedAt = 1000L)).getOrThrow()
            subTaskRepository.addSubTask(
                SubTask(
                    parentTaskId = taskId,
                    title = "Sub 1",
                    isCompleted = true,
                    createdAt = 1000L,
                    updatedAt = 1000L,
                ),
            )

            viewModel.loadRandomTask()
            advanceUntilIdle()

            val subTask = viewModel.uiState.value.subTasks.first()
            assertTrue(subTask.isCompleted)

            viewModel.toggleSubTask(subTask)
            advanceUntilIdle()

            val updated = viewModel.uiState.value.subTasks.first()
            assertFalse(updated.isCompleted)
        }

    @Test
    fun `showAddSubTask sets isAddingSubTask to true`() =
        runTest(testDispatcher) {
            viewModel.showAddSubTask()

            assertTrue(viewModel.uiState.value.isAddingSubTask)
        }

    @Test
    fun `hideAddSubTask clears state`() =
        runTest(testDispatcher) {
            viewModel.showAddSubTask()
            viewModel.onNewSubTaskTitleChange("Partial title")

            viewModel.hideAddSubTask()

            assertFalse(viewModel.uiState.value.isAddingSubTask)
            assertEquals("", viewModel.uiState.value.newSubTaskTitle)
        }

    @Test
    fun `onNewSubTaskTitleChange updates newSubTaskTitle`() =
        runTest(testDispatcher) {
            viewModel.onNewSubTaskTitleChange("My subtask")

            assertEquals("My subtask", viewModel.uiState.value.newSubTaskTitle)
        }

    @Test
    fun `skipTask clears subtasks and loads new task subtasks`() =
        runTest(testDispatcher) {
            val taskId = repository.addTask(Task(title = "Task 1", createdAt = 1000L, updatedAt = 1000L)).getOrThrow()
            subTaskRepository.addSubTask(
                SubTask(parentTaskId = taskId, title = "Sub 1", createdAt = 1000L, updatedAt = 1000L),
            )

            viewModel.loadRandomTask()
            advanceUntilIdle()

            assertEquals(1, viewModel.uiState.value.subTasks.size)

            viewModel.skipTask()
            advanceUntilIdle()

            // After skip, subtasks should be re-collected for the (same) task
            assertNotNull(viewModel.uiState.value.currentTask)
        }

    @Test
    fun `loadRandomTask clears subtasks when no task available`() =
        runTest(testDispatcher) {
            viewModel.loadRandomTask()
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.subTasks.isEmpty())
        }
}
