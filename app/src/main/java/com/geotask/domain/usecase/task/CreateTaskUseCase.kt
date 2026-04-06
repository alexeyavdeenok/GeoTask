package com.geotask.domain.usecase.task

import com.geotask.domain.model.Task
import com.geotask.domain.repository.TaskRepository
import javax.inject.Inject

class CreateTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {

    suspend operator fun invoke(
        title: String,
        description: String?,
        locationId: Long?,
        deadline: Long?
    ) {
        val task = Task.Builder(title)
            .description(description)
            .locationId(locationId)
            .deadline(deadline)
            .build()

        repository.insertTask(task)
    }
}