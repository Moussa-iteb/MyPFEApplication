package com.example.mypfeapplication.database

import androidx.room.*

@Dao
interface TrackingPointDao {

    @Insert
    suspend fun insert(point: TrackingPointEntity): Long

    @Query("SELECT * FROM tracking_points WHERE synced = 0")
    suspend fun getUnsynced(): List<TrackingPointEntity>

    @Query("UPDATE tracking_points SET synced = 1 WHERE id = :id")
    suspend fun markSynced(id: Int)

    @Query("DELETE FROM tracking_points WHERE synced = 1")
    suspend fun deleteSynced()
}