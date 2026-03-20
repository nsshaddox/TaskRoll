package com.nshaddox.randomtask.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Suppress("MagicNumber")
@Database(entities = [TaskEntity::class, SubTaskEntity::class], version = 3, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    abstract fun subTaskDao(): SubTaskDao

    companion object {
        val MIGRATION_1_2 =
            object : Migration(1, 2) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("ALTER TABLE tasks ADD COLUMN priority TEXT NOT NULL DEFAULT 'MEDIUM'")
                    db.execSQL("ALTER TABLE tasks ADD COLUMN due_date INTEGER")
                    db.execSQL("ALTER TABLE tasks ADD COLUMN category TEXT")
                }
            }

        val MIGRATION_2_3 =
            object : Migration(2, 3) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `subtasks` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`parent_task_id` INTEGER NOT NULL, " +
                            "`title` TEXT NOT NULL, " +
                            "`is_completed` INTEGER NOT NULL DEFAULT 0, " +
                            "`created_at` INTEGER NOT NULL, " +
                            "`updated_at` INTEGER NOT NULL, " +
                            "FOREIGN KEY(`parent_task_id`) REFERENCES `tasks`(`id`) ON DELETE CASCADE)",
                    )
                    db.execSQL(
                        "CREATE INDEX IF NOT EXISTS `index_subtasks_parent_task_id` ON `subtasks` (`parent_task_id`)",
                    )
                }
            }
    }
}
