package com.example.mypfeapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mypfeapplication.model.AuthResponse
import com.example.mypfeapplication.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserRepository(application)

    private val _authResponse = MutableLiveData<AuthResponse?>()
    val authResponse: LiveData<AuthResponse?> = _authResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _adminError = MutableLiveData<Boolean>(false)
    val adminError: LiveData<Boolean> = _adminError

    fun resetAuthResponse() {
        _authResponse.value = null
        _adminError.value = false
        _error.value = null
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.login(email, password)
            if (result != null && result.success) {
                val role = result.data?.user?.role
                if (role == "admin") {
                    _adminError.value = true
                } else {
                    result.data?.token?.let { repository.saveToken(it) }
                    result.data?.user?.role?.let { repository.saveRole(it) }
                    result.data?.user?.username?.let { repository.saveUsername(it) }
                    _authResponse.value = result
                }
            } else {
                _error.value = result?.message ?: "Email ou mot de passe invalide"
            }
            _isLoading.value = false
        }
    }

    fun showAdminError() {
        _adminError.value = true
    }

    fun clearAdminError() {
        _adminError.value = false
    }
}