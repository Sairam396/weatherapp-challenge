package com.example.weatherapp.data.repo

import com.example.weatherapp.data.local.Prefs
import com.example.weatherapp.data.mapper.toDomain
import com.example.weatherapp.data.remote.OpenWeatherApi
import com.example.weatherapp.domain.model.WeatherInfo
import com.example.weatherapp.util.AppLogger
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: OpenWeatherApi,
    private val prefs: Prefs
) : WeatherRepository {

    override suspend fun getWeatherByUsCity(city: String): WeatherInfo {
        val cleaned = city.trim()
        require(cleaned.isNotBlank()) { "City cannot be empty." }

        // Requirement says "enter a US city".
        // I keep query US-scoped to avoid picking same city in other countries.
        val query = "$cleaned,US"
        AppLogger.i("Repo: Geocoding city=\"$cleaned\" -> query=\"$query\"")

        val results = api.geocodeDirect(query = query, limit = 5)
        AppLogger.i("Repo: Geocode results size=${results.size}")

        val best = results.firstOrNull()
            ?: throw IllegalArgumentException("No results found for \"$cleaned\". Try: \"City, State\" like \"Austin, TX\".")

        AppLogger.i("Repo: Picked: ${best.name}, state=${best.state}, country=${best.country}, lat=${best.lat}, lon=${best.lon}")

        // Save last searched city for app launch auto-load.
        saveLastCity(cleaned)
        AppLogger.i("Repo: Saved last city=\"$cleaned\"")

        // Fetch weather (imperial to show °F and mph)
        val dto = api.currentWeather(lat = best.lat, lon = best.lon, units = "imperial")
        AppLogger.i("Repo: Weather success city=${dto.name}, tempF=${dto.main.temp}, icon=${dto.weather.firstOrNull()?.icon}")

        return dto.toDomain()
    }

    override suspend fun getWeatherByLatLon(lat: Double, lon: Double): WeatherInfo {
        AppLogger.i("Repo: Weather by lat/lon -> lat=$lat lon=$lon")
        val dto = api.currentWeather(lat = lat, lon = lon, units = "imperial")
        AppLogger.i("Repo: Location weather success city=${dto.name}, tempF=${dto.main.temp}")
        return dto.toDomain()
    }

    override fun saveLastCity(city: String) = prefs.saveLastCity(city)
    override fun getLastCity(): String? = prefs.getLastCity()
}
