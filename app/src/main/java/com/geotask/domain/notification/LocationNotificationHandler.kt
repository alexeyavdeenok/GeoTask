package com.geotask.domain.notification
import com.geotask.domain.model.Location
import com.geotask.domain.model.Settings
abstract class LocationNotificationHandler {

    private var next: LocationNotificationHandler? = null

    fun setNext(handler: LocationNotificationHandler): LocationNotificationHandler {
        next = handler
        return handler
    }

    fun handle(
        location: Location,
        distance: Float,
        settings: Settings
    ): Boolean {
        if (!check(location, distance, settings)) return false
        return next?.handle(location, distance, settings) ?: true
    }

    protected abstract fun check(
        location: Location,
        distance: Float,
        settings: Settings
    ): Boolean
}