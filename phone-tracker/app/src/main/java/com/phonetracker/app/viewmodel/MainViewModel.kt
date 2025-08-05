package com.phonetracker.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.phonetracker.app.model.Device
import com.phonetracker.app.repository.DeviceRepository
import com.phonetracker.app.service.LocationTrackingService
import kotlinx.coroutines.launch
import java.util.Date

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val deviceRepository = DeviceRepository(application)
    private val _devices = MutableLiveData<List<Device>>()
    val devices: LiveData<List<Device>> = _devices
    
    private val _isTracking = MutableLiveData<Boolean>()
    val isTracking: LiveData<Boolean> = _isTracking
    
    init {
        loadDevices()
    }
    
    fun loadDevices() {
        viewModelScope.launch {
            val deviceList = deviceRepository.getAllDevices()
            _devices.value = deviceList
        }
    }
    
    fun refreshDevices() {
        loadDevices()
    }
    
    fun addDevice(phoneNumber: String, name: String) {
        viewModelScope.launch {
            val device = Device(
                phoneNumber = phoneNumber,
                name = name
            )
            deviceRepository.insertDevice(device)
            loadDevices()
        }
    }
    
    fun deleteDevice(device: Device) {
        viewModelScope.launch {
            deviceRepository.deleteDevice(device)
            loadDevices()
        }
    }
    
    fun startLocationTracking() {
        _isTracking.value = true
        // Start the location tracking service
        val intent = android.content.Intent(getApplication(), LocationTrackingService::class.java)
        getApplication<Application>().startForegroundService(intent)
    }
    
    fun stopLocationTracking() {
        _isTracking.value = false
        val intent = android.content.Intent(getApplication(), LocationTrackingService::class.java)
        getApplication<Application>().stopService(intent)
    }
    
    fun updateDeviceLocation(deviceId: Long, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            deviceRepository.updateDeviceLocation(deviceId, latitude, longitude, Date())
            loadDevices()
        }
    }
    
    fun getDeviceById(deviceId: Long): Device? {
        return _devices.value?.find { it.id == deviceId }
    }
    
    fun getDeviceByPhoneNumber(phoneNumber: String): Device? {
        return _devices.value?.find { it.phoneNumber == phoneNumber }
    }
}