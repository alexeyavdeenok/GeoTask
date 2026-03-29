// com.geotask.domain.notification/LocationNotificationHandler.kt
package com.geotask.domain.notification

import com.geotask.domain.model.Location
import com.geotask.domain.model.Settings

interface LocationNotificationHandler {

    fun setNext(handler: LocationNotificationHandler): LocationNotificationHandler

    fun handle(
        location: Location,
        distance: Float,
        settings: Settings
    ): Boolean
}