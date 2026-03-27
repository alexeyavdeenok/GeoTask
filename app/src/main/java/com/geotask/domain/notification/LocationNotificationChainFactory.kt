package com.geotask.domain.notification

import com.geotask.domain.notification.handlers.LocationDistanceHandler
import com.geotask.domain.notification.handlers.NotificationEnabledHandler
import com.geotask.domain.notification.handlers.NotifyLocationHandler
import com.geotask.domain.repository.TaskRepository


object LocationNotificationChainFactory {

    fun create(taskRepository: TaskRepository): LocationNotificationHandler {
        val enabled = NotificationEnabledHandler()
        val distance = LocationDistanceHandler()
        val notify = NotifyLocationHandler(taskRepository)

        enabled.setNext(distance)
            .setNext(notify)

        return enabled
    }
}