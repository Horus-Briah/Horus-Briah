package com.phonetracker.companion.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _isSharing = MutableLiveData<Boolean>()
    val isSharing: LiveData<Boolean> = _isSharing
    
    private val _lastLocationUpdate = MutableLiveData<String>()
    val lastLocationUpdate: LiveData<String> = _lastLocationUpdate
    
    init {
        loadSharingStatus()
    }
    
    fun loadSharingStatus() {
        val prefs = getApplication<Application>().getSharedPreferences("location_sharing", 0)
        _isSharing.value = prefs.getBoolean("is_sharing", false)
        _lastLocationUpdate.value = prefs.getString("last_location_time", null)
    }
    
    fun startLocationSharing() {
        viewModelScope.launch {
            val prefs = getApplication<Application>().getSharedPreferences("location_sharing", 0)
            prefs.edit()
                .putBoolean("is_sharing", true)
                .apply()
            
            _isSharing.value = true
        }
    }
    
    fun stopLocationSharing() {
        viewModelScope.launch {
            val prefs = getApplication<Application>().getSharedPreferences("location_sharing", 0)
            prefs.edit()
                .putBoolean("is_sharing", false)
                .apply()
            
            _isSharing.value = false
        }
    }
    
    fun updateLastLocationTime(timestamp: String) {
        viewModelScope.launch {
            val prefs = getApplication<Application>().getSharedPreferences("location_sharing", 0)
            prefs.edit()
                .putString("last_location_time", timestamp)
                .apply()
            
            _lastLocationUpdate.value = timestamp
        }
    }
}