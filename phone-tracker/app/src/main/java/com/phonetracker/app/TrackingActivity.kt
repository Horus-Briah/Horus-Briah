package com.phonetracker.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.phonetracker.app.databinding.ActivityTrackingBinding
import com.phonetracker.app.model.Device
import com.phonetracker.app.viewmodel.TrackingViewModel
import java.text.SimpleDateFormat
import java.util.*

class TrackingActivity : AppCompatActivity(), OnMapReadyCallback {
    
    private lateinit var binding: ActivityTrackingBinding
    private lateinit var viewModel: TrackingViewModel
    private lateinit var googleMap: GoogleMap
    
    private var deviceId: Long = 0
    private var phoneNumber: String = ""
    private var deviceName: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Get device info from intent
        deviceId = intent.getLongExtra("device_id", 0)
        phoneNumber = intent.getStringExtra("phone_number") ?: ""
        deviceName = intent.getStringExtra("device_name") ?: ""
        
        if (deviceId == 0L) {
            Toast.makeText(this, "Invalid device", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        setupViewModel()
        setupMap()
        setupUI()
    }
    
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[TrackingViewModel::class.java]
        viewModel.setDeviceId(deviceId)
        
        viewModel.device.observe(this) { device ->
            device?.let { updateDeviceInfo(it) }
        }
        
        viewModel.locationUpdates.observe(this) { location ->
            location?.let { updateMapLocation(it) }
        }
    }
    
    private fun setupMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    
    private fun setupUI() {
        binding.toolbar.title = "Tracking: $deviceName"
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        binding.btnNavigateGoogleMaps.setOnClickListener {
            navigateWithGoogleMaps()
        }
        
        binding.btnNavigateWaze.setOnClickListener {
            navigateWithWaze()
        }
        
        binding.btnCall.setOnClickListener {
            callDevice()
        }
        
        binding.btnRefresh.setOnClickListener {
            viewModel.refreshLocation()
        }
    }
    
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        
        // Set initial camera position (you can set this to a default location)
        val defaultLocation = LatLng(0.0, 0.0)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f))
    }
    
    private fun updateDeviceInfo(device: Device) {
        binding.tvDeviceName.text = device.name
        binding.tvPhoneNumber.text = device.phoneNumber
        binding.tvOnlineStatus.text = if (device.isOnline) "Online" else "Offline"
        
        device.lastLocationUpdate?.let { lastUpdate ->
            val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
            binding.tvLastUpdate.text = "Last update: ${dateFormat.format(lastUpdate)}"
        }
    }
    
    private fun updateMapLocation(location: com.phonetracker.app.model.LocationUpdate) {
        val latLng = LatLng(location.latitude, location.longitude)
        
        // Clear existing markers
        googleMap.clear()
        
        // Add new marker
        val markerOptions = MarkerOptions()
            .position(latLng)
            .title(deviceName)
            .snippet("Phone: $phoneNumber")
        
        googleMap.addMarker(markerOptions)
        
        // Move camera to location
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }
    
    private fun navigateWithGoogleMaps() {
        val device = viewModel.device.value
        device?.let {
            if (it.lastKnownLatitude != null && it.lastKnownLongitude != null) {
                val uri = "google.navigation:q=${it.lastKnownLatitude},${it.lastKnownLongitude}"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setPackage("com.google.android.apps.maps")
                
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Google Maps not installed", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No location available", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun navigateWithWaze() {
        val device = viewModel.device.value
        device?.let {
            if (it.lastKnownLatitude != null && it.lastKnownLongitude != null) {
                val uri = "waze://?ll=${it.lastKnownLatitude},${it.lastKnownLongitude}&navigate=yes"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                } else {
                    // Try to open Waze from Play Store
                    val playStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"))
                    startActivity(playStoreIntent)
                }
            } else {
                Toast.makeText(this, "No location available", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun callDevice() {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}