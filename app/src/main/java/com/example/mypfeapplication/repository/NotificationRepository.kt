package com.example.mypfeapplication.repository

import android.content.Context
import com.example.mypfeapplication.model.AppNotification
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications

    fun setCurrentUserId(id: Int) {
        prefs.edit().putInt("userId", id).apply()
        android.util.Log.d("FCM", "✅ currentUserId set to $id")
    }

    fun getCurrentUserId(): Int = prefs.getInt("userId", -1)

    fun addNotification(title: String, body: String, targetUserId: Int? = null) {
        val userId = prefs.getInt("userId", -1)
        android.util.Log.d("FCM", "📩 addNotif — targetUserId=$targetUserId, userId=$userId")

        if (targetUserId != null && targetUserId != userId) {
            android.util.Log.d("FCM", "⛔ Ignorée — targetUserId=$targetUserId ≠ userId=$userId")
            return
        }
        val notif = AppNotification(title = title, body = body)
        _notifications.value = listOf(notif) + _notifications.value
        android.util.Log.d("FCM", "✅ Notif ajoutée: $title")
    }

    fun markAllAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun clearAll() {
        _notifications.value = emptyList()
    }

    fun unreadCount(): Int = _notifications.value.count { !it.isRead }
}