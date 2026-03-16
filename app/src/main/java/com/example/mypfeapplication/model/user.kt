package com.example.mypfeapplication.model

data class User(
    val id: Int = 0,
    val username: String = "",
    val email: String = "",
    val firstName: String? = null,
    val lastName: String? = null,
    val role: String = "user"
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val firstName: String? = null,
    val lastName: String? = null
)

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val data: AuthData?
)

data class AuthData(
    val user: User? = null,
    val token: String? = null
)