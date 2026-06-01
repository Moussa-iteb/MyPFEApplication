package com.example.mypfeapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypfeapplication.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _updateSuccess = MutableLiveData(false)
    val updateSuccess: LiveData<Boolean> = _updateSuccess

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _photoUrl = MutableLiveData<String?>(repository.getPhotoUrl())
    val photoUrl: LiveData<String?> = _photoUrl

    fun getPhone(): String     = repository.getPhone()
    fun getPhotoUrl(): String? = repository.getPhotoUrl()
    fun getUsername(): String  = repository.getUsername()
    fun getEmail(): String     = repository.getEmail()

    fun updateProfile(name: String, email: String, phone: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val success = repository.updateProfile(name, email, phone)
                if (success) {
                    _updateSuccess.value = true
                } else {
                    _error.value = "Failed to update profile"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetState() {
        _updateSuccess.value = false
        _error.value = null
        _isLoading.value = false
    }
}