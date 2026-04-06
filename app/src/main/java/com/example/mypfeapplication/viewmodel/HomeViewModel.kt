package com.example.mypfeapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mypfeapplication.repository.UserRepository

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserRepository(application)

    // ✅ Username depuis le token JWT
    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    // ✅ Est-ce que l'utilisateur a un vélo associé
    private val _hasBike = MutableLiveData<Boolean>(false)
    val hasBike: LiveData<Boolean> = _hasBike

    // ✅ Afficher l'historique ou non
    private val _showHistory = MutableLiveData<Boolean>(false)
    val showHistory: LiveData<Boolean> = _showHistory

    // ✅ Onglet sélectionné de la bottom bar
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

    fun logout() {
        repository.logout()
    }
}