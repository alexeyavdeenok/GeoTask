package com.geotask.data.repository

import com.geotask.data.remote.api.WeatherApi
import com.geotask.domain.model.Weather
import com.geotask.domain.model.mapOpenWeatherIcon
import com.geotask.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi
) : WeatherRepository {

    override suspend fun getCurrentWeather(lat: Double, lon: Double): Result<Weather> {
        return try {
            val dto = api.getCurrentWeather(lat, lon)
            val weather = mapOpenWeatherIcon(
                iconCode = dto.weather.firstOrNull()?.icon ?: "01d",
                description = dto.weather.firstOrNull()?.description ?: "Ясно"
            ).copy(
                temperature = dto.main.temp.toInt(),
                cityName = dto.name
            )
            Result.success(weather)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}