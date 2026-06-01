package com.example.mypfeapplication.repository

import android.content.Context
import org.json.JSONObject
import com.example.mypfeapplication.model.AuthResponse
import com.example.mypfeapplication.model.ScanBikeRequest
import com.example.mypfeapplication.model.ScanBikeResponse
import com.example.mypfeapplication.model.ScanTripRequest
import com.example.mypfeapplication.model.ScanTripResponse
import com.example.mypfeapplication.model.TripItem
import com.example.mypfeapplication.model.UserTripData
import com.example.mypfeapplication.network.ApiClient
import com.example.mypfeapplication.network.ApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val apiService = ApiClient.apiService

    // ─── Auth ─────────────────────────────────────────────────────────────────

    suspend fun login(email: String, password: String): AuthResponse? {
        return try {
            val response = apiService.login(
                com.example.mypfeapplication.model.LoginRequest(email, password)
            )
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) { null }
    }

    suspend fun register(username: String, email: String, password: String): AuthResponse? {
        return try {
            val response = apiService.register(
                com.example.mypfeapplication.model.RegisterRequest(username, email, password)
            )
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) { null }
    }

    suspend fun forgotPassword(email: String): Pair<Boolean, String> {
        return try {
            val response = apiService.forgotPasswordUser(mapOf("email" to email))
            when {
                response.isSuccessful -> {
                    val body = response.body()
                    val code = body?.get("code")?.toString() ?: ""
                    Pair(true, code)
                }
                response.code() == 404 -> Pair(false, "Email not found")
                response.code() == 403 -> Pair(false, "Access denied")
                else -> Pair(false, "Error: ${response.code()}")
            }
        } catch (e: java.net.SocketTimeoutException) {
            Pair(true, "")
        } catch (e: Exception) {
            Pair(false, e.message ?: "Connection error")
        }
    }

    suspend fun resetPassword(
        email: String,
        code: String,
        newPassword: String
    ): Pair<Boolean, String> {
        return try {
            val response = apiService.resetPasswordUser(
                mapOf("email" to email, "code" to code, "newPassword" to newPassword)
            )
            when {
                response.isSuccessful  -> Pair(true,  "Password reset successfully")
                response.code() == 400 -> Pair(false, "Invalid or expired code")
                response.code() == 404 -> Pair(false, "User not found")
                else                   -> Pair(false, "Error: ${response.code()}")
            }
        } catch (e: Exception) {
            Pair(false, e.message ?: "Connection error")
        }
    }

    suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Pair<Boolean, String> {
        return try {
            val token  = getToken()  ?: return Pair(false, "Not logged in")
            val userId = getUserId()
            if (userId == -1) return Pair(false, "User not found")

            val response = apiService.changePassword(
                token  = "Bearer $token",
                userId = userId,
                body   = mapOf(
                    "currentPassword" to currentPassword,
                    "newPassword"     to newPassword
                )
            )
            when {
                response.isSuccessful  -> Pair(true,  "Password changed successfully")
                response.code() == 400 -> Pair(false, "Current password is incorrect")
                response.code() == 404 -> Pair(false, "User not found")
                else                   -> Pair(false, "Error: ${response.code()}")
            }
        } catch (e: java.net.SocketTimeoutException) {
            Pair(false, "Connection timeout")
        } catch (e: java.net.UnknownHostException) {
            Pair(false, "No internet connection")
        } catch (e: Exception) {
            Pair(false, "Network error: ${e.message}")
        }
    }

    suspend fun updateProfile(username: String, email: String, phone: String): Boolean {
        return try {
            val token  = getToken()  ?: return false
            val userId = getUserId()
            if (userId == -1) return false

            val body = mutableMapOf("username" to username, "email" to email)
            if (phone.isNotBlank()) body["phone"] = phone

            val response = apiService.updateUser(
                token  = "Bearer $token",
                userId = userId,
                body   = body
            )
            if (response.isSuccessful) {
                saveUsername(username)
                saveEmail(email)
                if (phone.isNotBlank()) savePhone(phone)
            }
            response.isSuccessful
        } catch (e: Exception) { false }
    }

    // ✅ FCM Token — sauvegarde le token Firebase après login
    suspend fun saveFcmToken(userToken: String, fcmToken: String) {
        try {
            apiService.saveFcmToken(
                token = "Bearer $userToken",
                body  = ApiService.FcmTokenRequest(fcmToken = fcmToken)
            )
            android.util.Log.d("FCM", "✅ FCM token saved to backend")
        } catch (e: Exception) {
            android.util.Log.e("FCM", "❌ saveFcmToken error: ${e.message}")
        }
    }

    // ─── Bike ─────────────────────────────────────────────────────────────────

    suspend fun scanBike(qrCode: String): ScanBikeResponse? {
        return try {
            val token = getToken() ?: return null
            android.util.Log.d("SCAN_BIKE", "QR Code sent: $qrCode")

            val response = ApiClient.apiService.scanBike(
                token   = "Bearer $token",
                request = ScanBikeRequest(qrCode = qrCode)
            )
            android.util.Log.d("SCAN_BIKE", "Response code: ${response.code()}")

            if (response.isSuccessful) {
                response.body()
            } else {
                val errorBody    = response.errorBody()?.string()
                val errorMessage = try {
                    JSONObject(errorBody ?: "").getString("message")
                } catch (e: Exception) { "Bike not available" }
                ScanBikeResponse(success = false, message = errorMessage, data = null)
            }
        } catch (e: Exception) {
            android.util.Log.e("SCAN_BIKE", "Exception: ${e.message}")
            ScanBikeResponse(success = false, message = "Network error", data = null)
        }
    }

    suspend fun cancelTrip(): Boolean {
        return try {
            val token  = getToken()  ?: return false
            val tripId = getTripId()
            val userId = getUserId()
            if (tripId == -1) {
                android.util.Log.e("TRIP", "cancelTrip: tripId not found")
                return false
            }

            val response = ApiClient.apiService.cancelTripUser(
                token  = "Bearer $token",
                tripId = tripId,
                userId = userId
            )
            android.util.Log.d("TRIP", "cancelTrip response: ${response.code()}")

            if (response.isSuccessful) {
                saveTripId(-1)
                saveTripUserId(-1)
            }

            response.isSuccessful
        } catch (e: Exception) {
            android.util.Log.e("TRIP", "cancelTrip error: ${e.message}")
            false
        }
    }

    suspend fun fetchAndSaveActiveTrip() {
        try {
            val token  = getToken() ?: return
            val userId = getUserId()

            val response = ApiClient.apiService.getMyActiveTrip("Bearer $token")
            if (response.isSuccessful) {
                val trip = response.body()?.data
                if (trip != null) {
                    saveTripId(trip.id)
                    android.util.Log.d("TRIP", "✅ saved tripId=${trip.id}")

                    val tripUser = trip.tripUsers?.find { it.userId == userId }
                    if (tripUser != null) {
                        saveTripUserId(tripUser.id)
                        android.util.Log.d("TRIP", "✅ saved tripUserId=${tripUser.id}")
                    } else {
                        val first = trip.tripUsers?.firstOrNull()
                        if (first != null) {
                            saveTripUserId(first.id)
                            android.util.Log.d("TRIP", "✅ saved tripUserId (first)=${first.id}")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("TRIP", "fetchAndSaveActiveTrip error: ${e.message}")
        }
    }

    suspend fun getUserTrips(): List<UserTripData> {
        return try {
            val token  = getToken()
            val userId = getUserId()

            android.util.Log.d("TRIPS_DEBUG", "token=${token?.take(20)}, userId=$userId")

            if (token == null) return emptyList()
            if (userId == -1)  return emptyList()

            val response = ApiClient.apiService.getUserTrips(
                token  = "Bearer $token",
                userId = userId
            )

            android.util.Log.d("TRIPS_DEBUG", "response code=${response.code()}")

            if (response.isSuccessful) {
                val allTrips = response.body()?.data ?: emptyList()
                android.util.Log.d("TRIPS_DEBUG", "allTrips size=${allTrips.size}")

                val filtered = allTrips.mapNotNull { trip ->
                    val myTripUser = trip.tripUsers?.find { it.userId == userId }
                    if (myTripUser != null) trip.copy(tripUsers = listOf(myTripUser)) else null
                }

                android.util.Log.d("TRIPS_DEBUG", "filtered size=${filtered.size}")
                filtered
            } else {
                android.util.Log.e("TRIPS_DEBUG", "HTTP error=${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            android.util.Log.e("TRIPS_DEBUG", "exception=${e.message}")
            emptyList()
        }
    }

    suspend fun getMyActiveBike(): ScanBikeResponse? {
        return try {
            val token = getToken() ?: return null
            val response = apiService.getMyActiveBike("Bearer $token")
            android.util.Log.d("CHECK_BIKE", "Code: ${response.code()}")
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            android.util.Log.e("CHECK_BIKE", "Exception: ${e.message}")
            null
        }
    }

    suspend fun returnBike(): Boolean {
        return try {
            val token = getToken() ?: return false
            val response = apiService.returnMyBike("Bearer $token")
            response.isSuccessful
        } catch (e: Exception) { false }
    }

    // ─── Trip ─────────────────────────────────────────────────────────────────

    suspend fun scanTrip(qrCode: String, bikeId: Int): ScanTripResponse? {
        return try {
            val token = getToken() ?: return null
            val json  = JSONObject(qrCode)

            val tripId = when {
                json.has("tripId")  -> json.getInt("tripId")
                json.has("trip_id") -> json.getInt("trip_id")
                json.has("id")      -> json.getInt("id")
                else -> {
                    android.util.Log.e("SCAN_TRIP", "No tripId found in QR: $qrCode")
                    return ScanTripResponse(false, "Invalid trip QR code", null)
                }
            }

            val bikeIdToUse = if (json.has("bikeId") && json.getInt("bikeId") != 0)
                json.getInt("bikeId") else bikeId

            val currentUserId = getUserId()
            saveTripUserId(-1)
            saveTripId(tripId)

            android.util.Log.d("SCAN_TRIP", "tripId=$tripId, userId=$currentUserId, bikeId=$bikeIdToUse")

            val response = ApiClient.apiService.joinTrip(
                token  = "Bearer $token",
                tripId = tripId,
                body   = ScanTripRequest(userId = currentUserId, bikeId = bikeIdToUse)
            )

            val rawError = if (!response.isSuccessful) {
                response.errorBody()?.string().also { err ->
                    android.util.Log.e("SCAN_TRIP", "HTTP ${response.code()} RAW ERROR: $err")
                }
            } else null

            when {
                response.isSuccessful -> {
                    val tripUserId = response.body()?.data?.id
                    tripUserId?.let {
                        saveTripUserId(it)
                        android.util.Log.d("SCAN_TRIP", "✅ Saved tripUserId=$it")
                    }
                    response.body()
                }

                response.code() == 409 -> {
                    android.util.Log.d("SCAN_TRIP", "409 — fetching existing tripUserId...")
                    try {
                        val detailsResponse = ApiClient.apiService.getTripDetails(
                            token  = "Bearer $token",
                            tripId = tripId
                        )
                        if (detailsResponse.isSuccessful) {
                            val found = detailsResponse.body()?.data?.tripUsers
                                ?.find { it.userId == currentUserId }
                            if (found != null) {
                                saveTripUserId(found.id)
                                android.util.Log.d("SCAN_TRIP", "✅ Found existing tripUserId=${found.id}")
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("SCAN_TRIP", "Failed to fetch tripDetails: ${e.message}")
                    }
                    ScanTripResponse(success = true, message = "Trip already active", data = null)
                }

                response.code() == 400 -> {
                    val msg = try { JSONObject(rawError ?: "").getString("message") }
                    catch (e: Exception) { "Bad request" }
                    ScanTripResponse(false, msg, null)
                }

                response.code() == 404 ->
                    ScanTripResponse(false, "Trip #$tripId not found", null)

                response.code() == 500 -> {
                    val msg = try { JSONObject(rawError ?: "").optString("message", "Internal server error") }
                    catch (e: Exception) { "Internal server error" }
                    ScanTripResponse(false, "Server error: $msg", null)
                }

                else -> {
                    val msg = try { JSONObject(rawError ?: "").getString("message") }
                    catch (e: Exception) { "Error ${response.code()}" }
                    ScanTripResponse(false, msg, null)
                }
            }

        } catch (e: Exception) {
            android.util.Log.e("SCAN_TRIP", "Exception: ${e.message}")
            ScanTripResponse(false, "Network error: ${e.message}", null)
        }
    }

    suspend fun startTrip(): Boolean {
        return try {
            val token  = getToken()  ?: return false
            val tripId = getTripId()
            if (tripId == -1) return false

            android.util.Log.d("TRIP", "Starting trip #$tripId")
            val response = ApiClient.apiService.startTrip("Bearer $token", tripId)
            android.util.Log.d("TRIP", "Start trip response: ${response.code()}")
            response.isSuccessful
        } catch (e: Exception) {
            android.util.Log.e("TRIP", "Start error: ${e.message}")
            false
        }
    }

    suspend fun endTrip(lat: Double, lng: Double): Boolean {
        val tripId = getTripId()
        if (tripId == -1) return false

        savePendingEndTrip(lat, lng)

        return try {
            val token = getToken() ?: return false
            val response = ApiClient.apiService.endTrip(
                "Bearer $token", tripId,
                mapOf("end_point_lat" to lat, "end_point_lng" to lng)
            )
            if (response.isSuccessful) {
                clearPendingEndTrip()
                saveTripId(-1)
                saveTripUserId(-1)
                android.util.Log.d("TRIP", "✅ endTrip success, tripId=$tripId")
                true
            } else {
                android.util.Log.e("TRIP", "❌ endTrip HTTP ${response.code()}")
                false
            }
        } catch (e: Exception) {
            android.util.Log.e("TRIP", "❌ offline — pending saved in prefs")
            false
        }
    }

    // ─── Tracking ─────────────────────────────────────────────────────────────

    suspend fun sendTrackingPoint(lat: Double, lng: Double): Boolean {
        return try {
            val token      = getToken()      ?: return false
            val tripUserId = getTripUserId()
            if (tripUserId == -1) {
                android.util.Log.e("TRACKING", "tripUserId not found in prefs")
                return false
            }

            val response = ApiClient.apiService.addTrackingPoint(
                token      = "Bearer $token",
                tripUserId = tripUserId,
                body       = mapOf("latitude" to lat, "longitude" to lng)
            )
            android.util.Log.d("TRACKING", "Sent ($lat,$lng) → ${response.code()}")
            response.isSuccessful
        } catch (e: Exception) {
            android.util.Log.e("TRACKING", "Error: ${e.message}")
            false
        }
    }

    // ─── Explore ──────────────────────────────────────────────────────────────

    suspend fun getOpenTrips(): List<TripItem> {
        return try {
            val token  = getToken() ?: return emptyList()
            val userId = getUserId()
            val response = ApiClient.apiService.getAllTrips("Bearer $token")
            if (response.isSuccessful) {
                val all = response.body()?.data ?: emptyList()
                all.filter { trip ->
                    trip.tripUsers?.none { it.userId == userId } == true
                }
            } else emptyList()
        } catch (e: Exception) {
            android.util.Log.e("EXPLORE", "error: ${e.message}")
            emptyList()
        }
    }

    // ─── SharedPrefs helpers ──────────────────────────────────────────────────

    fun getToken(): String?          = prefs.getString("token", null)
    fun saveToken(token: String)     = prefs.edit().putString("token", token).apply()

    fun getUsername(): String        = prefs.getString("username", "User") ?: "User"
    fun saveUsername(name: String)   = prefs.edit().putString("username", name).apply()

    fun getRole(): String?           = prefs.getString("role", null)
    fun saveRole(role: String)       = prefs.edit().putString("role", role).apply()

    fun getUserId(): Int             = prefs.getInt("userId", -1)
    fun saveUserId(id: Int)          = prefs.edit().putInt("userId", id).apply()

    fun getEmail(): String           = prefs.getString("email", "") ?: ""
    fun saveEmail(email: String)     = prefs.edit().putString("email", email).apply()

    fun getPhone(): String           = prefs.getString("phone", "") ?: ""
    fun savePhone(phone: String)     = prefs.edit().putString("phone", phone).apply()

    fun getPhotoUrl(): String?       = prefs.getString("photoUrl", null)
    fun savePhotoUrl(url: String)    = prefs.edit().putString("photoUrl", url).apply()

    fun getTripId(): Int             = prefs.getInt("tripId", -1)
    fun saveTripId(id: Int)          = prefs.edit().putInt("tripId", id).apply()

    fun getTripUserId(): Int         = prefs.getInt("tripUserId", -1)
    fun saveTripUserId(id: Int)      = prefs.edit().putInt("tripUserId", id).apply()

    fun logout() {
        prefs.edit()
            .remove("token")
            .remove("role")
            .remove("userId")
            .remove("tripId")
            .remove("tripUserId")
            .remove("pending_end_lat")
            .remove("pending_end_lng")
            .remove("has_pending_end")
            .apply()
    }

    // ─── Pending End Trip ─────────────────────────────────────────────────────

    fun savePendingEndTrip(lat: Double, lng: Double) {
        prefs.edit()
            .putFloat("pending_end_lat", lat.toFloat())
            .putFloat("pending_end_lng", lng.toFloat())
            .putBoolean("has_pending_end", true)
            .commit()
    }

    fun hasPendingEndTrip(): Boolean   = prefs.getBoolean("has_pending_end", false)
    fun getPendingEndTripLat(): Double = prefs.getFloat("pending_end_lat", 0f).toDouble()
    fun getPendingEndTripLng(): Double = prefs.getFloat("pending_end_lng", 0f).toDouble()

    fun clearPendingEndTrip() {
        prefs.edit()
            .remove("pending_end_lat")
            .remove("pending_end_lng")
            .putBoolean("has_pending_end", false)
            .commit()
    }
}