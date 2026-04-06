package com.geotask.domain.location

import com.geotask.domain.model.Location

class LocationService(
    private val locationProvider: LocationProvider
) {

    fun getCurrentLocation(): Location? {
        return locationProvider.getCurrentLocation()
    }
}