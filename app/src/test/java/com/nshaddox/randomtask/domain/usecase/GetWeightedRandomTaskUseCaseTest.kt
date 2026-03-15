package com.nshaddox.randomtask.domain.usecase

import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.Task
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetWeightedRandomTaskUseCaseTest {
    private lateinit var repository: FakeTaskRepository
    private lateinit var getWeightedRandomTaskUseCase: GetWeightedRandomTaskUseCase

    @Before
    fun setup() {
        repository = FakeTaskRepository()
        getWeightedRandomTaskUseCase = GetWeightedRandomTaskUseCase(repository)
    }

    @Test
    fun `invoke returns null when no tasks exist`() =
        runTest {
            val result = getWeightedRandomTaskUseCase()

            assertNull(result)
        }

    @Test
    fun `invoke returns null when all tasks are completed`() =
        runTest {
            repository.addTask(
                Task(
                    title = "Done 1",
                    isCompleted = true,
                    createdAt = 1000L,
                    updatedAt = 1000L,
                    priority = Priority.HIGH,
                ),
            )
            repository.addTask(
                Task(
                    title = "Done 2",
                    isCompleted = true,
                    createdAt = 2000L,
                    updatedAt = 2000L,
                    priority = Priority.LOW,
                ),
            )

            val result = getWeightedRandomTaskUseCase()

            assertNull(result)
        }

    @Test
    fun `invoke returns single LOW-priority incomplete task`() =
        runTest {
            repository.addTask(
                Task(
                    title = "Only Task",
                    isCompleted = false,
                    createdAt = 1000L,
                    updatedAt = 1000L,
                    priority = Priority.LOW,
                ),
            )

            val result = getWeightedRandomTaskUseCase()

            assertNotNull(result)
            assertEquals("Only Task", result!!.title)
            assertEquals(Priority.LOW, result.priority)
        }

    @Test
    fun `buildWeightedPool produces correct pool size for HIGH and LOW tasks`() {
        val highTask =
            Task(
                id = 1,
                title = "High",
                isCompleted = false,
                createdAt = 1000L,
                updatedAt = 1000L,
                priority = Priority.HIGH,
            )
        val lowTask =
            Task(
                id = 2,
                title = "Low",
                isCompleted = false,
                createdAt = 2000L,
                updatedAt = 2000L,
                priority = Priority.LOW,
            )

        val pool = getWeightedRandomTaskUseCase.buildWeightedPool(listOf(highTask, lowTask))

        // HIGH=3 + LOW=1 = 4 total entries
        assertEquals(4, pool.size)
        assertEquals(3, pool.count { it.id == highTask.id })
        assertEquals(1, pool.count { it.id == lowTask.id })
    }

    @Test
    fun `buildWeightedPool produces correct pool size for mixed priorities`() {
        val highTask =
            Task(
                id = 1,
                title = "High",
                isCompleted = false,
                createdAt = 1000L,
                updatedAt = 1000L,
                priority = Priority.HIGH,
            )
        val mediumTask =
            Task(
                id = 2,
                title = "Medium",
                isCompleted = false,
                createdAt = 2000L,
                updatedAt = 2000L,
                priority = Priority.MEDIUM,
            )
        val lowTask =
            Task(
                id = 3,
                title = "Low",
                isCompleted = false,
                createdAt = 3000L,
                updatedAt = 3000L,
                priority = Priority.LOW,
            )

        val pool = getWeightedRandomTaskUseCase.buildWeightedPool(listOf(highTask, mediumTask, lowTask))

        // HIGH=3 + MEDIUM=2 + LOW=1 = 6 total entries
        assertEquals(6, pool.size)
        assertEquals(3, pool.count { it.id == highTask.id })
        assertEquals(2, pool.count { it.id == mediumTask.id })
        assertEquals(1, pool.count { it.id == lowTask.id })
    }

    @Test
    fun `invoke returns non-null task when incomplete tasks exist`() =
        runTest {
            repository.addTask(
                Task(
                    title = "Task A",
                    isCompleted = false,
                    createdAt = 1000L,
                    updatedAt = 1000L,
                    priority = Priority.HIGH,
                ),
            )
            repository.addTask(
                Task(
                    title = "Task B",
                    isCompleted = false,
                    createdAt = 2000L,
                    updatedAt = 2000L,
                    priority = Priority.LOW,
                ),
            )

            val result = getWeightedRandomTaskUseCase()

            assertNotNull(result)
            assertTrue(result!!.title == "Task A" || result.title == "Task B")
        }
}
