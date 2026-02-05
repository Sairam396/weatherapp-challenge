package com.example.weatherapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherDto(
    val name: String,
    val weather: List<WeatherItemDto>,
    val main: MainDto,
    val wind: WindDto? = null,
    val dt: Long
)

@Serializable
data class WeatherItemDto(
    val main: String,
    val description: String,
    val icon: String
)

@Serializable
data class MainDto(
    val temp: Double,

    // SerialName keeps both correct.
    @SerialName("feels_like") val feelsLike: Double,

    val humidity: Int
)

@Serializable
data class WindDto(
    val speed: Double? = null
)
