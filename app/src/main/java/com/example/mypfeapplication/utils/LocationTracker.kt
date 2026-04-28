package com.example.mypfeapplication.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class LocationTracker(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationCallback: LocationCallback? = null

    private val _currentLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val currentLocation: StateFlow<Pair<Double, Double>?> = _currentLocation

    private val _currentAddress = MutableStateFlow<String>("Fetching location...")
    val currentAddress: StateFlow<String> = _currentAddress

    @SuppressLint("MissingPermission")
    fun startTracking() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            30_000L
        ).apply {
            setMinUpdateIntervalMillis(30_000L)
            setWaitForAccurateLocation(false)
        }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                val lat = location.latitude
                val lng = location.longitude

                _currentLocation.value = Pair(lat, lng)
                Log.d("TRIP_LOCATION", "📍 Lat: $lat | Lng: $lng")

                // اسم الشارع الحقيقي
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(lat, lng, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        val streetName = address.thoroughfare
                            ?: address.subLocality
                            ?: address.locality
                            ?: "Unknown location"
                        _currentAddress.value = streetName
                        Log.d("TRIP_LOCATION", "📍 Address: $streetName")
                    }
                } catch (e: Exception) {
                    Log.e("TRIP_LOCATION", "Geocoder error: ${e.message}")
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            Looper.getMainLooper()
        )

        Log.d("TRIP_LOCATION", "✅ Location tracking started")
    }

    fun stopTracking() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
            Log.d("TRIP_LOCATION", "🛑 Location tracking stopped")
        }
        locationCallback = null
    }
}