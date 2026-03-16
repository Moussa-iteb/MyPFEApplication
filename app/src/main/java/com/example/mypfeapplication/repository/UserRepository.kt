package com.example.mypfeapplication.repository

import android.content.Context
import com.example.mypfeapplication.model.AuthData
import com.example.mypfeapplication.model.LoginRequest
import com.example.mypfeapplication.model.RegisterRequest
import com.example.mypfeapplication.network.ApiClient

class UserRepository(private val context: Context) {

    private val sharedPref = context.getSharedPreferences("SmartRide", Context.MODE_PRIVATE)

    suspend fun login(email: String, password: String): AuthData? {
        return try {
            val response = ApiClient.apiService.login(
                LoginRequest(email = email, password = password)
            )
            if (response.isSuccessful) {
                val data = response.body()?.data
                // ✅ Sauvegarde token ici dans le Repository
                data?.token?.let { token ->
                    sharedPref.edit().putString("token", token).apply()
                }
                data
            } else null
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
            if (response.isSuccessful) {
                val data = response.body()?.data
                // ✅ Sauvegarde token ici dans le Repository
                data?.token?.let { token ->
                    sharedPref.edit().putString("token", token).apply()
                }
                data
            } else null
        } catch (e: Exception) {
            null
        }
    }

    fun getToken(): String? {
        return sharedPref.getString("token", null)
    }

    fun logout() {
        sharedPref.edit().remove("token").apply()
    }
}