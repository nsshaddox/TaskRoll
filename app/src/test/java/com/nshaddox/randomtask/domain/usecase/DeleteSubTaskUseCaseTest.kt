package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.SubTask
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DeleteSubTaskUseCaseTest {
    private lateinit var repository: FakeSubTaskRepository
    private lateinit var deleteSubTaskUseCase: DeleteSubTaskUseCase

    @Before
    fun setup() {
        repository = FakeSubTaskRepository()
        deleteSubTaskUseCase = DeleteSubTaskUseCase(repository)
    }

    @Test
    fun `invoke deletes subtask from repository`() =
        runTest {
            repository.addSubTask(
                SubTask(
                    parentTaskId = 1L,
                    title = "To Delete",
                    createdAt = 1000L,
                    updatedAt = 1000L,
                ),
            )
            val stored = repository.getAllSubTasksSnapshot().first()

            deleteSubTaskUseCase(stored.id)

            assertTrue(repository.getAllSubTasksSnapshot().isEmpty())
        }

    @Test
    fun `invoke returns success on deletion`() =
        runTest {
            repository.addSubTask(
                SubTask(
                    parentTaskId = 1L,
                    title = "To Delete",
                    createdAt = 1000L,
                    updatedAt = 1000L,
                ),
            )

            val result = deleteSubTaskUseCase(1L)

            assertTrue(result.isSuccess)
        }

    @Test
    fun `invoke when repository fails returns failure`() =
        runTest {
            repository.shouldFailMutations = true

            val result = deleteSubTaskUseCase(1L)

            assertTrue(result.isFailure)
        }
}
