package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.Task
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetCompletedTasksUseCaseTest {
    private lateinit var repository: FakeTaskRepository
    private lateinit var getCompletedTasksUseCase: GetCompletedTasksUseCase

    @Before
    fun setup() {
        repository = FakeTaskRepository()
        getCompletedTasksUseCase = GetCompletedTasksUseCase(repository)
    }

    @Test
    fun `invoke returns empty list when no tasks exist`() =
        runTest {
            val tasks = getCompletedTasksUseCase().first()

            assertTrue(tasks.isEmpty())
        }

    @Test
    fun `invoke returns only completed tasks`() =
        runTest {
            repository.addTask(
                Task(title = "Completed Task", isCompleted = true, createdAt = 1000L, updatedAt = 1000L),
            )
            repository.addTask(
                Task(title = "Incomplete Task", isCompleted = false, createdAt = 2000L, updatedAt = 2000L),
            )
            repository.addTask(
                Task(title = "Another Completed", isCompleted = true, createdAt = 3000L, updatedAt = 3000L),
            )

            val tasks = getCompletedTasksUseCase().first()

            assertEquals(2, tasks.size)
            assertTrue(tasks.all { it.isCompleted })
            assertEquals("Completed Task", tasks[0].title)
            assertEquals("Another Completed", tasks[1].title)
        }
}
