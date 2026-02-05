package com.example.weatherapp.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.location.LocationProvider
import com.example.weatherapp.data.repo.WeatherRepository
import com.example.weatherapp.util.AppLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repo: WeatherRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {

    var state = androidx.compose.runtime.mutableStateOf(UiState())
        private set

    fun onQueryChange(newValue: String) {
        AppLogger.d("VM: query changed -> \"$newValue\"")
        state.value = state.value.copy(query = newValue, error = null)
    }

    // Requirement: "Auto-load the last city searched upon app launch."
    fun loadLastCityIfAny() {
        val last = repo.getLastCity()
        AppLogger.i("VM: loadLastCityIfAny last=\"$last\"")

        if (!last.isNullOrBlank()) {
            state.value = state.value.copy(query = last)
            fetchByCity()
        }
    }

    fun fetchByCity() {
        val city = state.value.query
        AppLogger.i("VM: fetchByCity query=\"$city\"")

        viewModelScope.launch {
            state.value = state.value.copy(isLoading = true, error = null, weather = null)
            try {
                val weather = repo.getWeatherByUsCity(city)
                AppLogger.i("VM: fetchByCity SUCCESS city=${weather.cityDisplayName} temp=${weather.tempF}")
                state.value = state.value.copy(isLoading = false, weather = weather)
            } catch (t: Throwable) {
                AppLogger.e("VM: fetchByCity FAILED msg=${t.message}", t)
                state.value = state.value.copy(isLoading = false, error = t.message ?: "Something went wrong.")
            }
        }
    }

    // Requirement: "Ask the User for location access... if granted retrieve weather data by default"
    fun fetchByLocationIfPermitted(
        hasLocationPermission: Boolean,
        onNeedPermission: () -> Unit
    ) {
        AppLogger.i("VM: fetchByLocationIfPermitted hasPerm=$hasLocationPermission")

        if (!hasLocationPermission) {
            AppLogger.w("VM: No location permission -> request permission + fallback to last city")
            onNeedPermission()
            loadLastCityIfAny()
            return
        }

        viewModelScope.launch {
            state.value = state.value.copy(isLoading = true, error = null, weather = null)
            try {
                val loc = locationProvider.getLastKnownLocation()
                if (loc == null) {
                    AppLogger.w("VM: Location is null -> fallback to last city")
                    state.value = state.value.copy(isLoading = false, error = "Could not get location. Loading last searched city.")
                    loadLastCityIfAny()
                    return@launch
                }

                AppLogger.i("VM: Got location lat=${loc.lat} lon=${loc.lon}")
                val weather = repo.getWeatherByLatLon(loc.lat, loc.lon)
                AppLogger.i("VM: Location weather SUCCESS city=${weather.cityDisplayName}")
                state.value = state.value.copy(isLoading = false, weather = weather)
            } catch (t: Throwable) {
                AppLogger.e("VM: Location weather FAILED msg=${t.message}", t)
                state.value = state.value.copy(isLoading = false, error = t.message ?: "Location weather failed.")
                loadLastCityIfAny()
            }
        }
    }
}
