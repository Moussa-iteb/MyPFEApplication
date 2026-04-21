package com.example.mypfeapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mypfeapplication.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: UserRepository  // ✅ injecté automatiquement par Hilt
) : ViewModel() {                           // ✅ ViewModel simple, plus AndroidViewModel

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    private val _hasBike = MutableLiveData<Boolean>(false)
    val hasBike: LiveData<Boolean> = _hasBike

    private val _showHistory = MutableLiveData<Boolean>(false)
    val showHistory: LiveData<Boolean> = _showHistory

    private val _selectedTab = MutableLiveData<Int>(0)
    val selectedTab: LiveData<Int> = _selectedTab

    init {
        loadUsername()
    }

    private fun loadUsername() {
        _username.value = repository.getUsername()
        _hasBike.value = true
    }

    fun onTabSelected(tab: Int) {
        _selectedTab.value = tab
    }

    fun onViewHistory() {
        _showHistory.value = true
    }

    fun onBackFromHistory() {
        _showHistory.value = false
    }

    fun setHasBike(value: Boolean) {
        _hasBike.value = value
    }

    fun getUsername(): String = repository.getUsername()

    fun getToken(): String? = repository.getToken()

    fun logout() = repository.logout()
}