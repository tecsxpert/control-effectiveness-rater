import json
import re
import time
from flask import Blueprint, request, jsonify
from services.groq_client import call_groq

report_bp = Blueprint('report', __name__)

PROMPT_TEMPLATE = """You are a cybersecurity and risk management expert. Generate a comprehensive effectiveness report for the following security control.

Control Name: {control_name}
Control Description: {control_description}
Category: {category}
Risk Level: {risk_level}
Effectiveness Score: {effectiveness_score}/100
Current Status: {status}

Respond ONLY with valid JSON in this exact format:
{{
    "title": "Control Effectiveness Report: [Control Name]",
    "summary": "Executive summary in 2-3 sentences",
    "overview": "Detailed overview of the control and its role in the security framework (3-5 sentences)",
    "key_findings": [
        "Finding 1",
        "Finding 2",
        "Finding 3"
    ],
    "risk_assessment": "Assessment of current risk posture based on the score and risk level",
    "recommendations": [
        {{
            "action": "Specific recommended action",
            "priority": "HIGH|MEDIUM|LOW",
            "timeline": "Suggested implementation timeline"
        }}
    ],
    "conclusion": "Final assessment and next steps",
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

@report_bp.route('/generate-report', methods=['POST'])
def generate_report():
    data = request.get_json()
    if not data:
        return jsonify({"error": "Request body is required"}), 400

    control_name = sanitize_input(data.get('control_name', ''))
    control_description = sanitize_input(data.get('control_description', ''))
    category = sanitize_input(data.get('category', ''))
    risk_level = sanitize_input(data.get('risk_level', 'MEDIUM'))
    status = sanitize_input(data.get('status', 'PENDING'))
    effectiveness_score = data.get('effectiveness_score', 50)

    if control_name is None or control_description is None:
        return jsonify({"error": "Potential prompt injection detected"}), 400

    if not control_name:
        return jsonify({"error": "control_name is required"}), 400

    prompt = PROMPT_TEMPLATE.format(
        control_name=control_name,
        control_description=control_description,
        category=category,
        risk_level=risk_level,
        effectiveness_score=effectiveness_score,
        status=status,
        timestamp=time.strftime('%Y-%m-%dT%H:%M:%SZ')
    )

    result = call_groq(prompt, temperature=0.3, max_tokens=2048)

    if result is None:
        return jsonify({
            "title": "Report Generation Failed",
            "summary": "AI service temporarily unavailable.",
            "report": "Please try again later.",
            "generated_at": time.strftime('%Y-%m-%dT%H:%M:%SZ'),
            "is_fallback": True
        }), 200

    try:
        parsed = json.loads(result)
        parsed['is_fallback'] = False
        return jsonify(parsed), 200
    except json.JSONDecodeError:
        return jsonify({
            "title": "Control Effectiveness Report",
            "raw_response": result,
            "is_fallback": False,
            "generated_at": time.strftime('%Y-%m-%dT%H:%M:%SZ')
        }), 200
