package com.geotask.domain.notification.handlers
import com.geotask.domain.model.Location
import com.geotask.domain.model.Settings
import com.geotask.domain.notification.AbstractLocationNotificationHandler

class NotificationEnabledHandler : AbstractLocationNotificationHandler() {
    override fun check(location: Location, distance: Float, settings: Settings): Boolean {
        return settings.notificationsEnabled
    }
}