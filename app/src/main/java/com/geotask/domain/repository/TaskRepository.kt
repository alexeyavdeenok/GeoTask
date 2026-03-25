package com.geotask.domain.repository

import androidx.lifecycle.LiveData
import com.geotask.domain.model.Task

interface TaskRepository {
    fun getAllTasks(): LiveData<List<Task>>
    suspend fun insertTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
}