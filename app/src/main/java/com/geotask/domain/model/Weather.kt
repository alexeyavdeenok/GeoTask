package com.geotask.domain.model

import com.geotask.R

data class Weather(
    val temperature: Int,
    val description: String,
    val iconRes: Int,
    val cityName: String = ""
)

fun mapOpenWeatherIcon(iconCode: String, description: String): Weather {
    val iconRes = when (iconCode) {
        "01d", "01n" -> R.drawable.ic_sunny
        "02d", "02n" -> R.drawable.ic_sunny      // замени на ic_cloudy когда будет
        "03d", "03n", "04d", "04n" -> R.drawable.ic_cloudy // замени на ic_cloudy
        "09d", "09n", "10d", "10n" -> R.drawable.ic_rainy// замени на ic_rain
        "11d", "11n" -> R.drawable.ic_sunny      // замени на ic_thunder
        "13d", "13n" -> R.drawable.ic_sunny      // замени на ic_snow
        "50d", "50n" -> R.drawable.ic_sunny      // замени на ic_fog
        else -> R.drawable.ic_sunny
    }

    return Weather(
        temperature = 0,
        description = description.replaceFirstChar { it.uppercase() },
        iconRes = iconRes
    )
}