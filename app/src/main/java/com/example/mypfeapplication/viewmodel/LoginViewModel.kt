package com.example.mypfeapplication.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypfeapplication.model.AuthResponse
import com.example.mypfeapplication.repository.NotificationRepository
import com.example.mypfeapplication.repository.UserRepository
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: UserRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

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
                    val token = result.data?.token
                    token?.let { repository.saveToken(it) }
                    result.data?.user?.role?.let     { repository.saveRole(it) }
                    result.data?.user?.username?.let { repository.saveUsername(it) }
                    result.data?.user?.email?.let    { repository.saveEmail(it) }
                    result.data?.user?.id?.let {
                        repository.saveUserId(it)
                        notificationRepository.setCurrentUserId(it)
                        Log.d("FCM", "✅ currentUserId set to $it")
                    }

                    token?.let { saveFcmToken(it) }
                    _authResponse.value = result
                }
            } else {
                _error.value = result?.message ?: "Email ou mot de passe invalide"
            }
            _isLoading.value = false
        }
    }

    private fun saveFcmToken(userToken: String) {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { fcmToken ->
                viewModelScope.launch {
                    try {
                        repository.saveFcmToken(userToken, fcmToken)
                        Log.d("FCM", "✅ Token saved: $fcmToken")
                    } catch (e: Exception) {
                        Log.e("FCM", "❌ Failed: ${e.message}")
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("FCM", "❌ Failed to get token: ${e.message}")
            }
    }

    fun showAdminError() { _adminError.value = true }
    fun clearAdminError() { _adminError.value = false }
}