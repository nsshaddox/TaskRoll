package com.nshaddox.randomtask.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SubTaskDao {
    @Query(
        "SELECT * FROM subtasks WHERE parent_task_id = :parentTaskId " +
            "ORDER BY is_completed ASC, " +
            "CASE WHEN is_completed = 0 THEN created_at END ASC, " +
            "CASE WHEN is_completed = 1 THEN updated_at END DESC",
    )
    fun getSubTasksForTask(parentTaskId: Long): Flow<List<SubTaskEntity>>

    @Query("SELECT * FROM subtasks WHERE id = :id")
    suspend fun getSubTaskById(id: Long): SubTaskEntity?

    @Insert
    suspend fun insertSubTask(subTask: SubTaskEntity): Long

    @Update
    suspend fun updateSubTask(subTask: SubTaskEntity)

    @Query("DELETE FROM subtasks WHERE id = :id")
    suspend fun deleteSubTaskById(id: Long)

    @Query("SELECT COUNT(*) FROM subtasks WHERE parent_task_id = :parentTaskId")
    fun getSubTaskCount(parentTaskId: Long): Flow<Int>

    @Query(
        "SELECT COUNT(*) FROM subtasks WHERE parent_task_id = :parentTaskId AND is_completed = 1",
    )
    fun getCompletedSubTaskCount(parentTaskId: Long): Flow<Int>
}
