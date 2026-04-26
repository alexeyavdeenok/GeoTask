package com.geotask.data.remote.api

import com.geotask.BuildConfig
import com.geotask.data.remote.dto.OpenWeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "ru",
        @Query("appid") apiKey: String = BuildConfig.OPENWEATHER_API_KEY
    ): OpenWeatherResponse
}