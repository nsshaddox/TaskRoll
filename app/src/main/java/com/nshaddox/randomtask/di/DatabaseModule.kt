package com.nshaddox.randomtask.di

import android.content.Context
import androidx.room.Room
import com.nshaddox.randomtask.data.local.AppDatabase
import com.nshaddox.randomtask.data.local.SubTaskDao
import com.nshaddox.randomtask.data.local.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "random_task_db",
        ).addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
            .build()
    }

    @Provides
    @Singleton
    fun provideTaskDao(db: AppDatabase): TaskDao {
        return db.taskDao()
    }

    @Provides
    @Singleton
    fun provideSubTaskDao(db: AppDatabase): SubTaskDao {
        return db.subTaskDao()
    }
}
