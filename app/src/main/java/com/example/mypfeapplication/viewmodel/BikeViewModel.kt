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
import android.content.Context
import androidx.work.*
import com.example.mypfeapplication.database.AppDatabase
import com.example.mypfeapplication.database.TrackingPointEntity
import com.example.mypfeapplication.workers.SyncWorker
import androidx.lifecycle.AndroidViewModel
import android.app.Application
@HiltViewModel
class BikeViewModel @Inject constructor(
    private val repository: UserRepository,
    application: android.app.Application
) : AndroidViewModel(application) {

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
        timerJob?.cancel()
        trackingJob?.cancel()

        _tripStarted.value = false
        _seconds.value = 0
        _tripEnded.value = true

        // ← احفظ فوراً قبل أي coroutine — synchronous
        repository.savePendingEndTrip(currentLat, currentLng)

        // شغل SyncWorker — يعمل حتى لو الـ process مات
        scheduleSyncWork()

        // حاول مباشرة في background
        viewModelScope.launch {
            if (currentLat != 0.0) {
                repository.sendTrackingPoint(currentLat, currentLng)
            }
            val success = repository.endTrip(currentLat, currentLng)
            android.util.Log.d("BIKE_VM", "endTrip direct result: $success")
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


    private fun startTracking() {
        trackingJob?.cancel()
        trackingJob = viewModelScope.launch {
            val db  = AppDatabase.getInstance(getApplication())
            val dao = db.trackingPointDao()

            while (_tripStarted.value == true) {
                delay(30_000L)
                if (_tripStarted.value == true && currentLat != 0.0 && currentLng != 0.0) {

                    // تحقق من الـ internet
                    val hasInternet = isInternetAvailable()

                    if (hasInternet) {
                        // أرسل مباشرة للـ API
                        val sent = repository.sendTrackingPoint(currentLat, currentLng)
                        android.util.Log.d("BIKE_VM", "Online — point sent: $sent")

                        // sync الـ points القديمة إذا موجودة
                        scheduleSyncWork()
                    } else {
                        // احفظ محلياً في Room
                        val tripUserId = repository.getTripUserId()
                        if (tripUserId != -1) {
                            dao.insert(
                                TrackingPointEntity(
                                    tripUserId = tripUserId,
                                    latitude   = currentLat,
                                    longitude  = currentLng
                                )
                            )
                            android.util.Log.d("BIKE_VM", "Offline — point saved locally")
                        }
                    }
                }
            }
        }
    }

    private fun isInternetAvailable(): Boolean {
        val cm = getApplication<android.app.Application>()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps    = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun scheduleSyncWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(getApplication())  // ← بدّل this بـ getApplication()
            .enqueueUniqueWork(
                "sync_on_network",
                ExistingWorkPolicy.REPLACE,
                syncRequest
            )
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