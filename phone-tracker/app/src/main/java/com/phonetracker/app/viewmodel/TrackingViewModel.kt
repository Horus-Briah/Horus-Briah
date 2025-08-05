package com.phonetracker.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.phonetracker.app.model.Device
import com.phonetracker.app.model.LocationUpdate
import com.phonetracker.app.repository.DeviceRepository
import kotlinx.coroutines.launch
import java.util.Date

class TrackingViewModel(application: Application) : AndroidViewModel(application) {
    
    private val deviceRepository = DeviceRepository(application)
    private val _device = MutableLiveData<Device>()
    val device: LiveData<Device> = _device
    
    private val _locationUpdates = MutableLiveData<LocationUpdate>()
    val locationUpdates: LiveData<LocationUpdate> = _locationUpdates
    
    private var currentDeviceId: Long = 0
    
    fun setDeviceId(deviceId: Long) {
        currentDeviceId = deviceId
        loadDevice()
    }
    
    private fun loadDevice() {
        viewModelScope.launch {
            val device = deviceRepository.getDeviceById(currentDeviceId)
            device?.let {
                _device.value = it
                
                // If device has location, create a location update
                if (it.lastKnownLatitude != null && it.lastKnownLongitude != null) {
                    val locationUpdate = LocationUpdate(
                        deviceId = it.id,
                        latitude = it.lastKnownLatitude,
                        longitude = it.lastKnownLongitude,
                        timestamp = it.lastLocationUpdate ?: Date()
                    )
                    _locationUpdates.value = locationUpdate
                }
            }
        }
    }
    
    fun refreshLocation() {
        loadDevice()
    }
    
    fun updateLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            deviceRepository.updateDeviceLocation(currentDeviceId, latitude, longitude, Date())
            
            val locationUpdate = LocationUpdate(
                deviceId = currentDeviceId,
                latitude = latitude,
                longitude = longitude,
                timestamp = Date()
            )
            _locationUpdates.value = locationUpdate
            
            loadDevice()
        }
    }
    
    fun updateOnlineStatus(isOnline: Boolean) {
        viewModelScope.launch {
            deviceRepository.updateDeviceOnlineStatus(currentDeviceId, isOnline)
            loadDevice()
        }
    }
}