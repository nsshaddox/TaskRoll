package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.Task
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetRandomTaskUseCaseTest {

    private lateinit var repository: FakeTaskRepository
    private lateinit var getRandomTaskUseCase: GetRandomTaskUseCase

    @Before
    fun setup() {
        repository = FakeTaskRepository()
        getRandomTaskUseCase = GetRandomTaskUseCase(repository)
    }

    @Test
    fun `invoke returns null when no tasks exist`() = runTest {
        val result = getRandomTaskUseCase()

        assertNull(result)
    }

    @Test
    fun `invoke returns null when all tasks are completed`() = runTest {
        repository.addTask(Task(title = "Done 1", isCompleted = true, createdAt = 1000L, updatedAt = 1000L))
        repository.addTask(Task(title = "Done 2", isCompleted = true, createdAt = 2000L, updatedAt = 2000L))

        val result = getRandomTaskUseCase()

        assertNull(result)
    }

    @Test
    fun `invoke returns incomplete task when tasks exist`() = runTest {
        repository.addTask(Task(title = "Incomplete", isCompleted = false, createdAt = 1000L, updatedAt = 1000L))
        repository.addTask(Task(title = "Complete", isCompleted = true, createdAt = 2000L, updatedAt = 2000L))

        val result = getRandomTaskUseCase()

        assertNotNull(result)
        assertTrue(!result!!.isCompleted)
    }
}
