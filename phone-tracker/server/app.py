from flask import Flask, request, jsonify
from flask_cors import CORS
from flask_sqlalchemy import SQLAlchemy
from datetime import datetime
import os
from dotenv import load_dotenv

load_dotenv()

app = Flask(__name__)
CORS(app)

# Database configuration
app.config['SQLALCHEMY_DATABASE_URI'] = os.getenv('DATABASE_URL', 'sqlite:///phone_tracker.db')
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
app.config['SECRET_KEY'] = os.getenv('SECRET_KEY', 'your-secret-key-here')

db = SQLAlchemy(app)

# Models
class Device(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    phone_number = db.Column(db.String(20), unique=True, nullable=False)
    name = db.Column(db.String(100), nullable=False)
    last_known_latitude = db.Column(db.Float)
    last_known_longitude = db.Column(db.Float)
    last_location_update = db.Column(db.DateTime)
    is_online = db.Column(db.Boolean, default=False)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    is_active = db.Column(db.Boolean, default=True)

class LocationUpdate(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    device_id = db.Column(db.Integer, db.ForeignKey('device.id'), nullable=False)
    latitude = db.Column(db.Float, nullable=False)
    longitude = db.Column(db.Float, nullable=False)
    timestamp = db.Column(db.DateTime, nullable=False)
    accuracy = db.Column(db.Float)
    speed = db.Column(db.Float)
    bearing = db.Column(db.Float)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)

# Routes
@app.route('/api/location/share', methods=['POST'])
def share_location():
    try:
        data = request.get_json()
        
        # Validate required fields
        required_fields = ['phone_number', 'latitude', 'longitude', 'timestamp']
        for field in required_fields:
            if field not in data:
                return jsonify({'success': False, 'message': f'Missing required field: {field}'}), 400
        
        phone_number = data['phone_number']
        latitude = data['latitude']
        longitude = data['longitude']
        timestamp = datetime.fromisoformat(data['timestamp'].replace('Z', '+00:00'))
        
        # Find or create device
        device = Device.query.filter_by(phone_number=phone_number).first()
        if not device:
            device = Device(
                phone_number=phone_number,
                name=f"Device {phone_number}",
                is_online=True
            )
            db.session.add(device)
            db.session.flush()  # Get the ID
        
        # Update device location
        device.last_known_latitude = latitude
        device.last_known_longitude = longitude
        device.last_location_update = timestamp
        device.is_online = True
        
        # Create location update record
        location_update = LocationUpdate(
            device_id=device.id,
            latitude=latitude,
            longitude=longitude,
            timestamp=timestamp,
            accuracy=data.get('accuracy'),
            speed=data.get('speed'),
            bearing=data.get('bearing')
        )
        
        db.session.add(location_update)
        db.session.commit()
        
        return jsonify({
            'success': True,
            'message': 'Location shared successfully',
            'timestamp': datetime.utcnow().isoformat()
        })
        
    except Exception as e:
        db.session.rollback()
        return jsonify({'success': False, 'message': str(e)}), 500

@app.route('/api/devices', methods=['GET'])
def get_devices():
    try:
        devices = Device.query.filter_by(is_active=True).all()
        device_list = []
        
        for device in devices:
            device_data = {
                'id': device.id,
                'phone_number': device.phone_number,
                'name': device.name,
                'last_known_latitude': device.last_known_latitude,
                'last_known_longitude': device.last_known_longitude,
                'last_location_update': device.last_location_update.isoformat() if device.last_location_update else None,
                'is_online': device.is_online,
                'created_at': device.created_at.isoformat()
            }
            device_list.append(device_data)
        
        return jsonify({'success': True, 'devices': device_list})
        
    except Exception as e:
        return jsonify({'success': False, 'message': str(e)}), 500

@app.route('/api/devices/<int:device_id>', methods=['GET'])
def get_device(device_id):
    try:
        device = Device.query.get_or_404(device_id)
        
        device_data = {
            'id': device.id,
            'phone_number': device.phone_number,
            'name': device.name,
            'last_known_latitude': device.last_known_latitude,
            'last_known_longitude': device.last_known_longitude,
            'last_location_update': device.last_location_update.isoformat() if device.last_location_update else None,
            'is_online': device.is_online,
            'created_at': device.created_at.isoformat()
        }
        
        return jsonify({'success': True, 'device': device_data})
        
    except Exception as e:
        return jsonify({'success': False, 'message': str(e)}), 500

@app.route('/api/devices/<int:device_id>/location', methods=['GET'])
def get_device_location(device_id):
    try:
        device = Device.query.get_or_404(device_id)
        
        if not device.last_known_latitude or not device.last_known_longitude:
            return jsonify({'success': False, 'message': 'No location data available'}), 404
        
        location_data = {
            'device_id': device.id,
            'phone_number': device.phone_number,
            'latitude': device.last_known_latitude,
            'longitude': device.last_known_longitude,
            'timestamp': device.last_location_update.isoformat() if device.last_location_update else None,
            'is_online': device.is_online
        }
        
        return jsonify({'success': True, 'location': location_data})
        
    except Exception as e:
        return jsonify({'success': False, 'message': str(e)}), 500

@app.route('/api/health', methods=['GET'])
def health_check():
    return jsonify({'status': 'healthy', 'timestamp': datetime.utcnow().isoformat()})

# Create database tables
with app.app_context():
    db.create_all()

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)