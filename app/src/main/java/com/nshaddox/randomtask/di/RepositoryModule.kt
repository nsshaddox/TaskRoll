package com.nshaddox.randomtask.di

import com.nshaddox.randomtask.data.repository.SubTaskRepositoryImpl
import com.nshaddox.randomtask.data.repository.TaskRepositoryImpl
import com.nshaddox.randomtask.domain.repository.SubTaskRepository
import com.nshaddox.randomtask.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository

    @Binds
    @Singleton
    abstract fun bindSubTaskRepository(impl: SubTaskRepositoryImpl): SubTaskRepository
}
