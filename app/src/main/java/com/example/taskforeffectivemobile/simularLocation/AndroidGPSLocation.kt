package com.example.taskforeffectivemobile.simularLocation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AndroidGPSLocation(
    private val context: Context
) : Location {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context.applicationContext)

    private val _locationFlow = MutableSharedFlow<Result<Point>>(replay = 1)
    private val callbackScope = CoroutineScope(Dispatchers.IO)

    private var locationCallback: LocationCallback? = null
    private var isActive: Boolean = false

    override fun getLocation(): Flow<Result<Point>> = _locationFlow.asSharedFlow()

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override suspend fun startBackgroundUpdates() {
        if (isActive) return

        if (!hasLocationPermission()) {
            _locationFlow.emit(Result.success(getDefaultLocation()))
            return
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000L
        ).apply {
            setMinUpdateIntervalMillis(5000L)
            setMaxUpdateDelayMillis(15000L)
        }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    Log.d("GPS_TEST", "Координаты: ${location.latitude}, ${location.longitude}")
                    Log.d("GPS_TEST", "Время: ${System.currentTimeMillis()}")
                    callbackScope.launch {
                        _locationFlow.emit(Result.success(location.toPoint()))
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            Looper.getMainLooper()
        )

        isActive = true
    }

    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun android.location.Location.toPoint() = Point(
        latitude = latitude,
        longitude = longitude
    )

    override suspend fun stopBackgroundUpdates() {
        if (!isActive) return

        locationCallback?.let { callback ->
            fusedLocationClient.removeLocationUpdates(callback)
        }
        locationCallback = null
        isActive = false
    }

    private fun getDefaultLocation(): Point = Point(
        longitude = 56.0304125,
        latitude = 54.7280135
    )
}