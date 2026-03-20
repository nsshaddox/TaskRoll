package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.SubTask
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UncompleteSubTaskUseCaseTest {
    private lateinit var repository: FakeSubTaskRepository
    private lateinit var uncompleteSubTaskUseCase: UncompleteSubTaskUseCase

    @Before
    fun setup() {
        repository = FakeSubTaskRepository()
        uncompleteSubTaskUseCase = UncompleteSubTaskUseCase(repository)
    }

    private fun createCompletedSubTask(
        id: Long = 1L,
        parentTaskId: Long = 1L,
    ) = SubTask(
        id = id,
        parentTaskId = parentTaskId,
        title = "Completed SubTask",
        isCompleted = true,
        createdAt = 1000L,
        updatedAt = 2000L,
    )

    @Test
    fun `invoke marks completed subtask as incomplete`() =
        runTest {
            repository.addSubTask(createCompletedSubTask())
            val stored = repository.getAllSubTasksSnapshot().first()

            uncompleteSubTaskUseCase(stored)

            val updated = repository.getAllSubTasksSnapshot().first()
            assertFalse(updated.isCompleted)
        }

    @Test
    fun `invoke updates updatedAt timestamp`() =
        runTest {
            repository.addSubTask(createCompletedSubTask())
            val stored = repository.getAllSubTasksSnapshot().first()
            val before = System.currentTimeMillis()

            uncompleteSubTaskUseCase(stored)

            val updated = repository.getAllSubTasksSnapshot().first()
            assertTrue(updated.updatedAt >= before)
        }

    @Test
    fun `invoke when repository fails returns failure`() =
        runTest {
            repository.addSubTask(createCompletedSubTask())
            val stored = repository.getAllSubTasksSnapshot().first()
            repository.shouldFailMutations = true

            val result = uncompleteSubTaskUseCase(stored)

            assertTrue(result.isFailure)
        }

    @Test
    fun `invoke returns success on uncomplete`() =
        runTest {
            repository.addSubTask(createCompletedSubTask())
            val stored = repository.getAllSubTasksSnapshot().first()

            val result = uncompleteSubTaskUseCase(stored)

            assertTrue(result.isSuccess)
        }
}
