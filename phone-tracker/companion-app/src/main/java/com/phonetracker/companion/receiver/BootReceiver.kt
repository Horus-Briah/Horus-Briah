package com.phonetracker.companion.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.phonetracker.companion.service.LocationSharingService

class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_QUICKBOOT_POWERON -> {
                Log.d("BootReceiver", "Device boot completed")
                
                // Check if location sharing should be started
                val prefs = context.getSharedPreferences("location_sharing", Context.MODE_PRIVATE)
                val shouldStartSharing = prefs.getBoolean("is_sharing", false)
                
                if (shouldStartSharing) {
                    Log.d("BootReceiver", "Starting location sharing service")
                    val serviceIntent = Intent(context, LocationSharingService::class.java)
                    context.startForegroundService(serviceIntent)
                }
            }
        }
    }
}