package com.example.mypfeapplication.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PendingEndTripDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(entity: PendingEndTripEntity)

    @Query("SELECT * FROM pending_end_trip WHERE synced = 0")
    suspend fun getUnsynced(): List<PendingEndTripEntity>

    @Query("UPDATE pending_end_trip SET synced = 1 WHERE tripId = :tripId")
    suspend fun markSynced(tripId: Int)

    @Query("DELETE FROM pending_end_trip WHERE synced = 1")
    suspend fun deleteSynced()
}