# Security Documentation

## Threats Identified
| Threat | Risk | Status |
|--------|------|--------|
| SQL Injection | HIGH | FIXED - parameterized queries |
| XSS | MEDIUM | FIXED - input sanitization |
| Unauthorized API access | HIGH | FIXED - Spring Security |
| CSRF | MEDIUM | Disabled for REST APIs |

## Tests Conducted
- SQL injection on all input fields — PASSED
- Empty input validation — PASSED
- API without token returns error — PASSED

## Residual Risks
- Rate limiting not fully implemented

## Sign-off
- Java Developer 2 - Ruchitha ✅
