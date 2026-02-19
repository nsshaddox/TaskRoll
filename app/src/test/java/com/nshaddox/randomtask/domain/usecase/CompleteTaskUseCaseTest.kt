package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.Task
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CompleteTaskUseCaseTest {

    private lateinit var repository: FakeTaskRepository
    private lateinit var completeTaskUseCase: CompleteTaskUseCase

    @Before
    fun setup() {
        repository = FakeTaskRepository()
        completeTaskUseCase = CompleteTaskUseCase(repository)
    }

    @Test
    fun `invoke marks task as completed`() = runTest {
        repository.addTask(Task(title = "Incomplete", isCompleted = false, createdAt = 1000L, updatedAt = 1000L))
        val task = repository.getTaskById(1L)!!

        val result = completeTaskUseCase(task)

        assertTrue(result.isSuccess)
        val stored = repository.getTaskById(task.id)
        assertEquals(true, stored?.isCompleted)
    }

    @Test
    fun `invoke updates updatedAt timestamp`() = runTest {
        repository.addTask(Task(title = "Incomplete", isCompleted = false, createdAt = 1000L, updatedAt = 1000L))
        val task = repository.getTaskById(1L)!!

        completeTaskUseCase(task)

        val stored = repository.getTaskById(task.id)
        assertTrue(stored!!.updatedAt >= task.updatedAt)
    }
}
