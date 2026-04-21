package com.example.mypfeapplication.network

import com.example.mypfeapplication.model.AuthResponse
import com.example.mypfeapplication.model.LoginRequest
import com.example.mypfeapplication.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

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
}
