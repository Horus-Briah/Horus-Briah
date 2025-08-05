# Phone Tracker - Build Instructions

## 🚀 Quick Start

This project contains a complete Android phone tracking solution with three components:

1. **Main Tracking App** (`app/`) - Displays tracked devices on Google Maps
2. **Companion App** (`companion-app/`) - Shares location from target devices
3. **Backend Server** (`server/`) - Flask API for data management

## 📋 Prerequisites

- **Android Studio** Arctic Fox or later
- **Python 3.8+** (for backend server)
- **Google Maps API Key**
- **Android device(s)** for testing

## 🔧 Setup Steps

### 1. Google Maps API Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable these APIs:
   - Maps SDK for Android
   - Places API
   - Geocoding API
4. Create credentials (API Key)
5. Replace `YOUR_GOOGLE_MAPS_API_KEY` in:
   - `app/src/main/AndroidManifest.xml`

### 2. Backend Server Setup

```bash
# Navigate to server directory
cd phone-tracker/server

# Create virtual environment
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate

# Install dependencies
pip install -r requirements.txt

# Create .env file
echo "DATABASE_URL=sqlite:///phone_tracker.db" > .env
echo "SECRET_KEY=your-secret-key-here" >> .env

# Run server
python app.py
```

Server will run on `http://localhost:5000`

### 3. Android Apps Setup

#### Main Tracking App

1. Open Android Studio
2. Open the `phone-tracker` project
3. Update server URL in relevant files:
   - Find `SERVER_URL` constants
   - Replace with your actual server URL
4. Build and run the app

#### Companion App

1. The companion app is in the `companion-app` module
2. Update server URL in `LocationSharingService.kt`
3. Build and install on target devices

## 🏗️ Project Structure

```
phone-tracker/
├── app/                          # Main tracking app
│   ├── src/main/
│   │   ├── java/com/phonetracker/app/
│   │   │   ├── MainActivity.kt
│   │   │   ├── TrackingActivity.kt
│   │   │   ├── AddDeviceActivity.kt
│   │   │   ├── adapter/
│   │   │   ├── database/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   ├── viewmodel/
│   │   │   └── receiver/
│   │   └── res/
│   │       ├── layout/
│   │       ├── values/
│   │       └── drawable/
├── companion-app/                 # Companion app
│   ├── src/main/
│   │   ├── java/com/phonetracker/companion/
│   │   │   ├── MainActivity.kt
│   │   │   ├── api/
│   │   │   ├── model/
│   │   │   ├── service/
│   │   │   ├── viewmodel/
│   │   │   └── receiver/
│   │   └── res/
├── server/                       # Backend server
│   ├── app.py
│   └── requirements.txt
├── docs/                         # Documentation
├── build.gradle                  # Root build file
└── settings.gradle              # Project settings
```

## 🔨 Building the Apps

### Using Android Studio

1. **Open Project**
   ```bash
   # Open Android Studio
   # File -> Open -> Select phone-tracker folder
   ```

2. **Sync Project**
   - Click "Sync Project with Gradle Files"
   - Wait for dependencies to download

3. **Build Apps**
   - **Main App**: Build -> Make Project (app module)
   - **Companion App**: Build -> Make Project (companion-app module)

### Using Command Line

```bash
# Build main app
./gradlew :app:assembleDebug

# Build companion app
./gradlew :companion-app:assembleDebug

# Build both apps
./gradlew assembleDebug
```

## 📱 Installing the Apps

### Main Tracking App

```bash
# Install on device/emulator
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Companion App

```bash
# Install on target device
adb install companion-app/build/outputs/apk/debug/companion-app-debug.apk
```

## 🧪 Testing

### 1. Backend Server Test

```bash
# Test server health
curl http://localhost:5000/api/health

# Expected response:
# {"status": "healthy", "timestamp": "..."}
```

### 2. Android Apps Test

1. **Install both apps** on test devices
2. **Grant permissions** when prompted
3. **Start companion app** on target device
4. **Enable location sharing**
5. **Add device** in main app using phone number
6. **Verify tracking** on Google Maps

## 🔍 Troubleshooting

### Common Issues

#### Build Errors

1. **Gradle Sync Failed**
   ```bash
   # Clean and rebuild
   ./gradlew clean
   ./gradlew build
   ```

2. **Missing Dependencies**
   ```bash
   # Update dependencies
   ./gradlew --refresh-dependencies
   ```

3. **Kotlin Version Issues**
   - Check `build.gradle` files for version compatibility
   - Update Kotlin plugin if needed

#### Runtime Errors

1. **Google Maps Not Loading**
   - Verify API key is correct
   - Check API is enabled in Google Cloud Console
   - Ensure billing is set up

2. **Location Not Updating**
   - Check location permissions
   - Verify internet connection
   - Check server is running

3. **App Crashes**
   - Check logcat for error details
   - Verify all permissions are granted
   - Check device compatibility

### Debug Mode

Enable debug logging:

```kotlin
// In Android apps
Log.d("PhoneTracker", "Debug message")
```

```python
# In Flask server
app.logger.debug("Debug message")
```

## 🔒 Security Notes

### Production Deployment

1. **Use HTTPS** for all API communications
2. **Implement authentication** (JWT, OAuth)
3. **Add rate limiting** to prevent abuse
4. **Use production database** (PostgreSQL)
5. **Enable ProGuard** for code obfuscation
6. **Sign apps** with release keys

### Environment Variables

```bash
# Production .env
DATABASE_URL=postgresql://user:pass@host:port/db
SECRET_KEY=your-secure-secret-key
GOOGLE_MAPS_API_KEY=your-api-key
```

## 📊 Performance Optimization

### Android Apps

- **Background Optimization**: Efficient location updates
- **Battery Management**: Minimize battery usage
- **Memory Management**: Proper resource cleanup
- **Network Efficiency**: Optimized API calls

### Backend Server

- **Database Indexing**: Optimize query performance
- **Connection Pooling**: Efficient database connections
- **Caching**: Reduce database load
- **Load Balancing**: Handle multiple requests

## 🚀 Deployment

### Backend Server (Production)

```bash
# Using Gunicorn
pip install gunicorn
gunicorn -w 4 -b 0.0.0.0:5000 app:app

# Using Docker
docker build -t phone-tracker-server .
docker run -p 5000:5000 phone-tracker-server
```

### Android Apps (Production)

1. **Generate release keys**
2. **Configure ProGuard**
3. **Update server URLs**
4. **Build release APKs**
5. **Sign and publish**

## 📞 Support

For issues and questions:

1. Check the troubleshooting section
2. Review the code comments
3. Check the logs for error details
4. Ensure all prerequisites are met
5. Verify network connectivity

## 📄 License

This project is for educational purposes. Ensure compliance with local laws and privacy regulations before use in production.