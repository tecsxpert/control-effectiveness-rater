import json
import re
import time
from flask import Blueprint, request, jsonify
from services.groq_client import call_groq

describe_bp = Blueprint('describe', __name__)

PROMPT_TEMPLATE = """You are a cybersecurity and risk management expert. Analyze the following security control and provide a detailed effectiveness description.

Control Name: {control_name}
Control Description: {control_description}
Category: {category}

Respond ONLY with valid JSON in this exact format:
{{
    "description": "A detailed 3-5 sentence analysis of this control's effectiveness, potential gaps, and overall security posture impact.",
    "key_strengths": ["strength1", "strength2"],
    "potential_gaps": ["gap1", "gap2"],
    "overall_assessment": "Brief one-line summary",
    "generated_at": "{timestamp}"
}}"""

def sanitize_input(text):
    if text is None:
        return ""
    text = re.sub(r'<[^>]+>', '', str(text))
    injection_patterns = [
        r'ignore\s+(previous|above|all)\s+instructions',
        r'forget\s+(everything|all|previous)',
        r'you\s+are\s+now',
        r'system\s*prompt',
        r'override',
    ]
    for pattern in injection_patterns:
        if re.search(pattern, text, re.IGNORECASE):
            return None
    return text[:2000]

@describe_bp.route('/describe', methods=['POST'])
def describe():
    data = request.get_json()
    if not data:
        return jsonify({"error": "Request body is required"}), 400

    control_name = sanitize_input(data.get('control_name', ''))
    control_description = sanitize_input(data.get('control_description', ''))
    category = sanitize_input(data.get('category', ''))

    if control_name is None or control_description is None or category is None:
        return jsonify({"error": "Potential prompt injection detected"}), 400

    if not control_name:
        return jsonify({"error": "control_name is required"}), 400

    prompt = PROMPT_TEMPLATE.format(
        control_name=control_name,
        control_description=control_description,
        category=category,
        timestamp=time.strftime('%Y-%m-%dT%H:%M:%SZ')
    )

    result = call_groq(prompt, temperature=0.3)

    if result is None:
        return jsonify({
            "description": "AI service temporarily unavailable. Please try again later.",
            "key_strengths": [],
            "potential_gaps": [],
            "overall_assessment": "Fallback response",
            "generated_at": time.strftime('%Y-%m-%dT%H:%M:%SZ'),
            "is_fallback": True
        }), 200

    try:
        parsed = json.loads(result)
        parsed['is_fallback'] = False
        return jsonify(parsed), 200
    except json.JSONDecodeError:
        return jsonify({
            "description": result,
            "is_fallback": False,
            "generated_at": time.strftime('%Y-%m-%dT%H:%M:%SZ')
        }), 200
