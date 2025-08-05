package com.phonetracker.app.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.phonetracker.app.model.Device
import java.util.Date

@Dao
interface DeviceDao {
    
    @Query("SELECT * FROM devices WHERE isActive = 1 ORDER BY lastLocationUpdate DESC")
    suspend fun getAllDevices(): List<Device>
    
    @Query("SELECT * FROM devices WHERE isActive = 1 ORDER BY lastLocationUpdate DESC")
    fun getAllDevicesLive(): LiveData<List<Device>>
    
    @Query("SELECT * FROM devices WHERE id = :id")
    suspend fun getDeviceById(id: Long): Device?
    
    @Query("SELECT * FROM devices WHERE phoneNumber = :phoneNumber")
    suspend fun getDeviceByPhoneNumber(phoneNumber: String): Device?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: Device): Long
    
    @Update
    suspend fun updateDevice(device: Device)
    
    @Delete
    suspend fun deleteDevice(device: Device)
    
    @Query("UPDATE devices SET lastKnownLatitude = :latitude, lastKnownLongitude = :longitude, lastLocationUpdate = :timestamp WHERE id = :deviceId")
    suspend fun updateDeviceLocation(deviceId: Long, latitude: Double, longitude: Double, timestamp: Date)
    
    @Query("UPDATE devices SET isOnline = :isOnline WHERE id = :deviceId")
    suspend fun updateDeviceOnlineStatus(deviceId: Long, isOnline: Boolean)
    
    @Query("SELECT * FROM devices WHERE isOnline = 1 AND isActive = 1")
    suspend fun getOnlineDevices(): List<Device>
}