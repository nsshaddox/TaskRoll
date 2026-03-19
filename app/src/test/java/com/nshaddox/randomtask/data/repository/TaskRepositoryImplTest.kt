package com.nshaddox.randomtask.data.repository

import app.cash.turbine.test
import com.nshaddox.randomtask.data.local.TaskDao
import com.nshaddox.randomtask.data.local.TaskEntity
import com.nshaddox.randomtask.domain.model.Priority
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

    // --- getCompletedTasks ---

    @Test
    fun `getCompletedTasks returns completed tasks from dao`() =
        runTest {
            val entities =
                listOf(
                    TaskEntity(
                        id = 1L,
                        title = "Completed Task 1",
                        description = null,
                        isCompleted = true,
                        createdAt = 1000L,
                        updatedAt = 2000L,
                    ),
                    TaskEntity(
                        id = 2L,
                        title = "Completed Task 2",
                        description = "Done",
                        isCompleted = true,
                        createdAt = 3000L,
                        updatedAt = 4000L,
                    ),
                )
            every { taskDao.getCompletedTasks() } returns flowOf(entities)

            repository.getCompletedTasks().test {
                val tasks = awaitItem()
                assertEquals(2, tasks.size)
                assertEquals("Completed Task 1", tasks[0].title)
                assertEquals("Completed Task 2", tasks[1].title)
                assertTrue(tasks.all { it.isCompleted })
                awaitComplete()
            }
        }

    @Test
    fun `getCompletedTasks returns empty list when no completed tasks`() =
        runTest {
            every { taskDao.getCompletedTasks() } returns flowOf(emptyList())

            repository.getCompletedTasks().test {
                val tasks = awaitItem()
                assertTrue(tasks.isEmpty())
                awaitComplete()
            }
        }

    // --- getTasksByPriority ---

    @Test
    fun `getTasksByPriority returns matching tasks and passes priority name to dao`() =
        runTest {
            val entities =
                listOf(
                    TaskEntity(
                        id = 1L,
                        title = "High Priority Task",
                        description = null,
                        isCompleted = false,
                        createdAt = 1000L,
                        updatedAt = 2000L,
                        priority = "HIGH",
                    ),
                )
            every { taskDao.getTasksByPriority("HIGH") } returns flowOf(entities)

            repository.getTasksByPriority(Priority.HIGH).test {
                val tasks = awaitItem()
                assertEquals(1, tasks.size)
                assertEquals("High Priority Task", tasks[0].title)
                assertEquals(Priority.HIGH, tasks[0].priority)
                awaitComplete()
            }

            io.mockk.verify { taskDao.getTasksByPriority("HIGH") }
        }

    @Test
    fun `getTasksByPriority returns empty list when no matching tasks`() =
        runTest {
            every { taskDao.getTasksByPriority("LOW") } returns flowOf(emptyList())

            repository.getTasksByPriority(Priority.LOW).test {
                val tasks = awaitItem()
                assertTrue(tasks.isEmpty())
                awaitComplete()
            }
        }

    // --- getTasksByCategory ---

    @Test
    fun `getTasksByCategory returns matching tasks from dao`() =
        runTest {
            val entities =
                listOf(
                    TaskEntity(
                        id = 1L,
                        title = "Work Task",
                        description = null,
                        isCompleted = false,
                        createdAt = 1000L,
                        updatedAt = 2000L,
                        category = "Work",
                    ),
                )
            every { taskDao.getTasksByCategory("Work") } returns flowOf(entities)

            repository.getTasksByCategory("Work").test {
                val tasks = awaitItem()
                assertEquals(1, tasks.size)
                assertEquals("Work Task", tasks[0].title)
                assertEquals("Work", tasks[0].category)
                awaitComplete()
            }
        }

    @Test
    fun `getTasksByCategory returns empty list when no matching tasks`() =
        runTest {
            every { taskDao.getTasksByCategory("NonExistent") } returns flowOf(emptyList())

            repository.getTasksByCategory("NonExistent").test {
                val tasks = awaitItem()
                assertTrue(tasks.isEmpty())
                awaitComplete()
            }
        }

    // --- searchTasks ---

    @Test
    fun `searchTasks returns matching tasks from dao`() =
        runTest {
            val entities =
                listOf(
                    TaskEntity(
                        id = 1L,
                        title = "Buy groceries",
                        description = "Milk and eggs",
                        isCompleted = false,
                        createdAt = 1000L,
                        updatedAt = 2000L,
                    ),
                )
            every { taskDao.searchTasks("groceries") } returns flowOf(entities)

            repository.searchTasks("groceries").test {
                val tasks = awaitItem()
                assertEquals(1, tasks.size)
                assertEquals("Buy groceries", tasks[0].title)
                awaitComplete()
            }
        }

    @Test
    fun `searchTasks returns empty list when no matching tasks`() =
        runTest {
            every { taskDao.searchTasks("nonexistent") } returns flowOf(emptyList())

            repository.searchTasks("nonexistent").test {
                val tasks = awaitItem()
                assertTrue(tasks.isEmpty())
                awaitComplete()
            }
        }

    // --- getTasksCompletedSince ---

    @Test
    fun `getTasksCompletedSince returns mapped completed tasks from dao`() =
        runTest {
            val entities =
                listOf(
                    TaskEntity(
                        id = 1L,
                        title = "Done Today",
                        description = null,
                        isCompleted = true,
                        createdAt = 1000L,
                        updatedAt = 5000L,
                    ),
                )
            every { taskDao.getCompletedTasksSince(4000L) } returns flowOf(entities)

            repository.getTasksCompletedSince(4000L).test {
                val tasks = awaitItem()
                assertEquals(1, tasks.size)
                assertEquals("Done Today", tasks[0].title)
                assertTrue(tasks[0].isCompleted)
                awaitComplete()
            }
        }

    @Test
    fun `getTasksCompletedSince returns empty list when none completed since cutoff`() =
        runTest {
            every { taskDao.getCompletedTasksSince(9000L) } returns flowOf(emptyList())

            repository.getTasksCompletedSince(9000L).test {
                val tasks = awaitItem()
                assertTrue(tasks.isEmpty())
                awaitComplete()
            }
        }

    // --- getOverdueIncompleteTasks ---

    @Test
    fun `getOverdueIncompleteTasks returns mapped overdue tasks from dao`() =
        runTest {
            val entities =
                listOf(
                    TaskEntity(
                        id = 2L,
                        title = "Overdue Task",
                        description = null,
                        isCompleted = false,
                        createdAt = 1000L,
                        updatedAt = 2000L,
                        dueDate = 19000L,
                    ),
                )
            every { taskDao.getOverdueIncompleteTasks(20000L) } returns flowOf(entities)

            repository.getOverdueIncompleteTasks(20000L).test {
                val tasks = awaitItem()
                assertEquals(1, tasks.size)
                assertEquals("Overdue Task", tasks[0].title)
                awaitComplete()
            }
        }

    @Test
    fun `getOverdueIncompleteTasks returns empty list when no overdue tasks`() =
        runTest {
            every { taskDao.getOverdueIncompleteTasks(20000L) } returns flowOf(emptyList())

            repository.getOverdueIncompleteTasks(20000L).test {
                val tasks = awaitItem()
                assertTrue(tasks.isEmpty())
                awaitComplete()
            }
        }

    // --- getIncompleteTaskCount ---

    @Test
    fun `getIncompleteTaskCount returns count from dao`() =
        runTest {
            every { taskDao.getIncompleteTaskCount() } returns flowOf(5)

            repository.getIncompleteTaskCount().test {
                val count = awaitItem()
                assertEquals(5, count)
                awaitComplete()
            }
        }

    @Test
    fun `getIncompleteTaskCount returns zero when no incomplete tasks`() =
        runTest {
            every { taskDao.getIncompleteTaskCount() } returns flowOf(0)

            repository.getIncompleteTaskCount().test {
                val count = awaitItem()
                assertEquals(0, count)
                awaitComplete()
            }
        }
}
