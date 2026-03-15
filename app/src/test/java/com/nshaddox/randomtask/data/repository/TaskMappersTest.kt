package com.nshaddox.randomtask.data.repository

import com.nshaddox.randomtask.data.local.TaskEntity
import com.nshaddox.randomtask.domain.model.Priority
import com.nshaddox.randomtask.domain.model.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TaskMappersTest {
    @Test
    fun `entity to domain maps all fields correctly`() {
        val entity =
            TaskEntity(
                id = 1L,
                title = "Test Task",
                description = "Test Description",
                isCompleted = true,
                createdAt = 1000L,
                updatedAt = 2000L,
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
        val domain =
            Task(
                id = 2L,
                title = "Domain Task",
                description = "Domain Description",
                isCompleted = false,
                createdAt = 3000L,
                updatedAt = 4000L,
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
        val original =
            TaskEntity(
                id = 5L,
                title = "Round Trip",
                description = "Testing round trip",
                isCompleted = true,
                createdAt = 5000L,
                updatedAt = 6000L,
            )

        val roundTripped = original.toDomain().toEntity()

        assertEquals(original, roundTripped)
    }

    @Test
    fun `null description maps correctly between entity and domain`() {
        val entity =
            TaskEntity(
                id = 3L,
                title = "No Description",
                description = null,
                isCompleted = false,
                createdAt = 9000L,
                updatedAt = 10000L,
            )

        val domain = entity.toDomain()
        assertEquals(null, domain.description)

        val backToEntity = domain.toEntity()
        assertEquals(null, backToEntity.description)
        assertEquals(entity, backToEntity)
    }

    @Test
    fun `round trip domain to entity to domain preserves all fields`() {
        val original =
            Task(
                id = 10L,
                title = "Round Trip Domain",
                description = "Testing domain round trip",
                isCompleted = false,
                createdAt = 7000L,
                updatedAt = 8000L,
            )

        val roundTripped = original.toEntity().toDomain()

        assertEquals(original, roundTripped)
    }

    // --- Priority mapping tests ---

    @Test
    fun `toDomain maps LOW priority string to Priority LOW`() {
        val entity =
            TaskEntity(
                id = 20L,
                title = "Low Priority",
                createdAt = 1000L,
                updatedAt = 2000L,
                priority = "LOW",
            )

        val domain = entity.toDomain()

        assertEquals(Priority.LOW, domain.priority)
    }

    @Test
    fun `toDomain maps MEDIUM priority string to Priority MEDIUM`() {
        val entity =
            TaskEntity(
                id = 21L,
                title = "Medium Priority",
                createdAt = 1000L,
                updatedAt = 2000L,
                priority = "MEDIUM",
            )

        val domain = entity.toDomain()

        assertEquals(Priority.MEDIUM, domain.priority)
    }

    @Test
    fun `toDomain maps HIGH priority string to Priority HIGH`() {
        val entity =
            TaskEntity(
                id = 22L,
                title = "High Priority",
                createdAt = 1000L,
                updatedAt = 2000L,
                priority = "HIGH",
            )

        val domain = entity.toDomain()

        assertEquals(Priority.HIGH, domain.priority)
    }

    @Test
    fun `toEntity maps Priority LOW to LOW string`() {
        val task =
            Task(
                id = 23L,
                title = "Low Priority",
                createdAt = 1000L,
                updatedAt = 2000L,
                priority = Priority.LOW,
            )

        val entity = task.toEntity()

        assertEquals("LOW", entity.priority)
    }

    @Test
    fun `toEntity maps Priority HIGH to HIGH string`() {
        val task =
            Task(
                id = 24L,
                title = "High Priority",
                createdAt = 1000L,
                updatedAt = 2000L,
                priority = Priority.HIGH,
            )

        val entity = task.toEntity()

        assertEquals("HIGH", entity.priority)
    }

    // --- dueDate mapping tests ---

    @Test
    fun `toDomain maps non-null dueDate correctly`() {
        val epochDays = 19800L
        val entity =
            TaskEntity(
                id = 30L,
                title = "With Due Date",
                createdAt = 1000L,
                updatedAt = 2000L,
                dueDate = epochDays,
            )

        val domain = entity.toDomain()

        assertEquals(epochDays, domain.dueDate)
    }

    @Test
    fun `toDomain maps null dueDate correctly`() {
        val entity =
            TaskEntity(
                id = 31L,
                title = "No Due Date",
                createdAt = 1000L,
                updatedAt = 2000L,
                dueDate = null,
            )

        val domain = entity.toDomain()

        assertNull(domain.dueDate)
    }

    @Test
    fun `toEntity maps non-null dueDate correctly`() {
        val epochDays = 19800L
        val task =
            Task(
                id = 32L,
                title = "With Due Date",
                createdAt = 1000L,
                updatedAt = 2000L,
                dueDate = epochDays,
            )

        val entity = task.toEntity()

        assertEquals(epochDays, entity.dueDate)
    }

    @Test
    fun `toEntity maps null dueDate correctly`() {
        val task =
            Task(
                id = 33L,
                title = "No Due Date",
                createdAt = 1000L,
                updatedAt = 2000L,
                dueDate = null,
            )

        val entity = task.toEntity()

        assertNull(entity.dueDate)
    }

    // --- category mapping tests ---

    @Test
    fun `toDomain maps non-null category correctly`() {
        val entity =
            TaskEntity(
                id = 40L,
                title = "With Category",
                createdAt = 1000L,
                updatedAt = 2000L,
                category = "Work",
            )

        val domain = entity.toDomain()

        assertEquals("Work", domain.category)
    }

    @Test
    fun `toDomain maps null category correctly`() {
        val entity =
            TaskEntity(
                id = 41L,
                title = "No Category",
                createdAt = 1000L,
                updatedAt = 2000L,
                category = null,
            )

        val domain = entity.toDomain()

        assertNull(domain.category)
    }

    @Test
    fun `toEntity maps non-null category correctly`() {
        val task =
            Task(
                id = 42L,
                title = "With Category",
                createdAt = 1000L,
                updatedAt = 2000L,
                category = "Personal",
            )

        val entity = task.toEntity()

        assertEquals("Personal", entity.category)
    }

    @Test
    fun `toEntity maps null category correctly`() {
        val task =
            Task(
                id = 43L,
                title = "No Category",
                createdAt = 1000L,
                updatedAt = 2000L,
                category = null,
            )

        val entity = task.toEntity()

        assertNull(entity.category)
    }

    // --- Full round-trip with all new fields ---

    @Test
    fun `round trip entity to domain to entity preserves all new fields`() {
        val original =
            TaskEntity(
                id = 50L,
                title = "Full Round Trip",
                description = "All fields populated",
                isCompleted = false,
                createdAt = 5000L,
                updatedAt = 6000L,
                priority = "HIGH",
                dueDate = 19800L,
                category = "Work",
            )

        val roundTripped = original.toDomain().toEntity()

        assertEquals(original, roundTripped)
    }

    @Test
    fun `round trip domain to entity to domain preserves all new fields`() {
        val original =
            Task(
                id = 51L,
                title = "Full Round Trip Domain",
                description = "All fields populated",
                isCompleted = true,
                createdAt = 7000L,
                updatedAt = 8000L,
                priority = Priority.LOW,
                dueDate = 20000L,
                category = "Personal",
            )

        val roundTripped = original.toEntity().toDomain()

        assertEquals(original, roundTripped)
    }

    @Test
    fun `toDomain maps unrecognized priority string to MEDIUM`() {
        val entity =
            TaskEntity(
                id = 60L,
                title = "Unknown Priority",
                createdAt = 1000L,
                updatedAt = 2000L,
                priority = "INVALID",
            )

        val domain = entity.toDomain()

        assertEquals(Priority.MEDIUM, domain.priority)
    }
}
