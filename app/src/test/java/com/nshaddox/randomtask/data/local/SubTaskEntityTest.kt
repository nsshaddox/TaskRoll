package com.nshaddox.randomtask.data.local

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SubTaskEntityTest {
    @Test
    fun `default id is 0`() {
        val entity =
            SubTaskEntity(
                parentTaskId = 1L,
                title = "Test",
                createdAt = 1000L,
                updatedAt = 2000L,
            )

        assertEquals(0L, entity.id)
    }

    @Test
    fun `default isCompleted is false`() {
        val entity =
            SubTaskEntity(
                parentTaskId = 1L,
                title = "Test",
                createdAt = 1000L,
                updatedAt = 2000L,
            )

        assertFalse(entity.isCompleted)
    }

    @Test
    fun `entity stores all fields correctly`() {
        val entity =
            SubTaskEntity(
                id = 5L,
                parentTaskId = 10L,
                title = "Full Entity",
                isCompleted = true,
                createdAt = 3000L,
                updatedAt = 4000L,
            )

        assertEquals(5L, entity.id)
        assertEquals(10L, entity.parentTaskId)
        assertEquals("Full Entity", entity.title)
        assertEquals(true, entity.isCompleted)
        assertEquals(3000L, entity.createdAt)
        assertEquals(4000L, entity.updatedAt)
    }

    @Test
    fun `copy preserves unchanged fields`() {
        val original =
            SubTaskEntity(
                id = 1L,
                parentTaskId = 10L,
                title = "Original",
                isCompleted = false,
                createdAt = 1000L,
                updatedAt = 2000L,
            )

        val copied = original.copy(isCompleted = true)

        assertEquals(1L, copied.id)
        assertEquals(10L, copied.parentTaskId)
        assertEquals("Original", copied.title)
        assertEquals(true, copied.isCompleted)
        assertEquals(1000L, copied.createdAt)
        assertEquals(2000L, copied.updatedAt)
    }

    @Test
    fun `two entities with same values are equal`() {
        val entity1 =
            SubTaskEntity(
                id = 1L,
                parentTaskId = 10L,
                title = "Test",
                isCompleted = false,
                createdAt = 1000L,
                updatedAt = 2000L,
            )
        val entity2 =
            SubTaskEntity(
                id = 1L,
                parentTaskId = 10L,
                title = "Test",
                isCompleted = false,
                createdAt = 1000L,
                updatedAt = 2000L,
            )

        assertEquals(entity1, entity2)
    }
}
