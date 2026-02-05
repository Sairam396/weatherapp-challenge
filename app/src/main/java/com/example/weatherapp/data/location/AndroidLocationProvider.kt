package com.example.weatherapp.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import com.example.weatherapp.util.AppLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidLocationProvider(private val context: Context) : LocationProvider {

    @SuppressLint("MissingPermission")
    override suspend fun getLastKnownLocation(): GeoPoint? = withContext(Dispatchers.IO) {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val gps = runCatching { lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) }
            .onFailure { AppLogger.w("GPS lastKnownLocation failed: ${it.message}") }
            .getOrNull()

        val net = runCatching { lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) }
            .onFailure { AppLogger.w("Network lastKnownLocation failed: ${it.message}") }
            .getOrNull()

        val best = when {
            gps == null -> net
            net == null -> gps
            else -> if (gps.time >= net.time) gps else net
        }

        val point = best?.let { GeoPoint(it.latitude, it.longitude) }

        AppLogger.i("LocationProvider: best=${point?.let { "${it.lat},${it.lon}" }}")
        point
    }
}
