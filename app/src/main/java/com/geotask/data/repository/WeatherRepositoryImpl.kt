package com.geotask.data.repository

import android.util.Log
import com.geotask.data.remote.api.WeatherApi
import com.geotask.domain.model.Weather
import com.geotask.domain.model.mapWeatherCode
import com.geotask.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi
) : WeatherRepository {

    override suspend fun getCurrentWeather(lat: Double, lon: Double): Result<Weather> {
        return try {
            val dto = api.getCurrentWeather(lat, lon)
            val weather = mapWeatherCode(dto.currentWeather.weatherCode).copy(
                temperature = dto.currentWeather.temperature.toInt()
            )
            Log.d("WeatherRepository", "Loaded: ${weather.temperature}° ${weather.description}")
            Result.success(weather)
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Error: ${e.javaClass.simpleName}: ${e.message}")
            Result.failure(e)
        }
    }
}