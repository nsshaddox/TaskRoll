package com.nshaddox.randomtask.data.repository

import com.nshaddox.randomtask.data.local.TaskEntity
import com.nshaddox.randomtask.domain.model.Task
import org.junit.Assert.assertEquals
import org.junit.Test

class TaskMappersTest {

    @Test
    fun `entity to domain maps all fields correctly`() {
        val entity = TaskEntity(
            id = 1L,
            title = "Test Task",
            description = "Test Description",
            isCompleted = true,
            createdAt = 1000L,
            updatedAt = 2000L
        )

        val domain = entity.toDomain()

        assertEquals(1L, domain.id)
        assertEquals("Test Task", domain.title)
        assertEquals("Test Description", domain.description)
        assertEquals(true, domain.isCompleted)
        assertEquals(1000L, domain.createdAt)
        assertEquals(2000L, domain.updatedAt)
    }

    @Test
    fun `domain to entity maps all fields correctly`() {
        val domain = Task(
            id = 2L,
            title = "Domain Task",
            description = "Domain Description",
            isCompleted = false,
            createdAt = 3000L,
            updatedAt = 4000L
        )

        val entity = domain.toEntity()

        assertEquals(2L, entity.id)
        assertEquals("Domain Task", entity.title)
        assertEquals("Domain Description", entity.description)
        assertEquals(false, entity.isCompleted)
        assertEquals(3000L, entity.createdAt)
        assertEquals(4000L, entity.updatedAt)
    }

    @Test
    fun `round trip entity to domain to entity preserves all fields`() {
        val original = TaskEntity(
            id = 5L,
            title = "Round Trip",
            description = "Testing round trip",
            isCompleted = true,
            createdAt = 5000L,
            updatedAt = 6000L
        )

        val roundTripped = original.toDomain().toEntity()

        assertEquals(original, roundTripped)
    }

    @Test
    fun `round trip domain to entity to domain preserves all fields`() {
        val original = Task(
            id = 10L,
            title = "Round Trip Domain",
            description = "Testing domain round trip",
            isCompleted = false,
            createdAt = 7000L,
            updatedAt = 8000L
        )

        val roundTripped = original.toEntity().toDomain()

        assertEquals(original, roundTripped)
    }
}
