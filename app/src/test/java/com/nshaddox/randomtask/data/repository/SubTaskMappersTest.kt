package com.nshaddox.randomtask.data.repository

import com.nshaddox.randomtask.data.local.SubTaskEntity
import com.nshaddox.randomtask.domain.model.SubTask
import org.junit.Assert.assertEquals
import org.junit.Test

class SubTaskMappersTest {
    @Test
    fun `entity to domain maps all fields correctly`() {
        val entity =
            SubTaskEntity(
                id = 1L,
                parentTaskId = 10L,
                title = "Test SubTask",
                isCompleted = true,
                createdAt = 1000L,
                updatedAt = 2000L,
            )

        val domain = entity.toDomain()

        assertEquals(1L, domain.id)
        assertEquals(10L, domain.parentTaskId)
        assertEquals("Test SubTask", domain.title)
        assertEquals(true, domain.isCompleted)
        assertEquals(1000L, domain.createdAt)
        assertEquals(2000L, domain.updatedAt)
    }

    @Test
    fun `domain to entity maps all fields correctly`() {
        val domain =
            SubTask(
                id = 2L,
                parentTaskId = 20L,
                title = "Domain SubTask",
                isCompleted = false,
                createdAt = 3000L,
                updatedAt = 4000L,
            )

        val entity = domain.toEntity()

        assertEquals(2L, entity.id)
        assertEquals(20L, entity.parentTaskId)
        assertEquals("Domain SubTask", entity.title)
        assertEquals(false, entity.isCompleted)
        assertEquals(3000L, entity.createdAt)
        assertEquals(4000L, entity.updatedAt)
    }

    @Test
    fun `round trip entity to domain to entity preserves all fields`() {
        val original =
            SubTaskEntity(
                id = 5L,
                parentTaskId = 50L,
                title = "Round Trip",
                isCompleted = true,
                createdAt = 5000L,
                updatedAt = 6000L,
            )

        val roundTripped = original.toDomain().toEntity()

        assertEquals(original, roundTripped)
    }

    @Test
    fun `round trip domain to entity to domain preserves all fields`() {
        val original =
            SubTask(
                id = 10L,
                parentTaskId = 100L,
                title = "Round Trip Domain",
                isCompleted = false,
                createdAt = 7000L,
                updatedAt = 8000L,
            )

        val roundTripped = original.toEntity().toDomain()

        assertEquals(original, roundTripped)
    }

    @Test
    fun `completed subtask maps isCompleted correctly`() {
        val entity =
            SubTaskEntity(
                id = 3L,
                parentTaskId = 30L,
                title = "Completed",
                isCompleted = true,
                createdAt = 9000L,
                updatedAt = 10000L,
            )

        val domain = entity.toDomain()
        assertEquals(true, domain.isCompleted)

        val backToEntity = domain.toEntity()
        assertEquals(true, backToEntity.isCompleted)
        assertEquals(entity, backToEntity)
    }

    @Test
    fun `incomplete subtask maps isCompleted correctly`() {
        val entity =
            SubTaskEntity(
                id = 4L,
                parentTaskId = 40L,
                title = "Incomplete",
                isCompleted = false,
                createdAt = 11000L,
                updatedAt = 12000L,
            )

        val domain = entity.toDomain()
        assertEquals(false, domain.isCompleted)

        val backToEntity = domain.toEntity()
        assertEquals(false, backToEntity.isCompleted)
        assertEquals(entity, backToEntity)
    }
}
