package com.example.mypfeapplication.network

import com.example.mypfeapplication.model.AuthResponse
import com.example.mypfeapplication.model.LoginRequest
import com.example.mypfeapplication.model.RegisterRequest
import com.example.mypfeapplication.model.ScanBikeRequest
import com.example.mypfeapplication.model.ScanBikeResponse
import com.example.mypfeapplication.model.ScanTripRequest
import com.example.mypfeapplication.model.ScanTripResponse
import com.example.mypfeapplication.model.TripDetailsResponse
import com.example.mypfeapplication.model.UserTripsResponse
import com.example.mypfeapplication.model.AllTripsResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.Part
import com.example.mypfeapplication.model.PhotoUploadResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("auth/login/user")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/forgot-password/user")
    suspend fun forgotPasswordUser(@Body body: Map<String, String>): Response<Map<String, Any>>

    @POST("auth/reset-password/user")
    suspend fun resetPasswordUser(@Body body: Map<String, String>): Response<Any>

    @PUT("bike-assignments/my/return")
    suspend fun returnMyBike(@Header("Authorization") token: String): Response<Any>

    @GET("bike-assignments/my/active")
    suspend fun getMyActiveBike(@Header("Authorization") token: String): Response<ScanBikeResponse>

    @PUT("users/{id}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("id") userId: Int,
        @Body body: Map<String, String>
    ): Response<Any>
    // FcmTokenRequest.kt
    data class FcmTokenRequest(val fcmToken: String)

    // Dans ton ApiService.kt — ajoute :
    @POST("users/fcm-token")
    suspend fun saveFcmToken(
        @Header("Authorization") token: String,
        @Body body: FcmTokenRequest
    ): Response<Unit>

    @PUT("users/{id}/password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Path("id") userId: Int,
        @Body body: Map<String, String>
    ): Response<Unit>
    @POST("trips/tracking/{tripUserId}")
    suspend fun addTrackingPoint(
        @Header("Authorization") token: String,
        @Path("tripUserId") tripUserId: Int,
        @Body body: @JvmSuppressWildcards Map<String, Any>  // ← نفس الشي
    ): Response<Any>

    @POST("bike-assignments/scan")
    suspend fun scanBike(
        @Header("Authorization") token: String,
        @Body request: ScanBikeRequest
    ): Response<ScanBikeResponse>
    @GET("trips/{id}")
    suspend fun getTripDetails(
        @Header("Authorization") token: String,
        @Path("id") tripId: Int
    ): Response<TripDetailsResponse>
    @POST("trips/{tripId}/users")
    suspend fun joinTrip(
        @Header("Authorization") token: String,
        @Path("tripId") tripId: Int,
        @Body body: ScanTripRequest
    ): Response<ScanTripResponse>
    @GET("trips/my-active")
    suspend fun getMyActiveTrip(
        @Header("Authorization") token: String
    ): Response<TripDetailsResponse>
    @PUT("trips/{id}/start")
    suspend fun startTrip(
        @Header("Authorization") token: String,
        @Path("id") tripId: Int
    ): Response<Any>
    @PUT("trips/{tripId}/users/{userId}/cancel")
    suspend fun cancelTripUser(
        @Header("Authorization") token: String,
        @Path("tripId") tripId: Int,
        @Path("userId") userId: Int
    ): Response<Any>
    @PUT("trips/{id}/end")
    suspend fun endTrip(
        @Header("Authorization") token: String,
        @Path("id") tripId: Int,
        @Body body: @JvmSuppressWildcards Map<String, Any>  // ← حط الـ annotation قبل Map
    ): Response<Any>
    @GET("trips/user/{userId}")
    suspend fun getUserTrips(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<UserTripsResponse>
    @GET("trips")
    suspend fun getAllTrips(
        @Header("Authorization") token: String
    ): Response<AllTripsResponse>
    @Multipart
    @POST("users/profile/photo")
    suspend fun uploadProfilePhoto(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part
    ): Response<PhotoUploadResponse>


}