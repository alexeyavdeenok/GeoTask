// data/remote/dto/WeatherDto.kt
package com.geotask.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OpenWeatherResponse(
    val main: MainData,
    val weather: List<WeatherItem>,
    val name: String // название города
)

data class MainData(
    val temp: Double,
    val feels_like: Double,
    val humidity: Int
)

data class WeatherItem(
    val main: String,      // "Clear", "Clouds", "Rain" и т.д.
    val description: String,
    val icon: String       // "01d", "02n" и т.д.
)