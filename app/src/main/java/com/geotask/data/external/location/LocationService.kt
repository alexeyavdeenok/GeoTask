package com.geotask.data.external.location

import com.geotask.data.external.adapter.AndroidLocationAdapter
import com.geotask.domain.model.Location

class LocationService(
    private val adapter: AndroidLocationAdapter
) {

    fun mapLocation(androidLocation: android.location.Location): Location {
        return adapter.toDomain(androidLocation)
    }
}