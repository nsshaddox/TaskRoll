package com.nshaddox.randomtask.data.local

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MigrationTest {
    companion object {
        private const val TEST_DB = "migration-test"
    }

    @get:Rule
    val helper =
        MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            AppDatabase::class.java,
        )

    @Test
    fun migrate1To2() {
        // Create version 1 database and insert a row
        helper.createDatabase(TEST_DB, 1).apply {
            execSQL(
                """
                INSERT INTO tasks (title, description, is_completed, created_at, updated_at)
                VALUES ('Test Task', 'Test Description', 0, 1000, 2000)
                """.trimIndent(),
            )
            close()
        }

        // Run migration and validate schema
        val db =
            helper.runMigrationsAndValidate(
                TEST_DB,
                2,
                true,
                AppDatabase.MIGRATION_1_2,
            )

        // Verify the migrated row exists with correct defaults for new columns
        val cursor = db.query("SELECT * FROM tasks")
        assertTrue("Expected at least one row after migration", cursor.moveToFirst())

        val titleIndex = cursor.getColumnIndex("title")
        val priorityIndex = cursor.getColumnIndex("priority")
        val dueDateIndex = cursor.getColumnIndex("due_date")
        val categoryIndex = cursor.getColumnIndex("category")

        assertEquals("Test Task", cursor.getString(titleIndex))
        assertEquals("MEDIUM", cursor.getString(priorityIndex))
        assertTrue("due_date should be NULL", cursor.isNull(dueDateIndex))
        assertTrue("category should be NULL", cursor.isNull(categoryIndex))

        cursor.close()
        db.close()
    }
}
