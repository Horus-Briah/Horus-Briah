package com.phonetracker.app

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager

class PhoneTrackerApplication : Application(), Configuration.Provider {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize WorkManager
        WorkManager.initialize(
            this,
            Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.INFO)
                .build()
        )
    }
    
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
    }
}