package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.SubTask
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CompleteSubTaskUseCaseTest {
    private lateinit var repository: FakeSubTaskRepository
    private lateinit var completeSubTaskUseCase: CompleteSubTaskUseCase

    @Before
    fun setup() {
        repository = FakeSubTaskRepository()
        completeSubTaskUseCase = CompleteSubTaskUseCase(repository)
    }

    private fun createSubTask(
        id: Long = 1L,
        parentTaskId: Long = 1L,
    ) = SubTask(
        id = id,
        parentTaskId = parentTaskId,
        title = "Test SubTask",
        isCompleted = false,
        createdAt = 1000L,
        updatedAt = 1000L,
    )

    @Test
    fun `invoke marks subtask as completed`() =
        runTest {
            repository.addSubTask(createSubTask())
            val stored = repository.getAllSubTasksSnapshot().first()

            completeSubTaskUseCase(stored)

            val updated = repository.getAllSubTasksSnapshot().first()
            assertTrue(updated.isCompleted)
        }

    @Test
    fun `invoke updates updatedAt timestamp`() =
        runTest {
            repository.addSubTask(createSubTask())
            val stored = repository.getAllSubTasksSnapshot().first()
            val before = System.currentTimeMillis()

            completeSubTaskUseCase(stored)

            val updated = repository.getAllSubTasksSnapshot().first()
            assertTrue(updated.updatedAt >= before)
        }

    @Test
    fun `invoke when repository fails returns failure`() =
        runTest {
            repository.addSubTask(createSubTask())
            val stored = repository.getAllSubTasksSnapshot().first()
            repository.shouldFailMutations = true

            val result = completeSubTaskUseCase(stored)

            assertTrue(result.isFailure)
        }

    @Test
    fun `invoke returns success on completion`() =
        runTest {
            repository.addSubTask(createSubTask())
            val stored = repository.getAllSubTasksSnapshot().first()

            val result = completeSubTaskUseCase(stored)

            assertTrue(result.isSuccess)
        }
}
