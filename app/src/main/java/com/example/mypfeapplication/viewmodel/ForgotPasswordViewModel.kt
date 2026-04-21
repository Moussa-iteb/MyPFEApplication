package com.example.mypfeapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mypfeapplication.repository.UserRepository
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserRepository(application)

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _codeSent = MutableLiveData<Boolean>(false)
    val codeSent: LiveData<Boolean> = _codeSent

    private val _passwordReset = MutableLiveData<Boolean>(false)
    val passwordReset: LiveData<Boolean> = _passwordReset

    fun sendResetCode(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val (success, message) = repository.forgotPassword(email)
                if (success) {
                    _codeSent.value = true
                } else {
                    _error.value = message
                }
            } catch (e: Exception) {
                // إذا timeout لكن جاك code — نعتبروها success
                if (e.message?.contains("timeout", ignoreCase = true) == true) {
                    _codeSent.value = true
                } else {
                    _error.value = e.message ?: "Connection error"
                }
            }
            _isLoading.value = false
        }
    }

    fun resetPassword(email: String, code: String, newPassword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val (success, message) = repository.resetPassword(email, code, newPassword)
            if (success) {
                _passwordReset.value = true
            } else {
                _error.value = message
            }
            _isLoading.value = false
        }
    }

    fun resetState() {
        _codeSent.value = false
        _passwordReset.value = false
        _error.value = null
    }
}