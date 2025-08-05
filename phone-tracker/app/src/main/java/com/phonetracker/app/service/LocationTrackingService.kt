package com.phonetracker.app.service

import android.app.*
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.phonetracker.app.MainActivity
import com.phonetracker.app.R
import com.phonetracker.app.repository.DeviceRepository
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class LocationTrackingService : Service() {
    
    private val binder = LocalBinder()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var deviceRepository: DeviceRepository
    
    private var currentDeviceId: Long? = null
    private var isTracking = false
    
    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "location_tracking_channel"
        private const val LOCATION_UPDATE_INTERVAL = 5 * 60 * 1000L // 5 minutes
    }
    
    inner class LocalBinder : Binder() {
        fun getService(): LocationTrackingService = this@LocationTrackingService
    }
    
    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        deviceRepository = DeviceRepository(this)
        createLocationCallback()
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        startLocationTracking()
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
    
    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    updateDeviceLocation(location)
                }
            }
        }
    }
    
    private fun startLocationTracking() {
        if (isTracking) return
        
        isTracking = true
        
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_UPDATE_INTERVAL)
            .setMinUpdateDistanceMeters(10f)
            .setMinUpdateIntervalMillis(LOCATION_UPDATE_INTERVAL)
            .setMaxUpdateDelayMillis(LOCATION_UPDATE_INTERVAL * 2)
            .build()
        
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            // Handle permission error
        }
    }
    
    private fun stopLocationTracking() {
        if (!isTracking) return
        
        isTracking = false
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
    
    private fun updateDeviceLocation(location: Location) {
        serviceScope.launch {
            currentDeviceId?.let { deviceId ->
                deviceRepository.updateDeviceLocation(
                    deviceId,
                    location.latitude,
                    location.longitude,
                    java.util.Date()
                )
                
                // Also update online status
                deviceRepository.updateDeviceOnlineStatus(deviceId, true)
            }
        }
    }
    
    fun setCurrentDevice(deviceId: Long) {
        currentDeviceId = deviceId
    }
    
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Location Tracking",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows when location tracking is active"
        }
        
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
    
    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Phone Tracker Active")
            .setContentText("Location tracking is running")
            .setSmallIcon(R.drawable.ic_location_on)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopLocationTracking()
        serviceScope.cancel()
    }
}