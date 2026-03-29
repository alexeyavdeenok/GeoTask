package com.geotask.domain.notification.handlers
import com.geotask.domain.model.Location
import com.geotask.domain.model.Settings
import com.geotask.domain.notification.AbstractLocationNotificationHandler

class LocationDistanceHandler : AbstractLocationNotificationHandler() {
    override fun check(location: Location, distance: Float, settings: Settings): Boolean {
        return distance <= settings.notificationRadiusMeters
    }
}