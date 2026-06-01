package com.example.mypfeapplication.model

data class AppNotification(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val body: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)