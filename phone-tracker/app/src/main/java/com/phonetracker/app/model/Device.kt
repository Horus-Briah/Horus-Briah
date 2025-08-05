package com.phonetracker.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "devices")
data class Device(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val phoneNumber: String,
    val name: String,
    val lastKnownLatitude: Double? = null,
    val lastKnownLongitude: Double? = null,
    val lastLocationUpdate: Date? = null,
    val isOnline: Boolean = false,
    val createdAt: Date = Date(),
    val isActive: Boolean = true
)

data class LocationUpdate(
    val deviceId: Long,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Date,
    val accuracy: Float? = null,
    val speed: Float? = null,
    val bearing: Float? = null
)

data class TrackingSession(
    val id: Long = 0,
    val deviceId: Long,
    val startTime: Date,
    val endTime: Date? = null,
    val isActive: Boolean = true
)