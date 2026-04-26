package com.geotask.domain.model

import com.geotask.R

data class Weather(
    val temperature: Int,
    val description: String,
    val iconRes: Int,
    val cityName: String = ""
)

fun mapWeatherCode(code: Int): Weather {
    return when (code) {
        0 -> Weather(0, "Ясно", R.drawable.ic_sunny)
        1, 2, 3 -> Weather(0, "Облачно", R.drawable.ic_sunny) // замени на ic_cloudy
        45, 48 -> Weather(0, "Туман", R.drawable.ic_sunny)    // замени на ic_fog
        51, 53, 55, 61, 63, 65 -> Weather(0, "Дождь", R.drawable.ic_sunny) // ic_rain
        71, 73, 75 -> Weather(0, "Снег", R.drawable.ic_sunny) // ic_snow
        95, 96, 99 -> Weather(0, "Гроза", R.drawable.ic_sunny) // ic_thunder
        else -> Weather(0, "Ясно", R.drawable.ic_sunny)
    }
}