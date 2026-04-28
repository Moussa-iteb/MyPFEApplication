package com.example.mypfeapplication.model

import com.google.gson.annotations.SerializedName

data class ScanBikeRequest(
    @SerializedName("qrCode")
    val qrCode: String
)

data class ScanBikeResponse(
    val success: Boolean,
    val message: String,
    val data: ScanBikeData?
)

data class ScanBikeData(
    val bike: BikeData?,
    val assignment: AssignmentData?
)

data class AssignmentData(
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("bike_id")
    val bikeId: Int,
    val status: String?
)

data class BikeData(
    val id: Int,
    val model: String?,
    val brand: String?,
    @SerializedName("serialNumber")
    val serialNumber: String?,
    val status: String?,
    @SerializedName("batteryLevel")
    val batteryLevel: Int?,
    @SerializedName("qrCode")
    val qrCode: String?
)