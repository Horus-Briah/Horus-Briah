package com.phonetracker.app.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.phonetracker.app.database.AppDatabase
import com.phonetracker.app.model.Device
import java.util.Date

class DeviceRepository(context: Context) {
    
    private val deviceDao = AppDatabase.getInstance(context).deviceDao()
    
    suspend fun getAllDevices(): List<Device> {
        return deviceDao.getAllDevices()
    }
    
    suspend fun getDeviceById(id: Long): Device? {
        return deviceDao.getDeviceById(id)
    }
    
    suspend fun getDeviceByPhoneNumber(phoneNumber: String): Device? {
        return deviceDao.getDeviceByPhoneNumber(phoneNumber)
    }
    
    suspend fun insertDevice(device: Device): Long {
        return deviceDao.insertDevice(device)
    }
    
    suspend fun updateDevice(device: Device) {
        deviceDao.updateDevice(device)
    }
    
    suspend fun deleteDevice(device: Device) {
        deviceDao.deleteDevice(device)
    }
    
    suspend fun updateDeviceLocation(deviceId: Long, latitude: Double, longitude: Double, timestamp: Date) {
        deviceDao.updateDeviceLocation(deviceId, latitude, longitude, timestamp)
    }
    
    suspend fun updateDeviceOnlineStatus(deviceId: Long, isOnline: Boolean) {
        deviceDao.updateDeviceOnlineStatus(deviceId, isOnline)
    }
    
    fun getAllDevicesLive(): LiveData<List<Device>> {
        return deviceDao.getAllDevicesLive()
    }
}