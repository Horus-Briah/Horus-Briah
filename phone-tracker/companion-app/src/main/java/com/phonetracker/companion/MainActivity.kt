package com.phonetracker.companion

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.phonetracker.companion.databinding.ActivityMainBinding
import com.phonetracker.companion.service.LocationSharingService
import com.phonetracker.companion.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            startLocationSharing()
        } else {
            Toast.makeText(this, "Location permissions are required for sharing", Toast.LENGTH_LONG).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViewModel()
        setupUI()
        checkPermissions()
    }
    
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        
        viewModel.isSharing.observe(this) { isSharing ->
            updateSharingStatus(isSharing)
        }
        
        viewModel.lastLocationUpdate.observe(this) { timestamp ->
            updateLastLocationTime(timestamp)
        }
    }
    
    private fun setupUI() {
        binding.toolbar.title = "Location Sharing"
        setSupportActionBar(binding.toolbar)
        
        binding.switchLocationSharing.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startLocationSharing()
            } else {
                stopLocationSharing()
            }
        }
        
        binding.btnSettings.setOnClickListener {
            // Open app settings
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = android.net.Uri.fromParts("package", packageName, null)
            startActivity(intent)
        }
        
        binding.btnPrivacy.setOnClickListener {
            // Show privacy policy or information
            Toast.makeText(this, "Privacy information will be shown here", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
        )
        
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
        
        if (permissionsToRequest.isNotEmpty()) {
            locationPermissionLauncher.launch(permissionsToRequest)
        }
    }
    
    private fun startLocationSharing() {
        viewModel.startLocationSharing()
        
        // Start the location sharing service
        val intent = Intent(this, LocationSharingService::class.java)
        startForegroundService(intent)
        
        Toast.makeText(this, "Location sharing started", Toast.LENGTH_SHORT).show()
    }
    
    private fun stopLocationSharing() {
        viewModel.stopLocationSharing()
        
        // Stop the location sharing service
        val intent = Intent(this, LocationSharingService::class.java)
        stopService(intent)
        
        Toast.makeText(this, "Location sharing stopped", Toast.LENGTH_SHORT).show()
    }
    
    private fun updateSharingStatus(isSharing: Boolean) {
        binding.switchLocationSharing.isChecked = isSharing
        binding.tvStatus.text = if (isSharing) "Sharing Location" else "Not Sharing"
        binding.tvStatus.setTextColor(
            if (isSharing) getColor(android.R.color.holo_green_dark)
            else getColor(android.R.color.holo_red_dark)
        )
    }
    
    private fun updateLastLocationTime(timestamp: String?) {
        binding.tvLastUpdate.text = timestamp ?: "Never"
    }
    
    override fun onResume() {
        super.onResume()
        viewModel.loadSharingStatus()
    }
}