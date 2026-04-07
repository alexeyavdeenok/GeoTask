package com.geotask.domain.usecase.task

import com.geotask.domain.model.Status
import com.geotask.domain.model.Task
import com.geotask.domain.repository.TaskRepository
import javax.inject.Inject

// domain/usecase/task/CompleteTaskUseCase.kt
class CompleteTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task) {
        val completedTask = task.copy(status = Status.COMPLETED)
        repository.updateTask(completedTask)
    }
}