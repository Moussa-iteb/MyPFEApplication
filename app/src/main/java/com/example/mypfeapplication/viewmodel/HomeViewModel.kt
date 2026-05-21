package com.example.mypfeapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypfeapplication.model.BikeData
import com.example.mypfeapplication.model.ScanBikeResponse
import com.example.mypfeapplication.model.ScanTripResponse
import com.example.mypfeapplication.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _hasBike = MutableLiveData<Boolean>(false)
    val hasBike: LiveData<Boolean> = _hasBike

    private val _showHistory = MutableLiveData<Boolean>(false)
    val showHistory: LiveData<Boolean> = _showHistory

    private val _selectedTab = MutableLiveData<Int>(0)
    val selectedTab: LiveData<Int> = _selectedTab

    private val _scanResult = MutableLiveData<ScanBikeResponse?>()
    val scanResult: LiveData<ScanBikeResponse?> = _scanResult

    private val _isScanning = MutableLiveData<Boolean>(false)
    val isScanning: LiveData<Boolean> = _isScanning

    private val _assignedBike = MutableLiveData<BikeData?>()
    val assignedBike: LiveData<BikeData?> = _assignedBike

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> = _isLoading

    // ===== TRIP =====
    private val _scanTripResult = MutableLiveData<ScanTripResponse?>()
    val scanTripResult: LiveData<ScanTripResponse?> = _scanTripResult

    private val _isScanningTrip = MutableLiveData<Boolean>(false)
    val isScanningTrip: LiveData<Boolean> = _isScanningTrip

    private val _hasTrip = MutableLiveData<Boolean>(false)
    val hasTrip: LiveData<Boolean> = _hasTrip

    private val _activeTripUserId = MutableLiveData<Int?>()
    val activeTripUserId: LiveData<Int?> = _activeTripUserId

    init {
        loadUsername()
        checkActiveBike()
    }

    private fun checkActiveBike() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getMyActiveBike()
                if (result?.success == true) {
                    _hasBike.value = true
                    _assignedBike.value = result.data?.bike
                } else {
                    _hasBike.value = false
                }
            } catch (e: Exception) {
                _hasBike.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadUsername() {
        _username.value = repository.getUsername()
        _email.value = repository.getEmail()
    }

    fun onTabSelected(tab: Int) { _selectedTab.value = tab }
    fun onViewHistory() { _showHistory.value = true }
    fun onBackFromHistory() { _showHistory.value = false }
    fun setHasBike(value: Boolean) { _hasBike.value = value }
    fun getUsername(): String = repository.getUsername()
    fun getBikeId(): String = _assignedBike.value?.id?.toString() ?: ""
    fun getBatteryLevel(): Float = _assignedBike.value?.batteryLevel?.toFloat() ?: 0f
    fun getToken(): String? = repository.getToken()

    fun logout() {
        _hasBike.value = false
        _assignedBike.value = null
        _scanResult.value = null
        _scanTripResult.value = null
        _activeTripUserId.value = null
        repository.logout()
    }

    fun onBikeScanned(qrCode: String) {
        viewModelScope.launch {
            _isScanning.value = true
            val result = repository.scanBike(qrCode)
            _scanResult.value = result
            if (result?.success == true) {
                _hasBike.value = true
                _assignedBike.value = result.data?.bike
            }
            _isScanning.value = false
        }
    }

    fun clearScanResult() { _scanResult.value = null }

    // ✅ Une seule fonction onTripScanned
    fun onTripScanned(qrCode: String) {
        viewModelScope.launch {
            _isScanningTrip.value = true
            val bikeId = _assignedBike.value?.id ?: 0
            val result = repository.scanTrip(qrCode, bikeId)
            _scanTripResult.value = result
            if (result?.success == true) {
                _hasTrip.value = true
                _activeTripUserId.value = result.data?.id
                result.data?.id?.let { repository.saveTripUserId(it) }

            }
            _isScanningTrip.value = false
        }
    }

    fun clearScanTripResult() { _scanTripResult.value = null }
    fun cancelTrip(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val success = repository.cancelTrip()
            if (success) {
                _hasTrip.value = false
                _activeTripUserId.value = null
                _hasBike.value = false        // ✅ reset bike aussi
                _assignedBike.value = null    // ✅ retourne à l'interface scan
                onSuccess()
            }
        }
    }
    fun returnBike(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val success = repository.returnBike()
            if (success) {
                _hasBike.value = false
                _assignedBike.value = null
                _hasTrip.value = false
                _activeTripUserId.value = null
                onSuccess()
            }
        }
    }
}