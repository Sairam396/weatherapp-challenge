package com.example.weatherapp

import com.example.weatherapp.data.location.GeoPoint
import com.example.weatherapp.ui.vm.WeatherViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchByCity success updates weather`() = runTest {
        val repo = FakeWeatherRepository()

        // For city search test, I don't need location at all so pass null.
        val locationProvider = FakeLocationProvider(point = null)

        val vm = WeatherViewModel(repo, locationProvider)

        vm.onQueryChange("Austin")
        vm.fetchByCity()
        dispatcher.scheduler.advanceUntilIdle()

        assertNotNull(vm.state.value.weather)
        assertEquals("Austin", vm.state.value.weather?.cityDisplayName)
        assertNull(vm.state.value.error)
        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun `fetchByLocation success updates weather`() = runTest {
        val repo = FakeWeatherRepository()

        val locationProvider = FakeLocationProvider(point = GeoPoint(30.0, -97.0))

        val vm = WeatherViewModel(repo, locationProvider)

        vm.fetchByLocationIfPermitted(hasLocationPermission = true, onNeedPermission = {})
        dispatcher.scheduler.advanceUntilIdle()

        assertNotNull(vm.state.value.weather)
        assertEquals("FromLocation", vm.state.value.weather?.cityDisplayName)
    }

    @Test
    fun `fetchByCity error sets error`() = runTest {
        val repo = FakeWeatherRepository().apply { shouldThrow = true }

        val locationProvider = FakeLocationProvider(point = null)

        val vm = WeatherViewModel(repo, locationProvider)

        vm.onQueryChange("X")
        vm.fetchByCity()
        dispatcher.scheduler.advanceUntilIdle()

        assertNull(vm.state.value.weather)
        assertNotNull(vm.state.value.error)
    }
}
