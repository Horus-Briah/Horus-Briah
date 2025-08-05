package com.phonetracker.app

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.phonetracker.app.databinding.ActivityAddDeviceBinding
import com.phonetracker.app.viewmodel.MainViewModel

class AddDeviceActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAddDeviceBinding
    private lateinit var viewModel: MainViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViewModel()
        setupUI()
    }
    
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }
    
    private fun setupUI() {
        binding.toolbar.title = "Add Device"
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        binding.btnAddDevice.setOnClickListener {
            addDevice()
        }
        
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }
    
    private fun addDevice() {
        val phoneNumber = binding.etPhoneNumber.text.toString().trim()
        val deviceName = binding.etDeviceName.text.toString().trim()
        
        // Validate input
        if (TextUtils.isEmpty(phoneNumber)) {
            binding.etPhoneNumber.error = "Phone number is required"
            return
        }
        
        if (TextUtils.isEmpty(deviceName)) {
            binding.etDeviceName.error = "Device name is required"
            return
        }
        
        // Validate phone number format (basic validation)
        if (!isValidPhoneNumber(phoneNumber)) {
            binding.etPhoneNumber.error = "Invalid phone number format"
            return
        }
        
        // Check if device already exists
        val existingDevice = viewModel.getDeviceByPhoneNumber(phoneNumber)
        if (existingDevice != null) {
            Toast.makeText(this, "Device with this phone number already exists", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Add device
        viewModel.addDevice(phoneNumber, deviceName)
        
        Toast.makeText(this, "Device added successfully", Toast.LENGTH_SHORT).show()
        finish()
    }
    
    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        // Basic phone number validation - you can enhance this based on your requirements
        val cleanNumber = phoneNumber.replace(Regex("[^0-9+]"), "")
        return cleanNumber.length >= 10 && cleanNumber.length <= 15
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}