# Tool-124 — Control Effectiveness Rater

> AI-powered web application for rating and analyzing the effectiveness of security controls. Built as a capstone project using an industry-standard tech stack.

---

## Architecture

```
┌──────────────┐     ┌──────────────────┐     ┌──────────────┐
│   Frontend   │────▶│   Backend (Java) │────▶│  AI Service  │
│  React + Vite│     │ Spring Boot 3.x  │     │  Flask + Groq│
│   Port: 80   │     │   Port: 8080     │     │  Port: 5000  │
└──────────────┘     └────────┬─────────┘     └──────────────┘
                              │
                    ┌─────────┴─────────┐
                    │                   │
              ┌─────▼─────┐     ┌──────▼──────┐
              │ PostgreSQL │     │    Redis    │
              │  Port:5432 │     │  Port:6379  │
              └───────────┘     └─────────────┘
```

## Tech Stack

| Layer     | Technology                         |
|-----------|-----------------------------------|
| Frontend  | React 18 + Vite, Tailwind CSS, Axios |
| Backend   | Java 17, Spring Boot 3.x, Spring Security, JWT |
| Database  | PostgreSQL 15, Flyway migrations   |
| Cache     | Redis 7                            |
| AI        | Python 3.11, Flask, Groq API (LLaMA-3.3-70b) |
| DevOps    | Docker + Docker Compose            |

## Prerequisites

- Docker & Docker Compose installed
- Git
- (Optional) Java 17, Node.js 18+, Python 3.11 for local dev

## Quick Start

```bash
# 1. Clone the repository
git clone <repository-url>
cd control-effectiveness-rater

# 2. Create environment file
cp .env.example .env
# Edit .env with your values (especially GROQ_API_KEY)

# 3. Start all services
docker-compose up --build

# 4. Access the application
# Frontend:  http://localhost
# Backend:   http://localhost:8080
# Swagger:   http://localhost:8080/swagger-ui.html
# AI Health: http://localhost:5000/health
```

## Default Credentials

| Username | Password   | Role  |
|----------|-----------|-------|
| admin    | admin123  | ADMIN |
| analyst  | analyst123| USER  |

## Environment Variables

| Variable          | Description                  | Default               |
|-------------------|-----------------------------|-----------------------|
| DB_HOST           | PostgreSQL host              | localhost             |
| DB_PORT           | PostgreSQL port              | 5432                  |
| DB_NAME           | Database name                | tool124_db            |
| DB_USERNAME       | Database user                | postgres              |
| DB_PASSWORD       | Database password            | postgres              |
| REDIS_HOST        | Redis host                   | localhost             |
| REDIS_PORT        | Redis port                   | 6379                  |
| JWT_SECRET        | JWT signing secret (min 256 bits) | (default provided) |
| JWT_EXPIRATION    | Access token TTL (ms)        | 86400000 (24h)        |
| GROQ_API_KEY      | Groq API key from console.groq.com | (required)       |
| AI_SERVICE_URL    | AI microservice URL          | http://localhost:5000  |
| MAIL_HOST         | SMTP host                    | smtp.gmail.com        |
| MAIL_PORT         | SMTP port                    | 587                   |
| MAIL_USERNAME     | SMTP username                |                       |
| MAIL_PASSWORD     | SMTP password / app password |                       |
| MAIL_FROM         | Sender email address         | noreply@tool124.com   |

## API Endpoints

### Authentication
- `POST /api/auth/register` — Register new user
- `POST /api/auth/login` — Login and get JWT tokens
- `POST /api/auth/refresh` — Refresh access token

### Controls (require JWT)
- `GET /api/controls/all` — List all (paginated)
- `GET /api/controls/{id}` — Get by ID
- `POST /api/controls/create` — Create new control
- `PUT /api/controls/{id}` — Update
- `DELETE /api/controls/{id}` — Soft delete (ADMIN only)
- `GET /api/controls/search?q=` — Search
- `GET /api/controls/filter` — Filter by status, category, risk, date
- `GET /api/controls/stats` — Dashboard statistics
- `GET /api/controls/export` — CSV export
- `POST /api/controls/{id}/ai/recommend` — AI recommendations
- `POST /api/controls/{id}/ai/report` — AI report generation

### Audit Log
- `GET /api/audit` — All audit logs (ADMIN)
- `GET /api/audit/entity/{type}/{id}` — Logs for specific entity

## Team

| Role            | Responsibilities                              |
|-----------------|-----------------------------------------------|
| Java Dev 1      | Spring Boot, JWT, Redis, Docker, README, Demo |
| Java Dev 2      | DB schema, React frontend, email, demo video  |
| AI Dev 1        | Flask, prompts, /describe, /recommend         |
| AI Dev 2        | GroqClient, /generate-report, security review |
| Security Reviewer | Security testing, SECURITY.md               |

## Sprint

Monday 14 April – Friday 9 May 2026 (20 working days)
Demo Day: Friday 9 May 2026

---

*Tool-124 — Control Effectiveness Rater | Capstone Project*
