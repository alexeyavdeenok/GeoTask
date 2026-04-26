package com.geotask.domain.repository

import com.geotask.domain.model.Weather

interface WeatherRepository {
    suspend fun getCurrentWeather(lat: Double, lon: Double): Result<Weather>
}