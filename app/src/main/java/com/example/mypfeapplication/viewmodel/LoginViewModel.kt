package com.example.mypfeapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mypfeapplication.model.AuthData
import com.example.mypfeapplication.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserRepository()

    private val _authData = MutableLiveData<AuthData?>()
    val authData: LiveData<AuthData?> = _authData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.login(email, password)
            if (result != null) {
                _authData.value = result
            } else {
                _error.value = "Invalid email or password"
            }
            _isLoading.value = false
        }
    }
}