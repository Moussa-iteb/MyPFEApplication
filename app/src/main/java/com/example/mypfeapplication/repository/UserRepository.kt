package com.example.mypfeapplication.repository

import android.content.Context
import com.example.mypfeapplication.model.AuthResponse
import com.example.mypfeapplication.model.ScanBikeRequest
import com.example.mypfeapplication.model.ScanBikeResponse
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
            val response = apiService.forgotPasswordUser(mapOf("email" to email))
            when {
                response.isSuccessful -> Pair(true, "Code sent successfully")
                response.code() == 404 -> Pair(false, "Email not found")
                response.code() == 403 -> Pair(false, "Access denied")
                else -> Pair(false, "Error: ${response.code()}")
            }
        } catch (e: java.net.SocketTimeoutException) {
            Pair(true, "Code sent — check your email")
        } catch (e: Exception) {
            Pair(false, e.message ?: "Connection error")
        }
    }

    suspend fun resetPassword(email: String, code: String, newPassword: String): Pair<Boolean, String> {
        return try {
            val response = apiService.resetPasswordUser(
                mapOf("email" to email, "code" to code, "newPassword" to newPassword)
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

    suspend fun scanBike(qrCode: String): ScanBikeResponse? {
        return try {
            val token = getToken() ?: return null
            val response = ApiClient.apiService.scanBike(
                token = "Bearer $token",
                request = ScanBikeRequest(qrCode = qrCode)
            )
            if (response.isSuccessful) {
                response.body()
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val json = org.json.JSONObject(errorBody ?: "")
                    json.getString("message")
                } catch (e: Exception) {
                    "Bike not available"
                }
                ScanBikeResponse(
                    success = false,
                    message = errorMessage,
                    data = null
                )
            }
        } catch (e: Exception) {
            ScanBikeResponse(
                success = false,
                message = "Network error",
                data = null
            )
        }
    }

    // ← خارج scanBike ✅
    suspend fun returnBike(): Boolean {
        return try {
            val token = getToken() ?: return false
            val response = apiService.returnMyBike("Bearer $token")
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
    suspend fun getMyActiveBike(): ScanBikeResponse? {
        return try {
            val token = getToken() ?: return null
            val response = apiService.getMyActiveBike("Bearer $token")
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }
    fun getUserId(): Int = prefs.getInt("userId", -1)
    fun saveUserId(id: Int) = prefs.edit().putInt("userId", id).apply()
    fun getEmail(): String = prefs.getString("email", "") ?: ""
    fun saveEmail(email: String) = prefs.edit().putString("email", email).apply()
    fun getPhone(): String = prefs.getString("phone", "") ?: ""
    fun savePhone(phone: String) = prefs.edit().putString("phone", phone).apply()

    suspend fun updateProfile(username: String, email: String, phone: String): Boolean {
        return try {
            val token = getToken()
            val userId = getUserId()
            if (token == null || userId == -1) return false
            val response = apiService.updateUser(
                token = "Bearer $token",
                userId = userId,
                body = mapOf("username" to username, "email" to email, "phone" to phone) // ← زيد phone
            )
            if (response.isSuccessful) {
                saveUsername(username)
                savePhone(phone) // ← احفظ phone
            }
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
    suspend fun changePassword(currentPassword: String, newPassword: String): Pair<Boolean, String> {
        return try {
            val token = getToken() ?: return Pair(false, "Not logged in")
            val userId = getUserId()
            if (userId == -1) return Pair(false, "User not found")

            val response = apiService.changePassword(
                token = "Bearer $token",
                userId = userId,
                body = mapOf(
                    "currentPassword" to currentPassword,
                    "newPassword" to newPassword
                )
            )
            when {
                response.isSuccessful -> Pair(true, "Password changed successfully")
                response.code() == 400 -> Pair(false, "Current password is incorrect")
                response.code() == 404 -> Pair(false, "User not found")
                else -> Pair(false, "Error: ${response.code()}")
            }
        } catch (e: java.net.SocketTimeoutException) {
            Pair(false, "Connection timeout")
        } catch (e: java.net.UnknownHostException) {
            Pair(false, "No internet connection")
        } catch (e: Exception) {
            Pair(false, "Network error: ${e.message}")
        }
    }
}