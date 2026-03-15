package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.Task
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetTasksByCategoryUseCaseTest {
    private lateinit var repository: FakeTaskRepository
    private lateinit var getTasksByCategoryUseCase: GetTasksByCategoryUseCase

    @Before
    fun setup() {
        repository = FakeTaskRepository()
        getTasksByCategoryUseCase = GetTasksByCategoryUseCase(repository)
    }

    @Test
    fun `invoke returns empty list when no tasks match category`() =
        runTest {
            repository.addTask(
                Task(title = "Work task", category = "Work", createdAt = 1000L, updatedAt = 1000L),
            )

            val tasks = getTasksByCategoryUseCase("Personal").first()

            assertTrue(tasks.isEmpty())
        }

    @Test
    fun `invoke returns only tasks matching the given category`() =
        runTest {
            repository.addTask(
                Task(title = "Work task 1", category = "Work", createdAt = 1000L, updatedAt = 1000L),
            )
            repository.addTask(
                Task(title = "Personal task", category = "Personal", createdAt = 2000L, updatedAt = 2000L),
            )
            repository.addTask(
                Task(title = "Work task 2", category = "Work", createdAt = 3000L, updatedAt = 3000L),
            )

            val tasks = getTasksByCategoryUseCase("Work").first()

            assertEquals(2, tasks.size)
            assertTrue(tasks.all { it.category == "Work" })
            assertEquals("Work task 1", tasks[0].title)
            assertEquals("Work task 2", tasks[1].title)
        }

    @Test
    fun `invoke excludes completed tasks with matching category`() =
        runTest {
            repository.addTask(
                Task(title = "Active work", category = "Work", createdAt = 1000L, updatedAt = 1000L),
            )
            repository.addTask(
                Task(
                    title = "Completed work",
                    category = "Work",
                    isCompleted = true,
                    createdAt = 2000L,
                    updatedAt = 2000L,
                ),
            )

            val tasks = getTasksByCategoryUseCase("Work").first()

            assertEquals(1, tasks.size)
            assertEquals("Active work", tasks[0].title)
        }
}
