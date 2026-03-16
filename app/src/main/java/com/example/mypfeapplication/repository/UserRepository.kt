package com.example.mypfeapplication.repository

import com.example.mypfeapplication.model.AuthData
import com.example.mypfeapplication.model.LoginRequest
import com.example.mypfeapplication.model.RegisterRequest
import com.example.mypfeapplication.network.ApiClient

class UserRepository {

    suspend fun login(email: String, password: String): AuthData? {
        return try {
            val response = ApiClient.apiService.login(
                LoginRequest(email = email, password = password)
            )
            if (response.isSuccessful) response.body()?.data else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun register(
        username: String,
        email: String,
        password: String,
        firstName: String? = null,
        lastName: String? = null
    ): AuthData? {
        return try {
            val response = ApiClient.apiService.register(
                RegisterRequest(
                    username = username,
                    email = email,
                    password = password,
                    firstName = firstName,
                    lastName = lastName
                )
            )
            if (response.isSuccessful) response.body()?.data else null
        } catch (e: Exception) {
            null
        }
    }
}