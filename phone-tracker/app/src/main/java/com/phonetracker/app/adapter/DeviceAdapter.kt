package com.phonetracker.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.phonetracker.app.databinding.ItemDeviceBinding
import com.phonetracker.app.model.Device
import java.text.SimpleDateFormat
import java.util.*

class DeviceAdapter(
    private val onDeviceClick: (Device) -> Unit,
    private val onDeleteClick: (Device) -> Unit
) : ListAdapter<Device, DeviceAdapter.DeviceViewHolder>(DeviceDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val binding = ItemDeviceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DeviceViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class DeviceViewHolder(
        private val binding: ItemDeviceBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(device: Device) {
            binding.tvDeviceName.text = device.name
            binding.tvPhoneNumber.text = device.phoneNumber
            
            // Update online status
            binding.tvOnlineStatus.text = if (device.isOnline) "Online" else "Offline"
            binding.ivOnlineStatus.setImageResource(
                if (device.isOnline) android.R.drawable.presence_online
                else android.R.drawable.presence_offline
            )
            
            // Update last location time
            device.lastLocationUpdate?.let { lastUpdate ->
                val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                binding.tvLastUpdate.text = "Last update: ${dateFormat.format(lastUpdate)}"
            } ?: run {
                binding.tvLastUpdate.text = "No location data"
            }
            
            // Show location info if available
            if (device.lastKnownLatitude != null && device.lastKnownLongitude != null) {
                binding.tvLocation.text = "📍 ${device.lastKnownLatitude}, ${device.lastKnownLongitude}"
                binding.tvLocation.visibility = android.view.View.VISIBLE
            } else {
                binding.tvLocation.visibility = android.view.View.GONE
            }
            
            // Set click listeners
            binding.root.setOnClickListener {
                onDeviceClick(device)
            }
            
            binding.btnDelete.setOnClickListener {
                onDeleteClick(device)
            }
        }
    }
    
    private class DeviceDiffCallback : DiffUtil.ItemCallback<Device>() {
        override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
            return oldItem == newItem
        }
    }
}