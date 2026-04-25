package com.geotask.domain.location

import com.geotask.domain.model.GeoPoint

class LocationService(
    private val locationProvider: LocationProvider
) {
    // Здесь тоже должен быть suspend
    suspend fun getCurrentLocation(): GeoPoint? {
        return locationProvider.getCurrentLocation()
    }
}