import time
from flask import Blueprint, jsonify, current_app

health_bp = Blueprint('health', __name__)

@health_bp.route('/health', methods=['GET'])
def health():
    uptime = time.time() - current_app.config.get('START_TIME', time.time())
    return jsonify({
        "status": "healthy",
        "model": "llama-3.3-70b-versatile",
        "provider": "Groq",
        "uptime_seconds": round(uptime, 2),
        "version": "1.0.0"
    }), 200
