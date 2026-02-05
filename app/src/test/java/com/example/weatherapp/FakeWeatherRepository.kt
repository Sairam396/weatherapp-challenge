package com.example.weatherapp

import com.example.weatherapp.data.repo.WeatherRepository
import com.example.weatherapp.domain.model.WeatherInfo

class FakeWeatherRepository : WeatherRepository {

    // If I name this property "lastCity", Kotlin creates getLastCity().
    // But my interface already has getLastCity(), so it clashes.
    // So I keep a private backing field with a different name.
    private var _lastCity: String? = null

    var shouldThrow = false

    override suspend fun getWeatherByUsCity(city: String): WeatherInfo {
        if (shouldThrow) throw IllegalArgumentException("Fake error")
        saveLastCity(city)

        return WeatherInfo(
            cityDisplayName = city,
            tempF = 70.0,
            feelsLikeF = 68.0,
            humidityPct = 40,
            windMph = 5.0,
            conditionTitle = "Clear",
            conditionDescription = "clear sky",
            iconUrl = "https://openweathermap.org/img/wn/01d@2x.png"
        )
    }

    override suspend fun getWeatherByLatLon(lat: Double, lon: Double): WeatherInfo {
        if (shouldThrow) throw IllegalStateException("Fake location error")
        return WeatherInfo(
            cityDisplayName = "FromLocation",
            tempF = 60.0,
            feelsLikeF = 59.0,
            humidityPct = 50,
            windMph = null,
            conditionTitle = "Clouds",
            conditionDescription = "few clouds",
            iconUrl = ""
        )
    }

    override fun saveLastCity(city: String) {
        _lastCity = city
    }

    override fun getLastCity(): String? = _lastCity
}
