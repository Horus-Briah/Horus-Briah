package com.phonetracker.companion.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class LocationData(
    @SerializedName("phone_number")
    val phoneNumber: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("timestamp")
    val timestamp: Date,
    @SerializedName("accuracy")
    val accuracy: Float? = null,
    @SerializedName("speed")
    val speed: Float? = null,
    @SerializedName("bearing")
    val bearing: Float? = null
)

data class LocationResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("timestamp")
    val timestamp: Date
)