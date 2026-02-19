package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.Task
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
    fun `invoke with valid title returns success with id`() = runTest {
        val task = Task(title = "Valid Task", createdAt = 1000L, updatedAt = 1000L)

        val result = addTaskUseCase(task)

        assertTrue(result.isSuccess)
        assertEquals(1L, result.getOrNull())
    }

    @Test
    fun `invoke with blank title returns failure`() = runTest {
        val task = Task(title = "   ", createdAt = 1000L, updatedAt = 1000L)

        val result = addTaskUseCase(task)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `invoke with empty title returns failure`() = runTest {
        val task = Task(title = "", createdAt = 1000L, updatedAt = 1000L)

        val result = addTaskUseCase(task)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }
}
