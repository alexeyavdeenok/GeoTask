package com.geotask.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.geotask.domain.model.Task

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks") fun getAllTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE locationId = :locationId")
    fun getTasksByLocationSync(locationId: Long): List<Task>
    @Insert
    suspend fun insert(task: Task)
    @Update
    suspend fun update(task: Task)
    @Delete
    suspend fun delete(task: Task)
}