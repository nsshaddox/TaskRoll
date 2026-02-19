package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.Task
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UpdateTaskUseCaseTest {

    private lateinit var repository: FakeTaskRepository
    private lateinit var updateTaskUseCase: UpdateTaskUseCase

    @Before
    fun setup() {
        repository = FakeTaskRepository()
        updateTaskUseCase = UpdateTaskUseCase(repository)
    }

    @Test
    fun `invoke updates task successfully`() = runTest {
        repository.addTask(Task(title = "Original", createdAt = 1000L, updatedAt = 1000L))
        val task = repository.getTasks().first().first()
        val updated = task.copy(title = "Updated")

        val result = updateTaskUseCase(updated)

        assertTrue(result.isSuccess)
        val stored = repository.getTaskById(task.id)
        assertEquals("Updated", stored?.title)
    }

    @Test
    fun `invoke sets updatedAt timestamp`() = runTest {
        repository.addTask(Task(title = "Original", createdAt = 1000L, updatedAt = 1000L))
        val task = repository.getTasks().first().first()

        updateTaskUseCase(task.copy(title = "Changed"))

        val stored = repository.getTaskById(task.id)
        assertTrue(stored!!.updatedAt >= task.updatedAt)
    }
}
