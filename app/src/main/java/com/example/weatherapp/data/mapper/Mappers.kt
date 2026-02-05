package com.example.weatherapp.data.mapper

import com.example.weatherapp.data.remote.dto.WeatherDto
import com.example.weatherapp.domain.model.WeatherInfo

fun WeatherDto.toDomain(): WeatherInfo {
    val first = weather.firstOrNull()
    val icon = first?.icon.orEmpty()

    // Icon code like "04n" should map to URL format:
    // https://openweathermap.org/img/wn/04n@2x.png
    val iconUrl = if (icon.isNotBlank()) {
        "https://openweathermap.org/img/wn/$icon@2x.png"
    } else ""

    return WeatherInfo(
        cityDisplayName = name,
        tempF = main.temp,
        feelsLikeF = main.feelsLike,
        humidityPct = main.humidity,
        windMph = wind?.speed,
        conditionTitle = first?.main ?: "Unknown",
        conditionDescription = first?.description ?: "No description",
        iconUrl = iconUrl
    )
}
