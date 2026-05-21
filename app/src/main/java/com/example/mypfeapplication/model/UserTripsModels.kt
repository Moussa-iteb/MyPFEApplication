package com.example.mypfeapplication.model

import com.google.gson.annotations.SerializedName

data class UserTripsResponse(
    val success: Boolean,
    val data: List<UserTripData>?
)

data class UserTripData(
    val id: Int,
    @SerializedName("started_at")
    val startedAt: String?,
    @SerializedName("distance_km")
    val distanceKm: Double?,
    @SerializedName("start_address")
    val startAddress: String?,
    @SerializedName("destination_address")
    val destinationAddress: String?,
    @SerializedName("tripUsers")
    val tripUsers: List<TripUserDetail>?
)

data class TripUserDetail(
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("joined_at")
    val joinedAt: String?,
    @SerializedName("left_at")
    val leftAt: String?,
    val status: String?,
    @SerializedName("trackingPoints")
    val trackingPoints: List<TrackingPoint>?
)

data class TrackingPoint(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    @SerializedName("recorded_at")
    val recordedAt: String?
)
data class AllTripsResponse(
    val success: Boolean,
    val data: List<TripItem>?
)

data class TripItem(
    val id: Int,
    @SerializedName("start_address") val startAddress: String?,
    @SerializedName("destination_address") val destinationAddress: String?,
    @SerializedName("start_point_lat") val startLat: Double?,
    @SerializedName("start_point_lng") val startLng: Double?,
    @SerializedName("destination_lat") val destLat: Double?,
    @SerializedName("destination_lng") val destLng: Double?,
    @SerializedName("started_at") val startedAt: String?,
    @SerializedName("distance_km") val distanceKm: Double?,
    val status: String?,
    @SerializedName("tripUsers") val tripUsers: List<TripUserDetail>?
)