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
class ForgotPasswordViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    var savedEmail = ""
    var savedCode = ""

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _codeSent = MutableLiveData<Boolean>(false)
    val codeSent: LiveData<Boolean> = _codeSent

    private val _passwordReset = MutableLiveData<Boolean>(false)
    val passwordReset: LiveData<Boolean> = _passwordReset

    fun sendResetCode(email: String) {
        savedEmail = email
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val (success, code) = repository.forgotPassword(email)
                if (success) {
                    savedCode = code  // ← احفظ الكود
                    _codeSent.value = true
                } else {
                    _error.value = code
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Connection error"
            }
            _isLoading.value = false
        }
    }

    fun resetPassword(code: String, newPassword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val (success, message) = repository.resetPassword(savedEmail, code, newPassword)
            if (success) _passwordReset.value = true
            else _error.value = message
            _isLoading.value = false
        }
    }

    fun resetState() {
        _codeSent.value = false
        _passwordReset.value = false
        _error.value = null
        savedEmail = ""
    }
}