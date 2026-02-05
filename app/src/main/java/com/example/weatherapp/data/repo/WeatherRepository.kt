package com.example.weatherapp.data.repo

import com.example.weatherapp.domain.model.WeatherInfo

interface WeatherRepository {
    suspend fun getWeatherByUsCity(city: String): WeatherInfo
    suspend fun getWeatherByLatLon(lat: Double, lon: Double): WeatherInfo
    fun saveLastCity(city: String)
    fun getLastCity(): String?
}
