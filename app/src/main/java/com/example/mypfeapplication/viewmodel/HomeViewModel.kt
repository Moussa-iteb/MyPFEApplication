package com.example.mypfeapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypfeapplication.model.BikeData
import com.example.mypfeapplication.model.ScanBikeResponse
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

    init {
        loadUsername()
        checkActiveBike()
    }

    private fun checkActiveBike() {
        viewModelScope.launch {
            val result = repository.getMyActiveBike()
            if (result?.success == true) {
                _hasBike.value = true
                _assignedBike.value = result.data?.bike
            }
        }
    }

    private fun loadUsername() {
        _username.value = repository.getUsername()
        _email.value = repository.getEmail()
        _hasBike.value = false
    }

    fun onTabSelected(tab: Int) {
        _selectedTab.value = tab
    }

    fun onViewHistory() {
        _showHistory.value = true
    }

    fun onBackFromHistory() {
        _showHistory.value = false
    }

    fun setHasBike(value: Boolean) {
        _hasBike.value = value
    }

    fun getUsername(): String = repository.getUsername()
    fun getBikeId(): String = _assignedBike.value?.id?.toString() ?: ""
    fun getBatteryLevel(): Float = _assignedBike.value?.batteryLevel?.toFloat() ?: 0f
    fun getToken(): String? = repository.getToken()

    fun logout() {
        _hasBike.value = false
        _assignedBike.value = null
        _scanResult.value = null
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

    fun clearScanResult() {
        _scanResult.value = null
    }

    fun returnBike(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val success = repository.returnBike()
            if (success) {
                _hasBike.value = false
                _assignedBike.value = null
                onSuccess()
            }
        }
    }
}