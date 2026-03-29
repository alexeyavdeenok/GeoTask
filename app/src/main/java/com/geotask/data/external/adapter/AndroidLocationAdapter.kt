// com.geotask.data.external.adapter/AndroidLocationAdapter.kt
package com.geotask.data.external.adapter

import android.location.Location as AndroidLocation
import com.geotask.domain.location.LocationProvider
import com.geotask.domain.model.Location

class AndroidLocationAdapter : LocationProvider {

    override fun getCurrentLocation(): Location? {
        // Здесь в реальном коде будет получение актуальной локации через FusedLocationProvider и т.д.
        // Пока оставляем заглушку или принимаем локацию как параметр
        return null // или реализация получения текущей локации
    }

    // Вспомогательный метод для конвертации (можно оставить)
    fun toDomain(androidLocation: AndroidLocation): Location {
        return Location(
            id = -1L,
            name = "Current Location",
            latitude = androidLocation.latitude,
            longitude = androidLocation.longitude
        )
    }
}