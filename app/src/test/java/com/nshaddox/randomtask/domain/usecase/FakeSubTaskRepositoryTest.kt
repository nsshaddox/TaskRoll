package com.nshaddox.randomtask.domain.usecase

import app.cash.turbine.test
import com.nshaddox.randomtask.domain.model.SubTask
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeSubTaskRepositoryTest {
    private lateinit var repository: FakeSubTaskRepository

    private fun createSubTask(
        parentTaskId: Long = 1L,
        title: String = "Test SubTask",
        isCompleted: Boolean = false,
    ) = SubTask(
        parentTaskId = parentTaskId,
        title = title,
        isCompleted = isCompleted,
        createdAt = 1000L,
        updatedAt = 2000L,
    )

    @Before
    fun setup() {
        repository = FakeSubTaskRepository()
    }

    @Test
    fun `addSubTask returns success with generated id`() =
        runTest {
            val result = repository.addSubTask(createSubTask())

            assertTrue(result.isSuccess)
            assertEquals(1L, result.getOrNull())
        }

    @Test
    fun `addSubTask when shouldFailMutations returns failure`() =
        runTest {
            repository.shouldFailMutations = true

            val result = repository.addSubTask(createSubTask())

            assertTrue(result.isFailure)
        }

    @Test
    fun `getSubTasksForTask returns subtasks for correct parent`() =
        runTest {
            repository.addSubTask(createSubTask(parentTaskId = 1L, title = "Sub 1"))
            repository.addSubTask(createSubTask(parentTaskId = 2L, title = "Sub 2"))
            repository.addSubTask(createSubTask(parentTaskId = 1L, title = "Sub 3"))

            repository.getSubTasksForTask(1L).test {
                val items = awaitItem()
                assertEquals(2, items.size)
                assertTrue(items.all { it.parentTaskId == 1L })
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `getSubTasksForTask when shouldFailQueries returns empty`() =
        runTest {
            repository.addSubTask(createSubTask(parentTaskId = 1L))
            repository.shouldFailQueries = true

            repository.getSubTasksForTask(1L).test {
                val items = awaitItem()
                assertTrue(items.isEmpty())
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `updateSubTask updates existing subtask`() =
        runTest {
            repository.addSubTask(createSubTask(title = "Original"))
            val stored = repository.getAllSubTasksSnapshot().first()

            val updated = stored.copy(title = "Updated")
            val result = repository.updateSubTask(updated)

            assertTrue(result.isSuccess)
            assertEquals("Updated", repository.getAllSubTasksSnapshot().first().title)
        }

    @Test
    fun `updateSubTask when shouldFailMutations returns failure`() =
        runTest {
            repository.addSubTask(createSubTask())
            repository.shouldFailMutations = true

            val result = repository.updateSubTask(repository.getAllSubTasksSnapshot().first())

            assertTrue(result.isFailure)
        }

    @Test
    fun `deleteSubTask removes subtask`() =
        runTest {
            repository.addSubTask(createSubTask())
            assertEquals(1, repository.getAllSubTasksSnapshot().size)

            val result = repository.deleteSubTask(1L)

            assertTrue(result.isSuccess)
            assertTrue(repository.getAllSubTasksSnapshot().isEmpty())
        }

    @Test
    fun `deleteSubTask when shouldFailMutations returns failure`() =
        runTest {
            repository.addSubTask(createSubTask())
            repository.shouldFailMutations = true

            val result = repository.deleteSubTask(1L)

            assertTrue(result.isFailure)
        }

    @Test
    fun `getSubTaskCount returns correct count for parent`() =
        runTest {
            repository.addSubTask(createSubTask(parentTaskId = 1L))
            repository.addSubTask(createSubTask(parentTaskId = 1L))
            repository.addSubTask(createSubTask(parentTaskId = 2L))

            repository.getSubTaskCount(1L).test {
                assertEquals(2, awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `getCompletedSubTaskCount returns correct count`() =
        runTest {
            repository.addSubTask(createSubTask(parentTaskId = 1L, isCompleted = true))
            repository.addSubTask(createSubTask(parentTaskId = 1L, isCompleted = false))
            repository.addSubTask(createSubTask(parentTaskId = 1L, isCompleted = true))

            repository.getCompletedSubTaskCount(1L).test {
                assertEquals(2, awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }
}
