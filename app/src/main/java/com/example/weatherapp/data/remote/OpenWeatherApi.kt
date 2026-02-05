package com.example.weatherapp.data.remote

import com.example.weatherapp.data.remote.dto.GeoDto
import com.example.weatherapp.data.remote.dto.WeatherDto
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApi {

    /**
     * Built-in Geocoding (recommended path)
     * City -> list of candidates with lat/lon
     */
    @GET("geo/1.0/direct")
    suspend fun geocodeDirect(
        @Query("q") query: String,      // "Austin,US" or "Austin,TX,US"
        @Query("limit") limit: Int = 5
    ): List<GeoDto>

    /**
     * Current weather by lat/lon (not deprecated)
     */
    @GET("data/2.5/weather")
    suspend fun currentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "imperial"
    ): WeatherDto
}
