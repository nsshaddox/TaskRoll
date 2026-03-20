package com.nshaddox.randomtask.data.repository

import app.cash.turbine.test
import com.nshaddox.randomtask.data.local.SubTaskDao
import com.nshaddox.randomtask.data.local.SubTaskEntity
import com.nshaddox.randomtask.domain.model.SubTask
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SubTaskRepositoryImplTest {
    private lateinit var subTaskDao: SubTaskDao
    private lateinit var repository: SubTaskRepositoryImpl

    @Before
    fun setup() {
        subTaskDao = mockk()
        repository = SubTaskRepositoryImpl(subTaskDao)
    }

    // --- getSubTasksForTask ---

    @Test
    fun `getSubTasksForTask returns mapped domain subtasks`() =
        runTest {
            val entities =
                listOf(
                    SubTaskEntity(
                        id = 1L,
                        parentTaskId = 10L,
                        title = "Sub 1",
                        isCompleted = false,
                        createdAt = 1000L,
                        updatedAt = 2000L,
                    ),
                    SubTaskEntity(
                        id = 2L,
                        parentTaskId = 10L,
                        title = "Sub 2",
                        isCompleted = true,
                        createdAt = 3000L,
                        updatedAt = 4000L,
                    ),
                )
            every { subTaskDao.getSubTasksForTask(10L) } returns flowOf(entities)

            repository.getSubTasksForTask(10L).test {
                val subtasks = awaitItem()
                assertEquals(2, subtasks.size)
                assertEquals("Sub 1", subtasks[0].title)
                assertEquals(10L, subtasks[0].parentTaskId)
                assertEquals("Sub 2", subtasks[1].title)
                assertTrue(subtasks[1].isCompleted)
                awaitComplete()
            }
        }

    @Test
    fun `getSubTasksForTask returns empty list when no subtasks`() =
        runTest {
            every { subTaskDao.getSubTasksForTask(99L) } returns flowOf(emptyList())

            repository.getSubTasksForTask(99L).test {
                val subtasks = awaitItem()
                assertTrue(subtasks.isEmpty())
                awaitComplete()
            }
        }

    @Test
    fun `getSubTasksForTask emits empty list when dao throws`() =
        runTest {
            every { subTaskDao.getSubTasksForTask(1L) } returns
                flow { throw IllegalStateException("DB error") }

            repository.getSubTasksForTask(1L).test {
                val subtasks = awaitItem()
                assertTrue(subtasks.isEmpty())
                awaitComplete()
            }
        }

    // --- getSubTaskCount ---

    @Test
    fun `getSubTaskCount returns count from dao`() =
        runTest {
            every { subTaskDao.getSubTaskCount(10L) } returns flowOf(3)

            repository.getSubTaskCount(10L).test {
                assertEquals(3, awaitItem())
                awaitComplete()
            }
        }

    // --- getCompletedSubTaskCount ---

    @Test
    fun `getCompletedSubTaskCount returns count from dao`() =
        runTest {
            every { subTaskDao.getCompletedSubTaskCount(10L) } returns flowOf(2)

            repository.getCompletedSubTaskCount(10L).test {
                assertEquals(2, awaitItem())
                awaitComplete()
            }
        }

    // --- addSubTask ---

    @Test
    fun `addSubTask success returns Result with generated id`() =
        runTest {
            val subTask =
                SubTask(
                    parentTaskId = 10L,
                    title = "New Sub",
                    createdAt = 1000L,
                    updatedAt = 1000L,
                )
            coEvery { subTaskDao.insertSubTask(any()) } returns 42L

            val result = repository.addSubTask(subTask)

            assertTrue(result.isSuccess)
            assertEquals(42L, result.getOrNull())
            coVerify { subTaskDao.insertSubTask(subTask.toEntity()) }
        }

    @Test
    fun `addSubTask exception returns Result failure`() =
        runTest {
            val subTask =
                SubTask(
                    parentTaskId = 10L,
                    title = "New Sub",
                    createdAt = 1000L,
                    updatedAt = 1000L,
                )
            val exception = RuntimeException("DB error")
            coEvery { subTaskDao.insertSubTask(any()) } throws exception

            val result = repository.addSubTask(subTask)

            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
        }

    // --- updateSubTask ---

    @Test
    fun `updateSubTask success returns Result success`() =
        runTest {
            val subTask =
                SubTask(
                    id = 1L,
                    parentTaskId = 10L,
                    title = "Updated",
                    isCompleted = true,
                    createdAt = 1000L,
                    updatedAt = 3000L,
                )
            coEvery { subTaskDao.updateSubTask(any()) } returns Unit

            val result = repository.updateSubTask(subTask)

            assertTrue(result.isSuccess)
            coVerify { subTaskDao.updateSubTask(subTask.toEntity()) }
        }

    @Test
    fun `updateSubTask exception returns Result failure`() =
        runTest {
            val subTask =
                SubTask(
                    id = 1L,
                    parentTaskId = 10L,
                    title = "Updated",
                    createdAt = 1000L,
                    updatedAt = 3000L,
                )
            val exception = RuntimeException("Update failed")
            coEvery { subTaskDao.updateSubTask(any()) } throws exception

            val result = repository.updateSubTask(subTask)

            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
        }

    // --- deleteSubTask ---

    @Test
    fun `deleteSubTask success returns Result success`() =
        runTest {
            coEvery { subTaskDao.deleteSubTaskById(5L) } returns Unit

            val result = repository.deleteSubTask(5L)

            assertTrue(result.isSuccess)
            coVerify { subTaskDao.deleteSubTaskById(5L) }
        }

    @Test
    fun `deleteSubTask exception returns Result failure`() =
        runTest {
            val exception = RuntimeException("Delete failed")
            coEvery { subTaskDao.deleteSubTaskById(5L) } throws exception

            val result = repository.deleteSubTask(5L)

            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
        }
}
