package com.nshaddox.randomtask

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

interface TaskRepository {
    fun getTaskCount(): Int
}

class ExampleUnitTest {

    @Test
    fun `mock returns expected value`() {
        val repository = mockk<TaskRepository>()
        every { repository.getTaskCount() } returns 5

        val result = repository.getTaskCount()

        assertEquals(5, result)
        verify(exactly = 1) { repository.getTaskCount() }
    }

    @Test
    fun `flow emits expected values`() = runTest {
        val values = flow {
            emit("task1")
            emit("task2")
            emit("task3")
        }

        values.test {
            assertEquals("task1", awaitItem())
            assertEquals("task2", awaitItem())
            assertEquals("task3", awaitItem())
            awaitComplete()
        }
    }
}
