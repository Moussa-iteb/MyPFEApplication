package com.example.mypfeapplication.network

import com.example.mypfeapplication.model.AuthResponse

import com.example.mypfeapplication.model.LoginRequest
import com.example.mypfeapplication.model.RegisterRequest
import com.example.mypfeapplication.model.ScanBikeRequest
import com.example.mypfeapplication.model.ScanBikeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("auth/login/user")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST("auth/forgot-password/user")
    suspend fun forgotPasswordUser(
        @Body body: Map<String, String>
    ): Response<Any>

    @POST("auth/reset-password/user")
    suspend fun resetPasswordUser(
        @Body body: Map<String, String>
    ): Response<Any>

    @POST("bikes/scan")
    suspend fun scanBike(
        @Header("Authorization") token: String,
        @Body request: ScanBikeRequest
    ): Response<ScanBikeResponse>

    @PUT("bike-assignments/my/return")
    suspend fun returnMyBike(
        @Header("Authorization") token: String
    ): Response<Any>
    @GET("bike-assignments/my/active")
    suspend fun getMyActiveBike(
        @Header("Authorization") token: String
    ): Response<ScanBikeResponse>
    @PUT("users/{id}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("id") userId: Int,
        @Body body: Map<String, String>
    ): Response<Any>
    @PUT("users/{id}/password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Path("id") userId: Int,
        @Body body: Map<String, String>
    ): Response<Unit>
}