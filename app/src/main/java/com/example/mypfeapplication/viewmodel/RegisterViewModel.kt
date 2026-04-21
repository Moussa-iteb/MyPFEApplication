package com.example.mypfeapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mypfeapplication.model.AuthResponse
import com.example.mypfeapplication.repository.UserRepository
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserRepository(application)

    private val _authResponse = MutableLiveData<AuthResponse?>()
    val authResponse: LiveData<AuthResponse?> = _authResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun register(
        username: String,
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.register(username, email, password)
            if (result != null) {
                _authResponse.value = result
            } else {
                _error.value = "Inscription échouée. Veuillez réessayer."
            }
            _isLoading.value = false
        }
    }
}