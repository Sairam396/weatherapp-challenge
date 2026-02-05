package com.example.weatherapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GeoDto(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String? = null
)
