package com.geotask.domain.usecase.task

import androidx.lifecycle.LiveData
import com.geotask.domain.model.Task
import com.geotask.domain.repository.TaskRepository
import javax.inject.Inject

class GetTaskByIdUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke(id: Long): LiveData<Task?> = repository.getTaskById(id)
}