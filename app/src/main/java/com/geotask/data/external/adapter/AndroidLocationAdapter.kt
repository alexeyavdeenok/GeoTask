package com.geotask.data.external.adapter

import android.location.Location as AndroidLocation
import com.geotask.domain.location.LocationProvider
import com.geotask.domain.model.Location

class AndroidLocationAdapter : LocationProvider {

    override fun getCurrentLocation(): Location? {
        // TODO: реализовать получение локации через Android API
        return null
    }

    fun toDomain(androidLocation: AndroidLocation): Location {
        return Location(
            id = -1L,
            name = "Current Location",
            latitude = androidLocation.latitude,
            longitude = androidLocation.longitude
        )
    }
}