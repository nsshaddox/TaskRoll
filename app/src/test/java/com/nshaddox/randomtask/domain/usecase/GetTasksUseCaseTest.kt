package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.Task
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetTasksUseCaseTest {

    private lateinit var repository: FakeTaskRepository
    private lateinit var getTasksUseCase: GetTasksUseCase

    @Before
    fun setup() {
        repository = FakeTaskRepository()
        getTasksUseCase = GetTasksUseCase(repository)
    }

    @Test
    fun `invoke returns empty list when no tasks exist`() = runTest {
        val tasks = getTasksUseCase().first()

        assertTrue(tasks.isEmpty())
    }

    @Test
    fun `invoke returns tasks after adding`() = runTest {
        repository.addTask(Task(title = "Task 1", createdAt = 1000L, updatedAt = 1000L))
        repository.addTask(Task(title = "Task 2", createdAt = 2000L, updatedAt = 2000L))

        val tasks = getTasksUseCase().first()

        assertEquals(2, tasks.size)
        assertEquals("Task 1", tasks[0].title)
        assertEquals("Task 2", tasks[1].title)
    }
}
