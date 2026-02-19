package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.Task
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DeleteTaskUseCaseTest {

    private lateinit var repository: FakeTaskRepository
    private lateinit var deleteTaskUseCase: DeleteTaskUseCase

    @Before
    fun setup() {
        repository = FakeTaskRepository()
        deleteTaskUseCase = DeleteTaskUseCase(repository)
    }

    @Test
    fun `invoke deletes existing task successfully`() = runTest {
        repository.addTask(Task(title = "To Delete", createdAt = 1000L, updatedAt = 1000L))
        val tasks = repository.getTasks().first()
        val taskId = tasks.first().id

        val result = deleteTaskUseCase(taskId)

        assertTrue(result.isSuccess)
        assertEquals(0, repository.getTasks().first().size)
    }

    @Test
    fun `invoke with non-existent id returns failure`() = runTest {
        val result = deleteTaskUseCase(999L)

        assertTrue(result.isFailure)
    }
}
