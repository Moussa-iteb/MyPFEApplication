package com.example.mypfeapplication.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracking_points")
data class TrackingPointEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tripUserId: Int,
    val latitude: Double,
    val longitude: Double,
    val recordedAt: Long = System.currentTimeMillis(),
    val synced: Boolean = false
)