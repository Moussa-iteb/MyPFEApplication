package com.example.mypfeapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypfeapplication.model.UserTripData
import com.example.mypfeapplication.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyTripsViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _trips = MutableStateFlow<List<UserTripData>>(emptyList())
    val trips: StateFlow<List<UserTripData>> = _trips

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadTrips()
    }

    fun loadTrips() {
        viewModelScope.launch {
            _isLoading.value = true
            _trips.value = repository.getUserTrips()
            _isLoading.value = false
        }
    }
}