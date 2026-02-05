package com.example.weatherapp.data.location

// Unit tests should not depend on Android framework classes.
// GeoPoint is a simple Kotlin-only model, so it safe in JVM tests.
data class GeoPoint(val lat: Double, val lon: Double)
