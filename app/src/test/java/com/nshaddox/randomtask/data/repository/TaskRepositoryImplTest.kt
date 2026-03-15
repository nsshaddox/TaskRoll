package com.nshaddox.randomtask.data.repository

import app.cash.turbine.test
import com.nshaddox.randomtask.data.local.TaskDao
import com.nshaddox.randomtask.data.local.TaskEntity
import com.nshaddox.randomtask.domain.model.Task
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TaskRepositoryImplTest {
    private lateinit var taskDao: TaskDao
    private lateinit var repository: TaskRepositoryImpl

    @Before
    fun setup() {
        taskDao = mockk()
        repository = TaskRepositoryImpl(taskDao)
    }

    // --- addTask ---

    @Test
    fun `addTask success returns Result with generated id`() =
        runTest {
            val task =
                Task(
                    title = "Test Task",
                    description = "Description",
                    isCompleted = false,
                    createdAt = 1000L,
                    updatedAt = 2000L,
                )
            coEvery { taskDao.insertTask(any()) } returns 42L

            val result = repository.addTask(task)

            assertTrue(result.isSuccess)
            assertEquals(42L, result.getOrNull())
            coVerify { taskDao.insertTask(task.toEntity()) }
        }

    @Test
    fun `addTask exception returns Result failure`() =
        runTest {
            val task =
                Task(
                    title = "Test Task",
                    createdAt = 1000L,
                    updatedAt = 2000L,
                )
            val exception = RuntimeException("DB error")
            coEvery { taskDao.insertTask(any()) } throws exception

            val result = repository.addTask(task)

            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
        }

    // --- updateTask ---

    @Test
    fun `updateTask success returns Result success`() =
        runTest {
            val task =
                Task(
                    id = 1L,
                    title = "Updated Task",
                    description = "Updated",
                    isCompleted = false,
                    createdAt = 1000L,
                    updatedAt = 3000L,
                )
            coEvery { taskDao.updateTask(any()) } returns Unit

            val result = repository.updateTask(task)

            assertTrue(result.isSuccess)
            coVerify { taskDao.updateTask(task.toEntity()) }
        }

    @Test
    fun `updateTask exception returns Result failure`() =
        runTest {
            val task =
                Task(
                    id = 1L,
                    title = "Updated Task",
                    createdAt = 1000L,
                    updatedAt = 3000L,
                )
            val exception = RuntimeException("Update failed")
            coEvery { taskDao.updateTask(any()) } throws exception

            val result = repository.updateTask(task)

            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
        }

    // --- deleteTask ---

    @Test
    fun `deleteTask success returns Result success`() =
        runTest {
            val task =
                Task(
                    id = 5L,
                    title = "To Delete",
                    createdAt = 1000L,
                    updatedAt = 2000L,
                )
            coEvery { taskDao.deleteTaskById(5L) } returns Unit

            val result = repository.deleteTask(task)

            assertTrue(result.isSuccess)
            coVerify { taskDao.deleteTaskById(5L) }
        }

    @Test
    fun `deleteTask exception returns Result failure`() =
        runTest {
            val task =
                Task(
                    id = 5L,
                    title = "To Delete",
                    createdAt = 1000L,
                    updatedAt = 2000L,
                )
            val exception = RuntimeException("Delete failed")
            coEvery { taskDao.deleteTaskById(5L) } throws exception

            val result = repository.deleteTask(task)

            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
        }

    // --- getAllTasks ---

    @Test
    fun `getAllTasks returns mapped domain tasks from dao`() =
        runTest {
            val entities =
                listOf(
                    TaskEntity(
                        id = 1L,
                        title = "Task 1",
                        description = null,
                        isCompleted = false,
                        createdAt = 1000L,
                        updatedAt = 2000L,
                    ),
                    TaskEntity(
                        id = 2L,
                        title = "Task 2",
                        description = "Desc",
                        isCompleted = true,
                        createdAt = 3000L,
                        updatedAt = 4000L,
                    ),
                )
            every { taskDao.getAllTasks() } returns flowOf(entities)

            repository.getAllTasks().test {
                val tasks = awaitItem()
                assertEquals(2, tasks.size)
                assertEquals("Task 1", tasks[0].title)
                assertEquals("Task 2", tasks[1].title)
                assertEquals(true, tasks[1].isCompleted)
                awaitComplete()
            }
        }

    @Test
    fun `getAllTasks returns empty list when dao has no tasks`() =
        runTest {
            every { taskDao.getAllTasks() } returns flowOf(emptyList())

            repository.getAllTasks().test {
                val tasks = awaitItem()
                assertTrue(tasks.isEmpty())
                awaitComplete()
            }
        }

    // --- getIncompleteTasks ---

    @Test
    fun `getIncompleteTasks returns only incomplete mapped tasks`() =
        runTest {
            val entities =
                listOf(
                    TaskEntity(
                        id = 1L,
                        title = "Incomplete",
                        description = null,
                        isCompleted = false,
                        createdAt = 1000L,
                        updatedAt = 2000L,
                    ),
                )
            every { taskDao.getIncompleteTasks() } returns flowOf(entities)

            repository.getIncompleteTasks().test {
                val tasks = awaitItem()
                assertEquals(1, tasks.size)
                assertEquals("Incomplete", tasks[0].title)
                assertEquals(false, tasks[0].isCompleted)
                awaitComplete()
            }
        }

    @Test
    fun `getIncompleteTasks returns empty list when all completed`() =
        runTest {
            every { taskDao.getIncompleteTasks() } returns flowOf(emptyList())

            repository.getIncompleteTasks().test {
                val tasks = awaitItem()
                assertTrue(tasks.isEmpty())
                awaitComplete()
            }
        }

    // --- getTaskById ---

    @Test
    fun `getTaskById returns mapped domain task when found`() =
        runTest {
            val entity =
                TaskEntity(
                    id = 7L,
                    title = "Found Task",
                    description = "Details",
                    isCompleted = false,
                    createdAt = 5000L,
                    updatedAt = 6000L,
                )
            every { taskDao.getTaskByIdFlow(7L) } returns flowOf(entity)

            repository.getTaskById(7L).test {
                val task = awaitItem()
                assertEquals(7L, task?.id)
                assertEquals("Found Task", task?.title)
                assertEquals("Details", task?.description)
                awaitComplete()
            }
        }

    @Test
    fun `getTaskById returns null when task not found`() =
        runTest {
            every { taskDao.getTaskByIdFlow(999L) } returns flowOf(null)

            repository.getTaskById(999L).test {
                val task = awaitItem()
                assertNull(task)
                awaitComplete()
            }
        }
}
