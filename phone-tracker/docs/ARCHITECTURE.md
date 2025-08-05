# Phone Tracker Architecture

## System Overview

The Phone Tracker system is designed as a legal and privacy-compliant location tracking solution with three main components:

1. **Main Tracking App** (Android)
2. **Companion App** (Android)
3. **Backend Server** (Python Flask)

## Architecture Diagram

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Main App      │    │ Companion App   │    │ Backend Server  │
│                 │    │                 │    │                 │
│ • Device List   │    │ • Location      │    │ • API Endpoints │
│ • Map Display   │◄───┤   Sharing       │───►│ • Database      │
│ • Navigation    │    │ • Permissions   │    │ • Data Storage  │
│ • Call Feature  │    │ • Background    │    │ • Security      │
└─────────────────┘    │   Service       │    └─────────────────┘
                       └─────────────────┘
```

## Component Details

### 1. Main Tracking App

**Purpose**: Display and manage tracked devices

**Key Features**:
- Device list management
- Google Maps integration
- Navigation to device locations
- Call functionality
- Real-time location updates

**Architecture**:
- **MVVM Pattern**: ViewModels manage UI state
- **Room Database**: Local device storage
- **Retrofit**: API communication
- **WorkManager**: Background tasks
- **Google Maps SDK**: Map display and navigation

**Key Classes**:
- `MainActivity`: Entry point and device list
- `TrackingActivity`: Map display and device tracking
- `AddDeviceActivity`: Device addition interface
- `MainViewModel`: Device management logic
- `LocationTrackingService`: Background location service

### 2. Companion App

**Purpose**: Share device location with the tracking system

**Key Features**:
- Location permission management
- Background location sharing
- Privacy controls
- Status monitoring

**Architecture**:
- **Service-based**: Background location sharing
- **Permission handling**: Runtime permissions
- **API integration**: Location data transmission
- **Preferences**: Local settings storage

**Key Classes**:
- `MainActivity`: User interface and controls
- `LocationSharingService`: Background location service
- `LocationApiService`: API communication
- `MainViewModel`: UI state management

### 3. Backend Server

**Purpose**: Central data management and API services

**Key Features**:
- RESTful API endpoints
- Database management
- Data validation
- Security handling

**Architecture**:
- **Flask Framework**: Web application
- **SQLAlchemy**: Database ORM
- **CORS Support**: Cross-origin requests
- **Environment Configuration**: Secure settings

**Key Components**:
- `app.py`: Main application file
- `Device Model`: Database entity for devices
- `LocationUpdate Model`: Location history storage
- API endpoints for CRUD operations

## Data Flow

### Location Sharing Flow

1. **Companion App** collects location every 5 minutes
2. **LocationSharingService** sends data to backend
3. **Backend Server** stores location in database
4. **Main App** fetches location data via API
5. **TrackingActivity** displays location on map

### Device Management Flow

1. **Main App** adds device via `AddDeviceActivity`
2. Device stored in local Room database
3. **MainViewModel** manages device list
4. **DeviceAdapter** displays devices in RecyclerView
5. User taps device to start tracking

## Security Architecture

### Data Protection

- **HTTPS Communication**: All API calls use HTTPS
- **Input Validation**: Server-side data validation
- **SQL Injection Prevention**: Parameterized queries
- **XSS Protection**: Input sanitization

### Privacy Features

- **User Consent**: Explicit permission required
- **Data Retention**: Configurable retention policies
- **Opt-out Mechanism**: Users can disable sharing
- **Data Encryption**: Sensitive data encryption

### Authentication (Production)

- **API Keys**: Secure API access
- **User Authentication**: Login system
- **Session Management**: Secure sessions
- **Rate Limiting**: Prevent abuse

## Database Schema

### Device Table
```sql
CREATE TABLE devices (
    id INTEGER PRIMARY KEY,
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    last_known_latitude FLOAT,
    last_known_longitude FLOAT,
    last_location_update DATETIME,
    is_online BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);
```

### LocationUpdate Table
```sql
CREATE TABLE location_updates (
    id INTEGER PRIMARY KEY,
    device_id INTEGER NOT NULL,
    latitude FLOAT NOT NULL,
    longitude FLOAT NOT NULL,
    timestamp DATETIME NOT NULL,
    accuracy FLOAT,
    speed FLOAT,
    bearing FLOAT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (device_id) REFERENCES devices(id)
);
```

## API Endpoints

### Location Sharing
- `POST /api/location/share` - Share device location

### Device Management
- `GET /api/devices` - Get all devices
- `GET /api/devices/{id}` - Get specific device
- `GET /api/devices/{id}/location` - Get device location

### Health Check
- `GET /api/health` - Server health status

## Performance Considerations

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

## Scalability

### Horizontal Scaling

- **Load Balancers**: Distribute traffic
- **Database Clustering**: Multiple database instances
- **CDN**: Content delivery optimization
- **Microservices**: Service decomposition

### Vertical Scaling

- **Resource Optimization**: Efficient resource usage
- **Database Optimization**: Query optimization
- **Caching Layers**: Redis/memcached
- **Monitoring**: Performance tracking

## Monitoring and Logging

### Application Monitoring

- **Error Tracking**: Crash reporting
- **Performance Metrics**: Response times
- **User Analytics**: Usage patterns
- **Health Checks**: System status

### Logging Strategy

- **Structured Logging**: JSON format logs
- **Log Levels**: Debug, Info, Warning, Error
- **Log Aggregation**: Centralized logging
- **Retention Policies**: Log cleanup

## Deployment Architecture

### Development Environment

- **Local Development**: Android Studio + Python
- **Testing**: Unit and integration tests
- **Version Control**: Git repository
- **CI/CD**: Automated builds

### Production Environment

- **Cloud Hosting**: AWS/Google Cloud/Azure
- **Containerization**: Docker containers
- **Orchestration**: Kubernetes
- **Monitoring**: Prometheus + Grafana

## Future Enhancements

### Planned Features

- **Real-time Updates**: WebSocket integration
- **Geofencing**: Location-based alerts
- **Route History**: Track movement patterns
- **Multi-user Support**: Team tracking
- **Offline Support**: Local data caching

### Technical Improvements

- **GraphQL API**: Flexible data queries
- **Push Notifications**: Real-time alerts
- **Machine Learning**: Predictive analytics
- **Blockchain**: Decentralized tracking
- **IoT Integration**: Device sensors