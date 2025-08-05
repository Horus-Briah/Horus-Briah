# Phone Tracker Setup Guide

## Overview

This project consists of three main components:
1. **Main Tracking App** - The app that displays tracked devices on a map
2. **Companion App** - The app that runs on target devices to share location
3. **Backend Server** - Flask server to handle location data

## Prerequisites

- Android Studio Arctic Fox or later
- Python 3.8+ (for backend server)
- Google Maps API Key
- Android device(s) for testing

## Setup Instructions

### 1. Google Maps API Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable the following APIs:
   - Maps SDK for Android
   - Places API
   - Geocoding API
4. Create credentials (API Key)
5. Replace `YOUR_GOOGLE_MAPS_API_KEY` in `app/src/main/AndroidManifest.xml`

### 2. Backend Server Setup

1. Navigate to the server directory:
   ```bash
   cd phone-tracker/server
   ```

2. Create a virtual environment:
   ```bash
   python -m venv venv
   source venv/bin/activate  # On Windows: venv\Scripts\activate
   ```

3. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```

4. Create `.env` file:
   ```env
   DATABASE_URL=sqlite:///phone_tracker.db
   SECRET_KEY=your-secret-key-here
   ```

5. Run the server:
   ```bash
   python app.py
   ```

The server will run on `http://localhost:5000`

### 3. Android Apps Setup

#### Main Tracking App

1. Open Android Studio
2. Open the `phone-tracker` project
3. Update the server URL in the app:
   - Find `SERVER_URL` in relevant files
   - Replace with your actual server URL
4. Build and run the app

#### Companion App

1. The companion app is in the `companion-app` module
2. Update the server URL in `LocationSharingService.kt`
3. Build and install on target devices

## Usage

### Adding Devices to Track

1. Open the main tracking app
2. Tap the "+" button to add a device
3. Enter the phone number and device name
4. The target device must have the companion app installed

### Setting Up Location Sharing

1. Install the companion app on the target device
2. Grant location permissions when prompted
3. Toggle "Enable Location Sharing" to start sharing
4. The device will share location every 5 minutes

### Tracking Devices

1. In the main app, tap on a device in the list
2. View the device location on Google Maps
3. Use navigation buttons to open Google Maps or Waze
4. Call the device directly from the app

## Security Considerations

- All location data is transmitted over HTTPS
- User consent is required for location sharing
- Data is stored securely on the server
- Implement proper authentication for production use

## Production Deployment

### Backend Server

1. Use a production database (PostgreSQL recommended)
2. Set up proper SSL certificates
3. Configure environment variables
4. Use a production WSGI server (Gunicorn)
5. Set up monitoring and logging

### Android Apps

1. Sign the apps with release keys
2. Configure ProGuard for code obfuscation
3. Test thoroughly on different devices
4. Publish to Google Play Store

## Troubleshooting

### Common Issues

1. **Location not updating**: Check permissions and internet connection
2. **Maps not loading**: Verify Google Maps API key
3. **Server connection failed**: Check server URL and network connectivity
4. **App crashes**: Check logcat for error details

### Debug Mode

Enable debug logging in the apps to troubleshoot issues:

```kotlin
// In Android apps
Log.d("PhoneTracker", "Debug message")
```

```python
# In Flask server
app.logger.debug("Debug message")
```

## Legal Compliance

- Ensure compliance with local privacy laws
- Implement proper data retention policies
- Provide clear privacy notices
- Allow users to opt-out of tracking
- Handle data deletion requests

## Support

For issues and questions:
1. Check the troubleshooting section
2. Review the code comments
3. Check the logs for error details
4. Ensure all prerequisites are met