package com.example.mypfeapplication.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.example.mypfeapplication.database.AppDatabase
import com.example.mypfeapplication.network.ApiClient
import com.example.mypfeapplication.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: UserRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {


        return try {
            val db    = AppDatabase.getInstance(applicationContext)
            val token = repository.getToken() ?: return Result.failure()

            // ─── 1. Sync tracking points ──────────────────────────────
            val trackingDao = db.trackingPointDao()
            val unsynced    = trackingDao.getUnsynced()
            Log.d("SYNC", "Syncing ${unsynced.size} tracking points...")

            unsynced.forEach { point ->
                try {
                    val response = ApiClient.apiService.addTrackingPoint(
                        token      = "Bearer $token",
                        tripUserId = point.tripUserId,
                        body       = mapOf(
                            "latitude"  to point.latitude,
                            "longitude" to point.longitude
                        )
                    )
                    when {
                        response.isSuccessful -> {
                            trackingDao.markSynced(point.id)
                            Log.d("SYNC", "✅ Point ${point.id} synced")
                        }
                        response.code() == 404 -> {
                            trackingDao.markSynced(point.id)
                            Log.w("SYNC", "⚠️ Point ${point.id} tripUserId=${point.tripUserId} not found — discarded")
                        }
                        else -> {
                            Log.e("SYNC", "❌ Point ${point.id} HTTP ${response.code()}")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("SYNC", "❌ Point ${point.id} failed: ${e.message}")
                }
            }
            trackingDao.deleteSynced()

            // ─── 2. Sync pending end trip from SharedPrefs ──────────────
            if (repository.hasPendingEndTrip()) {
                val tripId = repository.getTripId()
                val lat    = repository.getPendingEndTripLat()
                val lng    = repository.getPendingEndTripLng()
                Log.d("SYNC", "Found pending end trip: tripId=$tripId lat=$lat lng=$lng")

                if (tripId != -1) {
                    try {
                        val response = ApiClient.apiService.endTrip(
                            "Bearer $token",
                            tripId,
                            mapOf("end_point_lat" to lat, "end_point_lng" to lng)
                        )
                        if (response.isSuccessful) {
                            repository.clearPendingEndTrip()
                            repository.saveTripId(-1)
                            repository.saveTripUserId(-1)
                            Log.d("SYNC", "✅ EndTrip $tripId synced from SharedPrefs")
                        } else {
                            Log.e("SYNC", "❌ EndTrip HTTP ${response.code()}")
                        }
                    } catch (e: Exception) {
                        Log.e("SYNC", "❌ EndTrip failed: ${e.message}")
                    }
                }
            }

            // ─── 3. Sync pending end trips from Room (fallback) ─────────
            val endTripDao   = db.pendingEndTripDao()
            val pendingTrips = endTripDao.getUnsynced()
            Log.d("SYNC", "Syncing ${pendingTrips.size} pending end trips from Room...")

            pendingTrips.forEach { pending ->
                try {
                    val response = ApiClient.apiService.endTrip(
                        "Bearer $token",
                        pending.tripId,
                        mapOf("end_point_lat" to pending.lat, "end_point_lng" to pending.lng)
                    )
                    when {
                        response.isSuccessful -> {
                            endTripDao.markSynced(pending.tripId)
                            Log.d("SYNC", "✅ EndTrip ${pending.tripId} synced from Room")
                        }
                        response.code() == 404 -> {
                            endTripDao.markSynced(pending.tripId)
                            Log.w("SYNC", "⚠️ EndTrip ${pending.tripId} not found — discarded")
                        }
                        else -> {
                            Log.e("SYNC", "❌ EndTrip ${pending.tripId} HTTP ${response.code()}")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("SYNC", "❌ EndTrip ${pending.tripId} failed: ${e.message}")
                }
            }
            endTripDao.deleteSynced()

            Result.success()
        } catch (e: Exception) {
            Log.e("SYNC", "Sync failed: ${e.message}")
            Result.retry()
        }
    }

    private fun createForegroundInfo(): ForegroundInfo {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "sync_channel",
                "Sync",
                NotificationManager.IMPORTANCE_LOW
            )
            val nm = applicationContext.getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, "sync_channel")
            .setContentTitle("SmartRide")
            .setContentText("Syncing trip data...")
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .build()

        return ForegroundInfo(1001, notification)
    }
}