package com.geotask.domain.notification.handlers

import com.geotask.domain.model.Settings
import com.geotask.domain.model.Location
import com.geotask.domain.notification.LocationNotificationHandler
import com.geotask.domain.repository.TaskRepository

class NotifyLocationHandler(
    private val taskRepository: TaskRepository
) : LocationNotificationHandler() {

    override fun check(location: Location, distance: Float, settings: Settings): Boolean {
        // получаем задачи этой локации
        val tasks = taskRepository.getTasksByLocationSync(location.id)

        if (tasks.isEmpty()) return false

        return true
    }
}