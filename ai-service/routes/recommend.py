import json
import re
import time
from flask import Blueprint, request, jsonify
from services.groq_client import call_groq

recommend_bp = Blueprint('recommend', __name__)

PROMPT_TEMPLATE = """You are a cybersecurity and risk management expert. Based on the following control details, provide exactly 3 actionable recommendations to improve its effectiveness.

Control Name: {control_name}
Control Description: {control_description}
Risk Level: {risk_level}
Current Effectiveness Score: {effectiveness_score}/100

Respond ONLY with valid JSON in this exact format:
{{
    "recommendations": [
        {{
            "action_type": "IMMEDIATE|SHORT_TERM|LONG_TERM",
            "description": "Detailed recommendation",
            "priority": "HIGH|MEDIUM|LOW",
            "expected_impact": "Brief expected impact description"
        }},
        {{
            "action_type": "IMMEDIATE|SHORT_TERM|LONG_TERM",
            "description": "Detailed recommendation",
            "priority": "HIGH|MEDIUM|LOW",
            "expected_impact": "Brief expected impact description"
        }},
        {{
            "action_type": "IMMEDIATE|SHORT_TERM|LONG_TERM",
            "description": "Detailed recommendation",
            "priority": "HIGH|MEDIUM|LOW",
            "expected_impact": "Brief expected impact description"
        }}
    ],
    "generated_at": "{timestamp}"
}}"""

def sanitize_input(text):
    if text is None:
        return ""
    text = re.sub(r'<[^>]+>', '', str(text))
    patterns = [r'ignore\s+(previous|above|all)\s+instructions',
                r'forget\s+(everything|all|previous)', r'you\s+are\s+now',
                r'system\s*prompt', r'override']
    for p in patterns:
        if re.search(p, text, re.IGNORECASE):
            return None
    return text[:2000]

@recommend_bp.route('/recommend', methods=['POST'])
def recommend():
    data = request.get_json()
    if not data:
        return jsonify({"error": "Request body is required"}), 400

    control_name = sanitize_input(data.get('control_name', ''))
    control_description = sanitize_input(data.get('control_description', ''))
    risk_level = sanitize_input(data.get('risk_level', 'MEDIUM'))
    effectiveness_score = data.get('effectiveness_score', 50)

    if control_name is None or control_description is None:
        return jsonify({"error": "Potential prompt injection detected"}), 400

    if not control_name:
        return jsonify({"error": "control_name is required"}), 400

    prompt = PROMPT_TEMPLATE.format(
        control_name=control_name,
        control_description=control_description,
        risk_level=risk_level,
        effectiveness_score=effectiveness_score,
        timestamp=time.strftime('%Y-%m-%dT%H:%M:%SZ')
    )

    result = call_groq(prompt, temperature=0.3)

    if result is None:
        return jsonify({
            "recommendations": [],
            "generated_at": time.strftime('%Y-%m-%dT%H:%M:%SZ'),
            "is_fallback": True
        }), 200

    try:
        parsed = json.loads(result)
        parsed['is_fallback'] = False
        return jsonify(parsed), 200
    except json.JSONDecodeError:
        return jsonify({
            "recommendations": [],
            "raw_response": result,
            "is_fallback": False,
            "generated_at": time.strftime('%Y-%m-%dT%H:%M:%SZ')
        }), 200
