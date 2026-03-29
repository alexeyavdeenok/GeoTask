package com.geotask.domain.notification
import com.geotask.domain.model.Location
import com.geotask.domain.model.Settings
abstract class AbstractLocationNotificationHandler : LocationNotificationHandler{

    private var next: LocationNotificationHandler? = null

    override fun setNext(handler: LocationNotificationHandler): LocationNotificationHandler {
        next = handler
        return handler
    }

    override fun handle(
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