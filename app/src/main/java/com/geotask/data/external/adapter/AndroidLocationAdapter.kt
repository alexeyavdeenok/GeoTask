package com.geotask.data.external.adapter

import android.location.Location as AndroidLocation
import com.geotask.domain.model.Location

class AndroidLocationAdapter {

    fun toDomain(androidLocation: AndroidLocation): Location {
        return Location(
            id = -1, // временная локация
            name = "Current",
            latitude = androidLocation.latitude,
            longitude = androidLocation.longitude
        )
    }
}