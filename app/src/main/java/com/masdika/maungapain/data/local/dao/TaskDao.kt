package com.masdika.maungapain.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.masdika.maungapain.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    // Read (Order by priority, then date)
    @Query("SELECT * FROM `task-table` ORDER BY priority ASC, createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    // Create
    @Insert(onConflict = REPLACE)
    suspend fun insertTask(task: TaskEntity)

    // Update
    @Update
    suspend fun updateTask(task: TaskEntity)

    // Delete
    @Delete
    suspend fun deleteTask(task: TaskEntity)
}