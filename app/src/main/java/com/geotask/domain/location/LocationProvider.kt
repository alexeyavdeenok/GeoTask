// com.geotask.domain.location/LocationProvider.kt
package com.geotask.domain.location

import com.geotask.domain.model.GeoPoint

interface LocationProvider {
    suspend fun getCurrentLocation(): GeoPoint?
}