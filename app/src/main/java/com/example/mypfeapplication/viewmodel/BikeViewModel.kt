package com.example.mypfeapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BikeViewModel : ViewModel() {

    // ✅ État du trip
    private val _tripStarted = MutableLiveData<Boolean>(true)
    val tripStarted: LiveData<Boolean> = _tripStarted

    // ✅ Timer en secondes
    private val _seconds = MutableLiveData<Int>(0)
    val seconds: LiveData<Int> = _seconds

    // ✅ Infos vélo
    private val _bikeId = MutableLiveData<String>("ZE-0815")
    val bikeId: LiveData<String> = _bikeId

    private val _batteryLevel = MutableLiveData<Float>(0.87f)
    val batteryLevel: LiveData<Float> = _batteryLevel

    private val _location = MutableLiveData<String>("Avenue de l'Indépendance, Tunis")
    val location: LiveData<String> = _location

    // ✅ Stats trip
    private val _distance = MutableLiveData<String>("0.0 km")
    val distance: LiveData<String> = _distance

    private val _speed = MutableLiveData<String>("0 km/h")
    val speed: LiveData<String> = _speed

    fun startTrip() {
        _tripStarted.value = true
        _seconds.value = 0
        startTimer()
    }

    fun endTrip() {
        _tripStarted.value = false
        _seconds.value = 0
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (_tripStarted.value == true) {
                delay(1000)
                _seconds.value = (_seconds.value ?: 0) + 1
            }
        }
    }

    // ✅ Formater le timer en HH:MM:SS
    fun getFormattedTime(): String {
        val s = _seconds.value ?: 0
        val h = s / 3600
        val m = (s % 3600) / 60
        val sec = s % 60
        return "%02d:%02d:%02d".format(h, m, sec)
    }
}