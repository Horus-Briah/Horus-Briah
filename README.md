# Phone Tracker Android App

A legal and privacy-compliant Android application for tracking phone locations using Google Maps or Waze integration.

## ⚠️ Important Legal Notice

This app is designed for **legal use only**:
- Tracking your own devices
- Tracking devices you own (company phones, family devices with consent)
- Emergency situations with proper authorization

**It is illegal to track someone's phone without their explicit consent.**

## Features

- Real-time location tracking with 5-minute updates
- Google Maps and Waze integration
- Phone number-based device identification
- Secure location sharing between devices
- Companion app for target devices
- Simple backend server for data management

## Project Structure

```
phone-tracker/
├── app/                    # Main tracking app
├── companion-app/          # App that runs on target devices
├── server/                 # Backend server
├── shared/                 # Shared utilities
└── docs/                   # Documentation
```

## Technical Requirements

- Android 6.0+ (API level 23)
- Google Play Services
- Internet connection
- Location permissions
- Target device must have companion app installed

## Setup Instructions

1. Install the companion app on the target device
2. Grant location permissions
3. Enter the target phone number in the main app
4. Start tracking

## Privacy & Security

- All location data is encrypted
- User consent is required
- Data is stored securely
- Automatic data deletion after specified time
