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

    private val _updateSuccess = MutableLiveData<Boolean>(false)
    val updateSuccess: LiveData<Boolean> = _updateSuccess

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun getUsername(): String = repository.getUsername()
    fun getPhone(): String = repository.getPhone()
    fun updateProfile(username: String, email: String, phone: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.updateProfile(username, email, phone)
            if (success) _updateSuccess.value = true
            else _error.value = "Update failed"
            _isLoading.value = false
        }
    }

    fun resetState() {
        _updateSuccess.value = false
        _error.value = null
    }
}