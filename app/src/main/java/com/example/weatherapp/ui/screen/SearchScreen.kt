package com.example.weatherapp.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.weatherapp.ui.vm.WeatherViewModel
import com.example.weatherapp.util.AppLogger
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(vm: WeatherViewModel) {
    val state = vm.state.value
    val context = LocalContext.current

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        AppLogger.i("UI: permission result = $result")
        vm.fetchByLocationIfPermitted(hasLocationPermission(), onNeedPermission = {})
    }

    // On launch:
    // 1) If permission already granted -> load location weather by default (requirement)
    // 2) Else -> auto-load last city (requirement)
    LaunchedEffect(Unit) {
        val granted = hasLocationPermission()
        AppLogger.i("UI: launch hasLocationPermission=$granted")

        if (granted) {
            vm.fetchByLocationIfPermitted(true) {}
        } else {
            vm.loadLastCityIfAny()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Weather") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = vm::onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Enter US city (ex: Austin or Austin, TX)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        AppLogger.i("UI: keyboard search pressed")
                        vm.fetchByCity()
                    }
                )
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        AppLogger.i("UI: Search clicked")
                        vm.fetchByCity()
                    },
                    enabled = !state.isLoading
                ) { Text("Search") }

                OutlinedButton(
                    onClick = {
                        AppLogger.i("UI: Use Location clicked")
                        vm.fetchByLocationIfPermitted(
                            hasLocationPermission = hasLocationPermission(),
                            onNeedPermission = {
                                AppLogger.i("UI: requesting permission now")
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        )
                    },
                    enabled = !state.isLoading
                ) { Text("Use Location") }
            }

            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            state.weather?.let { w ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(w.cityDisplayName, style = MaterialTheme.typography.titleLarge)

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Icon caching requirement is automatically satisfied by Coil.
                            // Coil caches images (memory + disk) without extra work.
                            if (w.iconUrl.isNotBlank()) {
                                AsyncImage(
                                    model = w.iconUrl,
                                    contentDescription = w.conditionDescription,
                                    modifier = Modifier.size(64.dp)
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("${w.conditionTitle} • ${w.conditionDescription}")
                                Text("Temp: ${w.tempF.roundToInt()}°F")
                                Text("Feels like: ${w.feelsLikeF.roundToInt()}°F")
                                Text("Humidity: ${w.humidityPct}%")
                                Text("Wind: ${w.windMph?.let { "${it} mph" } ?: "N/A"}")
                            }
                        }
                    }
                }
            }
        }
    }
}
