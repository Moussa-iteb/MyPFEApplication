package com.example.mypfeapplication.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.mypfeapplication.R
import com.example.mypfeapplication.repository.NotificationRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FcmService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationRepository: NotificationRepository

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title        = remoteMessage.data["title"] ?: "SmartRide"
        val body         = remoteMessage.data["body"]  ?: ""
        val targetUserId = remoteMessage.data["userId"]?.toIntOrNull()

        val currentUserId = notificationRepository.getCurrentUserId()
        android.util.Log.d("FCM", "📩 title=$title, targetUserId=$targetUserId, currentUserId=$currentUserId")

        // ✅ Filtre — affiche seulement si c'est pour cet utilisateur
        val isForCurrentUser = targetUserId == null || targetUserId == currentUserId

        if (isForCurrentUser) {
            notificationRepository.addNotification(title, body, targetUserId)
            showSystemNotification(title, body)
        } else {
            android.util.Log.d("FCM", "⛔ Ignorée — targetUserId=$targetUserId ≠ currentUserId=$currentUserId")
        }
    }

    private fun showSystemNotification(title: String, body: String) {
        val channelId = "smartride_channel"
        val manager   = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "SmartRide Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                enableVibration(true)
            }
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onNewToken(token: String) {
        android.util.Log.d("FCM", "New token: $token")
    }
}