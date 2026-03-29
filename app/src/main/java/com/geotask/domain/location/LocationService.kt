// com.geotask.domain.location/LocationService.kt  (лучше перенести в domain)
package com.geotask.domain.location

import com.geotask.data.external.adapter.AndroidLocationAdapter
import com.geotask.domain.model.Location

class LocationService(
    private val locationProvider: LocationProvider   // ← инжектим интерфейс!
) {

    fun getCurrentLocation(): Location? {
        return locationProvider.getCurrentLocation()
    }

    // Если нужно конвертировать уже полученную Android Location:
    fun mapLocation(androidLocation: android.location.Location): Location {
        return (locationProvider as? AndroidLocationAdapter)?.toDomain(androidLocation)
            ?: throw IllegalStateException("Unsupported provider")
    }
}