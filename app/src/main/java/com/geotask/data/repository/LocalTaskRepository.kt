package com.geotask.data.repository

import androidx.lifecycle.LiveData
import com.geotask.data.local.dao.TaskDao
import com.geotask.domain.model.Task
import com.geotask.domain.repository.TaskRepository
import javax.inject.Inject

class LocalTaskRepository @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getAllTasks(): LiveData<List<Task>> = taskDao.getAllTasks()
    override fun getTaskById(id: Long): LiveData<Task?> = taskDao.getTaskById(id)
    override suspend fun insertTask(task: Task) = taskDao.insert(task)
    override suspend fun updateTask(task: Task) = taskDao.update(task)
    override suspend fun deleteTask(task: Task) = taskDao.delete(task)
    override fun getAllActiveTasks(): LiveData<List<Task>> = taskDao.getAllActiveTasks()
    override fun getTasksByLocationSync(locationId: Long): List<Task> {
        return taskDao.getTasksByLocationSync(locationId)
    }
}