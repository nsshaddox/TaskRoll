package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.Priority
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AddTaskUseCaseTest {
    private lateinit var repository: FakeTaskRepository
    private lateinit var addTaskUseCase: AddTaskUseCase

    @Before
    fun setup() {
        repository = FakeTaskRepository()
        addTaskUseCase = AddTaskUseCase(repository)
    }

    @Test
    fun `invoke with title only stores task with MEDIUM priority null dueDate null category`() =
        runTest {
            addTaskUseCase(title = "Simple Task")

            val stored = repository.getAllTasksSnapshot().first()
            assertEquals("Simple Task", stored.title)
            assertNull(stored.description)
            assertEquals(Priority.MEDIUM, stored.priority)
            assertNull(stored.dueDate)
            assertNull(stored.category)
        }

    @Test
    fun `invoke with all explicit params stores correct field values`() =
        runTest {
            addTaskUseCase(
                title = "Full Task",
                description = "A detailed description",
                priority = Priority.LOW,
                dueDate = 19900L,
                category = "Work",
            )

            val stored = repository.getAllTasksSnapshot().first()
            assertEquals("Full Task", stored.title)
            assertEquals("A detailed description", stored.description)
            assertEquals(Priority.LOW, stored.priority)
            assertEquals(19900L, stored.dueDate)
            assertEquals("Work", stored.category)
        }

    @Test
    fun `invoke sets createdAt and updatedAt to current time`() =
        runTest {
            val before = System.currentTimeMillis()

            addTaskUseCase(title = "Timed Task")

            val stored = repository.getAllTasksSnapshot().first()
            assertTrue(stored.createdAt >= before)
            assertTrue(stored.updatedAt >= before)
            assertEquals(stored.createdAt, stored.updatedAt)
        }

    @Test
    fun `invoke with blank title returns failure`() =
        runTest {
            val result = addTaskUseCase(title = "   ")

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        }

    @Test
    fun `invoke with empty title returns failure`() =
        runTest {
            val result = addTaskUseCase(title = "")

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        }

    @Test
    fun `invoke with valid title and description returns success with generated id`() =
        runTest {
            val result = addTaskUseCase(title = "Valid Task", description = "Some description")

            assertTrue(result.isSuccess)
            assertNotNull(result.getOrNull())
            assertEquals(1L, result.getOrNull())
        }
}
