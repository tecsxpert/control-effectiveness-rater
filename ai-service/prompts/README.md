# Prompt Templates Reference
# ===========================
# All prompt templates are embedded in their respective route files:
# - routes/describe.py   → /describe endpoint
# - routes/recommend.py  → /recommend endpoint
# - routes/report.py     → /generate-report endpoint
#
# Key Parameters:
# - model: llama-3.3-70b-versatile
# - temperature: 0.3 (factual/consistent)
# - max_tokens: 1024 (describe/recommend), 2048 (report)
#
# All prompts enforce JSON-only output format.
# All inputs are sanitized for HTML and prompt injection.
