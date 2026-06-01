package com.example.mypfeapplication.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_end_trip")
data class PendingEndTripEntity(
    @PrimaryKey val tripId: Int,
    val lat: Double,
    val lng: Double,
    val synced: Boolean = false
)