package com.nshaddox.randomtask.domain.usecase

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AddSubTaskUseCaseTest {
    private lateinit var repository: FakeSubTaskRepository
    private lateinit var addSubTaskUseCase: AddSubTaskUseCase

    @Before
    fun setup() {
        repository = FakeSubTaskRepository()
        addSubTaskUseCase = AddSubTaskUseCase(repository)
    }

    @Test
    fun `invoke with valid title stores subtask with correct parentTaskId`() =
        runTest {
            addSubTaskUseCase(parentTaskId = 5L, title = "Downstairs")

            val stored = repository.getAllSubTasksSnapshot().first()
            assertEquals(5L, stored.parentTaskId)
            assertEquals("Downstairs", stored.title)
        }

    @Test
    fun `invoke with blank title returns failure with IllegalArgumentException`() =
        runTest {
            val result = addSubTaskUseCase(parentTaskId = 1L, title = "   ")

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        }

    @Test
    fun `invoke with empty title returns failure`() =
        runTest {
            val result = addSubTaskUseCase(parentTaskId = 1L, title = "")

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        }

    @Test
    fun `invoke sets createdAt and updatedAt to current time`() =
        runTest {
            val before = System.currentTimeMillis()

            addSubTaskUseCase(parentTaskId = 1L, title = "Timed SubTask")

            val stored = repository.getAllSubTasksSnapshot().first()
            assertTrue(stored.createdAt >= before)
            assertTrue(stored.updatedAt >= before)
            assertEquals(stored.createdAt, stored.updatedAt)
        }

    @Test
    fun `invoke returns success with generated id`() =
        runTest {
            val result = addSubTaskUseCase(parentTaskId = 1L, title = "Valid SubTask")

            assertTrue(result.isSuccess)
            assertNotNull(result.getOrNull())
            assertEquals(1L, result.getOrNull())
        }

    @Test
    fun `invoke when repository fails returns failure`() =
        runTest {
            repository.shouldFailMutations = true

            val result = addSubTaskUseCase(parentTaskId = 1L, title = "Will Fail")

            assertTrue(result.isFailure)
        }
}
