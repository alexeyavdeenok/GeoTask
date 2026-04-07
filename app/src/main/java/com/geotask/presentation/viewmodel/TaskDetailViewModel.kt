package com.geotask.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geotask.domain.model.Task
import com.geotask.domain.repository.TaskRepository
import com.geotask.domain.usecase.task.CompleteTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val completeTaskUseCase: CompleteTaskUseCase
) : ViewModel() {

    private val _task = MutableLiveData<Task?>()
    val task: LiveData<Task?> = _task

    fun loadTask(taskId: Long) {
        if (taskId == -1L) return

        repository.getTaskById(taskId).observeForever { loadedTask ->
            _task.value = loadedTask
        }
    }

    fun completeTask() {                     // ← новая функция
        task.value?.let {
            viewModelScope.launch {
                completeTaskUseCase(it)
            }
        }
    }

    fun updateTask(title: String, description: String?) {
        task.value?.let { currentTask ->
            val updatedTask = currentTask.copy(
                title = title.trim(),
                description = description?.trim()
            )
            viewModelScope.launch {
                repository.updateTask(updatedTask)
            }
        }
    }

    fun deleteTask() {
        task.value?.let { currentTask ->
            viewModelScope.launch {
                repository.deleteTask(currentTask)
            }
        }
    }
}