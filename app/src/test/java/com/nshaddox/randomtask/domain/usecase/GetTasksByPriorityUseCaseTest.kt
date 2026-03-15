package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.Task
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetTasksByPriorityUseCaseTest {
    private lateinit var repository: FakeTaskRepository
    private lateinit var getTasksByPriorityUseCase: GetTasksByPriorityUseCase

    @Before
    fun setup() {
        repository = FakeTaskRepository()
        getTasksByPriorityUseCase = GetTasksByPriorityUseCase(repository)
    }

    @Test
    fun `invoke returns empty list when no tasks match priority`() =
        runTest {
            repository.addTask(
                Task(title = "Low task", priority = Priority.LOW, createdAt = 1000L, updatedAt = 1000L),
            )

            val tasks = getTasksByPriorityUseCase(Priority.HIGH).first()

            assertTrue(tasks.isEmpty())
        }

    @Test
    fun `invoke returns only tasks matching the given priority`() =
        runTest {
            repository.addTask(
                Task(title = "High task 1", priority = Priority.HIGH, createdAt = 1000L, updatedAt = 1000L),
            )
            repository.addTask(
                Task(title = "Medium task", priority = Priority.MEDIUM, createdAt = 2000L, updatedAt = 2000L),
            )
            repository.addTask(
                Task(title = "High task 2", priority = Priority.HIGH, createdAt = 3000L, updatedAt = 3000L),
            )

            val tasks = getTasksByPriorityUseCase(Priority.HIGH).first()

            assertEquals(2, tasks.size)
            assertTrue(tasks.all { it.priority == Priority.HIGH })
            assertEquals("High task 1", tasks[0].title)
            assertEquals("High task 2", tasks[1].title)
        }

    @Test
    fun `invoke excludes completed tasks with matching priority`() =
        runTest {
            repository.addTask(
                Task(title = "Active high", priority = Priority.HIGH, createdAt = 1000L, updatedAt = 1000L),
            )
            repository.addTask(
                Task(
                    title = "Completed high",
                    priority = Priority.HIGH,
                    isCompleted = true,
                    createdAt = 2000L,
                    updatedAt = 2000L,
                ),
            )

            val tasks = getTasksByPriorityUseCase(Priority.HIGH).first()

            assertEquals(1, tasks.size)
            assertEquals("Active high", tasks[0].title)
        }
}
