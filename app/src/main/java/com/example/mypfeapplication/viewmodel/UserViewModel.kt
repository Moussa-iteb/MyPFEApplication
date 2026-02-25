package com.example.mypfeapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mypfeapplication.model.User
import com.example.mypfeapplication.repository.UserRepository

class UserViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    init {
        loadUser()
    }

    private fun loadUser() {
        _user.value = repository.getUser()
    }
}