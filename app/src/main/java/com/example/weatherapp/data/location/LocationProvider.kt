package com.example.weatherapp.data.location

interface LocationProvider {
    suspend fun getLastKnownLocation(): GeoPoint?
}
