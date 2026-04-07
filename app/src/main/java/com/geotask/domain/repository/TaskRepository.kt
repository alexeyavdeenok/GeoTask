package com.geotask.domain.repository

import androidx.lifecycle.LiveData
import com.geotask.domain.model.Task

interface TaskRepository {
    fun getAllTasks(): LiveData<List<Task>>
    suspend fun insertTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    fun getTasksByLocationSync(locationId: Long): List<Task>
    fun getTaskById(id: Long): LiveData<Task?>

    fun getAllActiveTasks(): LiveData<List<Task>>
}