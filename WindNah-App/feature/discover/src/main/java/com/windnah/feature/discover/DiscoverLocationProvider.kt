package com.windnah.feature.discover

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker

internal class DiscoverLocationProvider(
    private val context: Context,
) {
    fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PermissionChecker.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    fun requestCurrentLocation(
        onResolved: (latitude: Double, longitude: Double) -> Unit,
        onUnavailable: () -> Unit,
    ) {
        if (!hasLocationPermission()) {
            onUnavailable()
            return
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (locationManager == null) {
            onUnavailable()
            return
        }

        val provider = when {
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            else -> null
        }

        if (provider == null) {
            bestLastKnownLocation(locationManager)?.let { location ->
                onResolved(location.latitude, location.longitude)
            } ?: onUnavailable()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            locationManager.getCurrentLocation(
                provider,
                null,
                ContextCompat.getMainExecutor(context),
            ) { location ->
                when {
                    location != null -> onResolved(location.latitude, location.longitude)
                    else -> {
                        bestLastKnownLocation(locationManager)?.let { fallback ->
                            onResolved(fallback.latitude, fallback.longitude)
                        } ?: onUnavailable()
                    }
                }
            }
        } else {
            bestLastKnownLocation(locationManager)?.let { fallback ->
                onResolved(fallback.latitude, fallback.longitude)
            } ?: onUnavailable()
        }
    }

    @SuppressLint("MissingPermission")
    private fun bestLastKnownLocation(locationManager: LocationManager): android.location.Location? =
        listOf(
            LocationManager.NETWORK_PROVIDER,
            LocationManager.GPS_PROVIDER,
            LocationManager.PASSIVE_PROVIDER,
        )
            .mapNotNull { provider ->
                runCatching { locationManager.getLastKnownLocation(provider) }.getOrNull()
            }
            .maxByOrNull { location -> location.time }
}
