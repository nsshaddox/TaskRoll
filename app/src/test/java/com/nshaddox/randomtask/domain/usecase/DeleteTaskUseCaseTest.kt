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
        val tasks = repository.getAllTasks().first()
        val task = tasks.first()

        val result = deleteTaskUseCase(task)

        assertTrue(result.isSuccess)
        assertEquals(0, repository.getAllTasks().first().size)
    }

    @Test
    fun `invoke with non-existent task returns failure`() = runTest {
        val nonExistentTask = Task(id = 999L, title = "Ghost", createdAt = 1000L, updatedAt = 1000L)
        val result = deleteTaskUseCase(nonExistentTask)

        assertTrue(result.isFailure)
    }
}
