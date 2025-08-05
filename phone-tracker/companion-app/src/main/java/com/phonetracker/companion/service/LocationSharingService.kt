package com.phonetracker.companion.service

import android.app.*
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.telephony.TelephonyManager
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.phonetracker.companion.MainActivity
import com.phonetracker.companion.R
import com.phonetracker.companion.api.LocationApiService
import com.phonetracker.companion.model.LocationData
import kotlinx.coroutines.*
import java.util.*

class LocationSharingService : Service() {
    
    private val binder = LocalBinder()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var locationApiService: LocationApiService
    
    private var isSharing = false
    private var phoneNumber: String? = null
    
    companion object {
        private const val NOTIFICATION_ID = 2001
        private const val CHANNEL_ID = "location_sharing_channel"
        private const val LOCATION_UPDATE_INTERVAL = 5 * 60 * 1000L // 5 minutes
        private const val SERVER_URL = "https://your-server.com/api" // Replace with your server URL
    }
    
    inner class LocalBinder : Binder() {
        fun getService(): LocationSharingService = this@LocationSharingService
    }
    
    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationApiService = LocationApiService.create(SERVER_URL)
        createLocationCallback()
        createNotificationChannel()
        getPhoneNumber()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        startLocationSharing()
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
    
    private fun getPhoneNumber() {
        val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        try {
            phoneNumber = telephonyManager.line1Number
        } catch (e: SecurityException) {
            // Handle permission error
            phoneNumber = "unknown"
        }
    }
    
    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    shareLocation(location)
                }
            }
        }
    }
    
    private fun startLocationSharing() {
        if (isSharing) return
        
        isSharing = true
        
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
    
    private fun stopLocationSharing() {
        if (!isSharing) return
        
        isSharing = false
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
    
    private fun shareLocation(location: Location) {
        serviceScope.launch {
            try {
                val locationData = LocationData(
                    phoneNumber = phoneNumber ?: "unknown",
                    latitude = location.latitude,
                    longitude = location.longitude,
                    timestamp = Date(),
                    accuracy = location.accuracy,
                    speed = location.speed,
                    bearing = location.bearing
                )
                
                locationApiService.shareLocation(locationData)
                
                // Update local storage or preferences
                updateLastLocationTime()
                
            } catch (e: Exception) {
                // Handle API error
            }
        }
    }
    
    private fun updateLastLocationTime() {
        val timestamp = java.text.SimpleDateFormat("MMM dd, HH:mm:ss", Locale.getDefault())
            .format(Date())
        
        // Save to SharedPreferences or local database
        getSharedPreferences("location_sharing", MODE_PRIVATE)
            .edit()
            .putString("last_location_time", timestamp)
            .apply()
    }
    
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Location Sharing",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows when location sharing is active"
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
            .setContentTitle("Location Sharing Active")
            .setContentText("Your location is being shared")
            .setSmallIcon(R.drawable.ic_location_on)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopLocationSharing()
        serviceScope.cancel()
    }
}