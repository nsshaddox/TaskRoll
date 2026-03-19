package com.nshaddox.randomtask.domain.usecase

import app.cash.turbine.test
import com.nshaddox.randomtask.domain.model.Task
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeTaskRepositoryTest {
    private lateinit var repository: FakeTaskRepository

    @Before
    fun setup() {
        repository = FakeTaskRepository()
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

    // --- shouldFailMutations tests ---

    @Test
    fun `addTask returns failure when shouldFailMutations is true`() =
        runTest {
            repository.shouldFailMutations = true

            val result = repository.addTask(createTask(title = "New Task"))

            assertTrue(result.isFailure)
            assertEquals("Simulated failure", result.exceptionOrNull()?.message)
        }

    @Test
    fun `updateTask returns failure when shouldFailMutations is true`() =
        runTest {
            repository.addTask(createTask(title = "Existing"))
            repository.shouldFailMutations = true

            val task = repository.getAllTasksSnapshot().first()
            val result = repository.updateTask(task.copy(title = "Updated"))

            assertTrue(result.isFailure)
            assertEquals("Simulated failure", result.exceptionOrNull()?.message)
        }

    @Test
    fun `deleteTask returns failure when shouldFailMutations is true`() =
        runTest {
            repository.addTask(createTask(title = "To Delete"))
            repository.shouldFailMutations = true

            val task = repository.getAllTasksSnapshot().first()
            val result = repository.deleteTask(task)

            assertTrue(result.isFailure)
            assertEquals("Simulated failure", result.exceptionOrNull()?.message)
        }

    @Test
    fun `addTask succeeds when shouldFailMutations is false`() =
        runTest {
            repository.shouldFailMutations = false

            val result = repository.addTask(createTask(title = "New Task"))

            assertTrue(result.isSuccess)
        }

    // --- shouldFailQueries tests ---

    @Test
    fun `getAllTasks emits empty list when shouldFailQueries is true`() =
        runTest {
            repository.addTask(createTask(title = "Existing"))
            repository.shouldFailQueries = true

            repository.getAllTasks().test {
                val items = awaitItem()
                assertTrue(items.isEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `getIncompleteTasks emits empty list when shouldFailQueries is true`() =
        runTest {
            repository.addTask(createTask(title = "Existing"))
            repository.shouldFailQueries = true

            repository.getIncompleteTasks().test {
                val items = awaitItem()
                assertTrue(items.isEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `getCompletedTasks emits empty list when shouldFailQueries is true`() =
        runTest {
            repository.shouldFailQueries = true

            repository.getCompletedTasks().test {
                val items = awaitItem()
                assertTrue(items.isEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `getTaskById emits null when shouldFailQueries is true`() =
        runTest {
            repository.addTask(createTask(title = "Existing"))
            repository.shouldFailQueries = true

            repository.getTaskById(1L).test {
                val item = awaitItem()
                assertTrue(item == null)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `getTasksByPriority emits empty list when shouldFailQueries is true`() =
        runTest {
            repository.shouldFailQueries = true

            repository.getTasksByPriority(com.nshaddox.randomtask.domain.model.Priority.HIGH).test {
                val items = awaitItem()
                assertTrue(items.isEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `getTasksByCategory emits empty list when shouldFailQueries is true`() =
        runTest {
            repository.shouldFailQueries = true

            repository.getTasksByCategory("Work").test {
                val items = awaitItem()
                assertTrue(items.isEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `searchTasks emits empty list when shouldFailQueries is true`() =
        runTest {
            repository.shouldFailQueries = true

            repository.searchTasks("query").test {
                val items = awaitItem()
                assertTrue(items.isEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }
}
