package com.geotask.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OpenMeteoResponse(
    @SerializedName("current_weather") val currentWeather: CurrentWeatherDto
)

data class CurrentWeatherDto(
    val temperature: Double,
    @SerializedName("weathercode") val weatherCode: Int,
    val windspeed: Double
)