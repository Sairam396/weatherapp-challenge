package com.example.weatherapp.data.local

import android.content.Context

class Prefs(context: Context) {
    private val sp = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)

    fun saveLastCity(city: String) {
        sp.edit().putString(KEY_LAST_CITY, city.trim()).apply()
    }

    fun getLastCity(): String? = sp.getString(KEY_LAST_CITY, null)

    companion object {
        private const val KEY_LAST_CITY = "last_city"
    }
}
