package com.geotask.domain.notification.handlers

import com.geotask.domain.notification.LocationNotificationHandler
import com.geotask.domain.model.Location
import com.geotask.domain.model.Settings

class LocationDistanceHandler : LocationNotificationHandler() {
    override fun check(location: Location, distance: Float, settings: Settings): Boolean {
        return distance <= settings.notificationRadiusMeters
    }
}