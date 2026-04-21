package com.example.mypfeapplication.repository

import android.content.Context
import com.example.mypfeapplication.model.AuthResponse
import com.example.mypfeapplication.network.ApiClient
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val apiService = ApiClient.apiService

    suspend fun login(email: String, password: String): AuthResponse? {
        return try {
            val response = apiService.login(
                com.example.mypfeapplication.model.LoginRequest(email, password)
            )
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun register(username: String, email: String, password: String): AuthResponse? {
        return try {
            val response = apiService.register(
                com.example.mypfeapplication.model.RegisterRequest(username, email, password)
            )
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun forgotPassword(email: String): Pair<Boolean, String> {
        return try {
            val response = apiService.forgotPasswordUser(
                mapOf("email" to email)
            )
            when {
                response.isSuccessful -> Pair(true, "Code sent successfully")
                response.code() == 404 -> Pair(false, "Email not found")
                response.code() == 403 -> Pair(false, "Access denied")
                else -> Pair(false, "Error: ${response.code()}")
            }
        } catch (e: java.net.SocketTimeoutException) {
            // Timeout — لكن الـ email ربما أُرسل
            Pair(true, "Code sent — check your email")
        } catch (e: Exception) {
            Pair(false, e.message ?: "Connection error")
        }
    }

    suspend fun resetPassword(email: String, code: String, newPassword: String): Pair<Boolean, String> {
        return try {
            val response = apiService.resetPasswordUser(
                mapOf(
                    "email" to email,
                    "code" to code,
                    "newPassword" to newPassword
                )
            )
            when {
                response.isSuccessful -> Pair(true, "Password reset successfully")
                response.code() == 400 -> Pair(false, "Invalid or expired code")
                response.code() == 404 -> Pair(false, "User not found")
                else -> Pair(false, "Error: ${response.code()}")
            }
        } catch (e: Exception) {
            Pair(false, e.message ?: "Connection error")
        }
    }

    fun getToken(): String? = prefs.getString("token", null)
    fun getUsername(): String = prefs.getString("username", "User") ?: "User"
    fun saveToken(token: String) = prefs.edit().putString("token", token).apply()
    fun saveUsername(username: String) = prefs.edit().putString("username", username).apply()
    fun logout() = prefs.edit().clear().apply()
    fun getRole(): String? = prefs.getString("role", null)
    fun saveRole(role: String) = prefs.edit().putString("role", role).apply()
}