package com.geotask.data.local.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.geotask.data.security.EncryptionManager
import com.geotask.domain.model.Task

/**
 * Обёртка для TaskDao с автоматическим шифрованием/расшифровкой
 * Шифрует чувствительные поля перед сохранением в БД
 * и расшифровывает при чтении
 */
class SecureTaskDao(
    private val delegate: TaskDao,
    private val encryptionManager: EncryptionManager
) : TaskDao {

    override fun getAllTasks(): LiveData<List<Task>> {
        return delegate.getAllTasks().map { tasks ->
            tasks.map { task -> decryptTask(task) }
        }
    }

    override fun getTasksByLocationSync(locationId: Long): List<Task> {
        return delegate.getTasksByLocationSync(locationId).map { task ->
            decryptTask(task)
        }
    }

    override fun getTaskById(id: Long): LiveData<Task?> {
        return delegate.getTaskById(id).map { task ->
            task?.let { decryptTask(it) }
        }
    }

    override fun getAllActiveTasks(): LiveData<List<Task>> {
        return delegate.getAllActiveTasks().map { tasks ->
            tasks.map { task -> decryptTask(task) }
        }
    }

    override suspend fun insert(task: Task) {
        val encryptedTask = encryptTask(task)
        delegate.insert(encryptedTask)
    }

    override suspend fun update(task: Task) {
        val encryptedTask = encryptTask(task)
        delegate.update(encryptedTask)
    }

    override suspend fun delete(task: Task) {
        // ID не шифруется, но для консистентности
        val encryptedTask = encryptTask(task)
        delegate.delete(encryptedTask)
    }

    /**
     * Шифрует чувствительные поля Task перед сохранением в БД
     */
    private fun encryptTask(task: Task): Task {
        return task.copy(
            title = encryptionManager.encrypt(task.title),
            description = task.description?.let { 
                encryptionManager.encrypt(it) 
            }
        )
    }

    /**
     * Расшифровывает чувствительные поля Task после чтения из БД
     */
    private fun decryptTask(task: Task): Task {
        return try {
            task.copy(
                title = encryptionManager.decrypt(task.title),
                description = task.description?.let { 
                    encryptionManager.decrypt(it) 
                }
            )
        } catch (e: Exception) {
            // Если расшифровка не удалась, возвращаем оригинальный объект
            // (может быть данные старые, без шифрования)
            task
        }
    }
}
