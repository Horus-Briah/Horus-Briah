package com.phonetracker.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.phonetracker.app.service.LocationTrackingService

class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_QUICKBOOT_POWERON -> {
                Log.d("BootReceiver", "Device boot completed")
                
                // Check if location tracking should be started
                val prefs = context.getSharedPreferences("location_tracking", Context.MODE_PRIVATE)
                val shouldStartTracking = prefs.getBoolean("tracking_enabled", false)
                
                if (shouldStartTracking) {
                    Log.d("BootReceiver", "Starting location tracking service")
                    val serviceIntent = Intent(context, LocationTrackingService::class.java)
                    context.startForegroundService(serviceIntent)
                }
            }
        }
    }
}