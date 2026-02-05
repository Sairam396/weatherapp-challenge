package com.example.weatherapp.domain.model

data class WeatherInfo(
    val cityDisplayName: String,
    val tempF: Double,
    val feelsLikeF: Double,
    val humidityPct: Int,
    val windMph: Double?,
    val conditionTitle: String,
    val conditionDescription: String,
    val iconUrl: String
)
