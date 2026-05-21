package com.example.mypfeapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypfeapplication.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BikeViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _tripStarted = MutableLiveData<Boolean>(false)
    val tripStarted: LiveData<Boolean> = _tripStarted

    private val _seconds = MutableLiveData<Int>(0)
    val seconds: LiveData<Int> = _seconds

    private val _tripError = MutableLiveData<String?>(null)
    val tripError: LiveData<String?> = _tripError

    private val _tripEnded = MutableLiveData<Boolean>(false)
    val tripEnded: LiveData<Boolean> = _tripEnded

    private var timerJob: Job? = null
    private var trackingJob: Job? = null

    // Position courante — mise à jour depuis BikeLocationScreen
    private var currentLat: Double = 0.0
    private var currentLng: Double = 0.0

    fun updateLocation(lat: Double, lng: Double) {
        currentLat = lat
        currentLng = lng
        android.util.Log.d("BIKE_VM", "Location updated: $lat, $lng")
    }

    // ✅ startTrip — appelle d'abord l'API, puis démarre timer + tracking
    fun startTrip() {
        viewModelScope.launch {
            android.util.Log.d("BIKE_VM", "Starting trip...")

            // ✅ si tripId manquant → fetch depuis API avant de démarrer
            var tripId = repository.getTripId()
            if (tripId == -1) {
                android.util.Log.d("BIKE_VM", "tripId = -1, fetching active trip from API...")
                repository.fetchAndSaveActiveTrip()
                tripId = repository.getTripId()
            }

            if (tripId == -1) {
                _tripError.value = "No trip found — contact admin"
                android.util.Log.e("BIKE_VM", "startTrip: still no tripId after fetch")
                return@launch
            }

            val success = repository.startTrip()
            android.util.Log.d("BIKE_VM", "startTrip API result: $success")

            _tripStarted.value = true
            _seconds.value = 0
            _tripError.value = null
            startTimer()
            startTracking()
        }
    }

    // ✅ endTrip — arrête timer + tracking, puis appelle l'API
    fun endTrip() {
        // Arrête immédiatement les coroutines
        timerJob?.cancel()
        trackingJob?.cancel()

        _tripStarted.value = false
        _seconds.value = 0
        _tripEnded.value = true

        // ✅ Envoie le dernier point GPS avant de terminer
        viewModelScope.launch {
            if (currentLat != 0.0) {
                repository.sendTrackingPoint(currentLat, currentLng)
                android.util.Log.d("BIKE_VM", "Last tracking point sent: $currentLat, $currentLng")
            }

            // ✅ Appel API PUT /trips/:id/end
            val success = repository.endTrip(currentLat, currentLng)
            android.util.Log.d("BIKE_VM", "endTrip API result: $success")

            if (!success) {
                android.util.Log.e("BIKE_VM", "endTrip API failed")
            }
        }
    }

    fun resetTripEnded() {
        _tripEnded.value = false
    }

    // ✅ Timer — s'arrête proprement avec Job
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_tripStarted.value == true) {
                delay(1000)
                _seconds.value = (_seconds.value ?: 0) + 1
            }
        }
    }

    // ✅ Tracking — envoie position toutes les 30s
    private fun startTracking() {
        trackingJob?.cancel()
        trackingJob = viewModelScope.launch {
            while (_tripStarted.value == true) {
                delay(30_000L)
                if (_tripStarted.value == true) {
                    if (currentLat != 0.0 && currentLng != 0.0) {
                        val sent = repository.sendTrackingPoint(currentLat, currentLng)
                        android.util.Log.d("BIKE_VM", "Tracking point sent: $sent ($currentLat, $currentLng)")
                    } else {
                        android.util.Log.w("BIKE_VM", "Location not available yet, skipping tracking point")
                    }
                }
            }
        }
    }

    fun getFormattedTime(): String {
        val s = _seconds.value ?: 0
        val h = s / 3600
        val m = (s % 3600) / 60
        val sec = s % 60
        return "%02d:%02d:%02d".format(h, m, sec)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        trackingJob?.cancel()
    }
}