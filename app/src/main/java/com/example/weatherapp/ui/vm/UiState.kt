package com.example.weatherapp.ui.vm

import com.example.weatherapp.domain.model.WeatherInfo

data class UiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val weather: WeatherInfo? = null,
    val error: String? = null
)
