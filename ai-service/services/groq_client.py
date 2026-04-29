import os
import time
import json
import logging
from groq import Groq

logger = logging.getLogger(__name__)

client = None

def get_client():
    global client
    if client is None:
        api_key = os.getenv('GROQ_API_KEY')
        if not api_key:
            logger.error("GROQ_API_KEY not set")
            return None
        client = Groq(api_key=api_key)
    return client

def call_groq(prompt, temperature=0.3, max_tokens=1024, retries=3):
    """Call Groq API with retry and backoff."""
    groq_client = get_client()
    if groq_client is None:
        return None

    for attempt in range(1, retries + 1):
        try:
            start = time.time()
            response = groq_client.chat.completions.create(
                model="llama-3.3-70b-versatile",
                messages=[{"role": "user", "content": prompt}],
                temperature=temperature,
                max_tokens=max_tokens
            )
            elapsed = time.time() - start
            logger.info(f"Groq call completed in {elapsed:.2f}s (attempt {attempt})")
            return response.choices[0].message.content
        except Exception as e:
            logger.error(f"Groq API error (attempt {attempt}/{retries}): {e}")
            if attempt < retries:
                time.sleep(2 ** attempt)

    logger.error("Groq API failed after all retries")
    return None
