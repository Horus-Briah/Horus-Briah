package com.phonetracker.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.phonetracker.app.adapter.DeviceAdapter
import com.phonetracker.app.databinding.ActivityMainBinding
import com.phonetracker.app.model.Device
import com.phonetracker.app.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var deviceAdapter: DeviceAdapter
    
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            setupLocationTracking()
        } else {
            Toast.makeText(this, "Location permissions are required", Toast.LENGTH_LONG).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViewModel()
        setupRecyclerView()
        setupUI()
        checkPermissions()
    }
    
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.devices.observe(this) { devices ->
            deviceAdapter.submitList(devices)
            updateEmptyState(devices.isEmpty())
        }
    }
    
    private fun setupRecyclerView() {
        deviceAdapter = DeviceAdapter(
            onDeviceClick = { device -> startTracking(device) },
            onDeleteClick = { device -> deleteDevice(device) }
        )
        
        binding.recyclerViewDevices.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = deviceAdapter
        }
    }
    
    private fun setupUI() {
        binding.fabAddDevice.setOnClickListener {
            startActivity(Intent(this, AddDeviceActivity::class.java))
        }
        
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshDevices()
            binding.swipeRefreshLayout.isRefreshing = false
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
        } else {
            setupLocationTracking()
        }
    }
    
    private fun setupLocationTracking() {
        viewModel.startLocationTracking()
    }
    
    private fun startTracking(device: Device) {
        val intent = Intent(this, TrackingActivity::class.java).apply {
            putExtra("device_id", device.id)
            putExtra("phone_number", device.phoneNumber)
            putExtra("device_name", device.name)
        }
        startActivity(intent)
    }
    
    private fun deleteDevice(device: Device) {
        viewModel.deleteDevice(device)
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyStateGroup.visibility = if (isEmpty) android.view.View.VISIBLE else android.view.View.GONE
        binding.recyclerViewDevices.visibility = if (isEmpty) android.view.View.GONE else android.view.View.VISIBLE
    }
    
    override fun onResume() {
        super.onResume()
        viewModel.loadDevices()
    }
}