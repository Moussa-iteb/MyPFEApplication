package com.example.mypfeapplication.model

import com.google.gson.annotations.SerializedName

data class ScanTripRequest(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("bike_id")
    val bikeId: Int
)

data class ScanTripResponse(
    val success: Boolean,
    val message: String,
    val data: TripUserData?
)

data class TripUserData(
    val id: Int,
    @SerializedName("trip_id")
    val tripId: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("bike_id")
    val bikeId: Int,
    val status: String?,
    @SerializedName("joined_at")
    val joinedAt: String?
)
data class TripDetailsResponse(
    val success: Boolean,
    val data: TripDetailData?
)

data class TripDetailData(
    val id: Int,
    @SerializedName("trip_users")
    val tripUsers: List<TripUserData>?
)