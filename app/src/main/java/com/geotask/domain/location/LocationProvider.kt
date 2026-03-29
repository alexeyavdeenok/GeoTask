// com.geotask.domain.location/LocationProvider.kt
package com.geotask.domain.location

import com.geotask.domain.model.Location

interface LocationProvider {
    fun getCurrentLocation(): Location?
}