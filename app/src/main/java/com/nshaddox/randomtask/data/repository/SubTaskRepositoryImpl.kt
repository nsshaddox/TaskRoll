package com.nshaddox.randomtask.data.repository

import com.nshaddox.randomtask.data.local.SubTaskDao
import com.nshaddox.randomtask.domain.model.SubTask
import com.nshaddox.randomtask.domain.repository.SubTaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@Suppress("TooGenericExceptionCaught")
class SubTaskRepositoryImpl
    @Inject
    constructor(
        private val subTaskDao: SubTaskDao,
    ) : SubTaskRepository {
        override fun getSubTasksForTask(parentTaskId: Long): Flow<List<SubTask>> =
            subTaskDao.getSubTasksForTask(parentTaskId).map { entities ->
                entities.map { it.toDomain() }
            }.catch { emit(emptyList()) }

        override fun getSubTaskCount(parentTaskId: Long): Flow<Int> = subTaskDao.getSubTaskCount(parentTaskId)

        override fun getCompletedSubTaskCount(parentTaskId: Long): Flow<Int> =
            subTaskDao.getCompletedSubTaskCount(parentTaskId)

        override suspend fun addSubTask(subTask: SubTask): Result<Long> {
            return try {
                val id = subTaskDao.insertSubTask(subTask.toEntity())
                Result.success(id)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        override suspend fun updateSubTask(subTask: SubTask): Result<Unit> {
            return try {
                subTaskDao.updateSubTask(subTask.toEntity())
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        override suspend fun deleteSubTask(subTaskId: Long): Result<Unit> {
            return try {
                subTaskDao.deleteSubTaskById(subTaskId)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
