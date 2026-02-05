package com.example.weatherapp

import com.example.weatherapp.data.location.GeoPoint
import com.example.weatherapp.data.location.LocationProvider

class FakeLocationProvider(private val point: GeoPoint?) : LocationProvider {
    override suspend fun getLastKnownLocation(): GeoPoint? = point
}
