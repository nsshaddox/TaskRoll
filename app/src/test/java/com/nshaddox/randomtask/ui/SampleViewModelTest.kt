package com.nshaddox.randomtask.ui

import app.cash.turbine.test
import com.nshaddox.randomtask.domain.model.Task
import com.nshaddox.randomtask.domain.usecase.FakeTaskRepository
import com.nshaddox.randomtask.domain.usecase.GetTasksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SampleViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeTaskRepository
    private lateinit var getTasksUseCase: GetTasksUseCase
    private lateinit var viewModel: SampleViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTaskRepository()
        getTasksUseCase = GetTasksUseCase(repository)
        viewModel = SampleViewModel(getTasksUseCase, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `tasks emits empty list initially`() =
        runTest(testDispatcher) {
            viewModel.tasks.test {
                val initial = awaitItem()
                assertTrue(initial.isEmpty())
            }
        }

    @Test
    fun `tasks emits updated list when repository changes`() =
        runTest(testDispatcher) {
            viewModel.tasks.test {
                assertTrue(awaitItem().isEmpty())

                repository.addTask(Task(title = "Task 1", createdAt = 1000L, updatedAt = 1000L))

                val updated = awaitItem()
                assertEquals(1, updated.size)
                assertEquals("Task 1", updated[0].title)
            }
        }

    @Test
    fun `tasks emits multiple tasks`() =
        runTest(testDispatcher) {
            viewModel.tasks.test {
                assertTrue(awaitItem().isEmpty())

                repository.addTask(Task(title = "Task 1", createdAt = 1000L, updatedAt = 1000L))
                awaitItem()

                repository.addTask(Task(title = "Task 2", createdAt = 2000L, updatedAt = 2000L))

                val tasks = awaitItem()
                assertEquals(2, tasks.size)
                assertEquals("Task 1", tasks[0].title)
                assertEquals("Task 2", tasks[1].title)
            }
        }
}
