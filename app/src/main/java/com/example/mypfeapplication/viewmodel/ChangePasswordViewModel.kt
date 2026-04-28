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
class ChangePasswordViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _success = MutableLiveData(false)
    val success: LiveData<Boolean> = _success

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun changePassword(current: String, new: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val (ok, msg) = repository.changePassword(current, new)
            if (ok) _success.value = true
            else _error.value = msg
            _isLoading.value = false
        }
    }

    fun resetState() {
        _success.value = false
        _error.value = null
    }
}