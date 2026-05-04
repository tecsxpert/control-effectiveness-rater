# Tool-124 — Control Effectiveness Rater

**An AI-powered web application that helps organizations check how well their security controls are working.**

Built as a capstone internship project by a team of 5.
Sprint: 14 April – 9 May 2026 | Demo Day: 9 May 2026

---

## What This Project Does

Every company has security controls — things like firewalls, access control policies, data encryption, and password rules. The problem is: how do you know if they are actually working?

This tool lets you:
- **Add** a security control and give it a score (0–100) based on how effective it is
- **Get AI analysis** — the AI (powered by Groq's LLaMA model) automatically reads the control details and gives you a written description, recommendations to improve it, and a full report
- **Track everything** — see which controls are pending, in progress, completed, or reviewed
- **Search and filter** — find controls by name, category, risk level, or date
- **Export to CSV** — download all your data in one click
- **See a dashboard** — get a quick overview of scores, statuses, and risk levels
- **Audit log** — every create, update, and delete action is recorded automatically

---

## My Role — Java Developer 1

I was responsible for the entire **Spring Boot backend** — the core engine that powers this application.

Here is exactly what I built:

### 1. Project Setup (Day 1)
Set up the full Spring Boot project structure — all the folders, the `pom.xml` (dependency file), and `application.yml` (configuration file). Used environment variables for all sensitive values like database password and JWT secret so no secrets are hardcoded.

### 2. JPA Entities and Repositories (Day 2)
Created the Java classes that represent database tables — `ControlEffectiveness`, `User`, and `AuditLog`. These are the objects the application works with. Also built the repository layer that handles all database queries.

### 3. Business Logic / Service Layer (Day 3)
Wrote all the business logic — input validation, error handling, and the rules for how data flows through the system. Created custom exception classes so every error returns a clear, consistent message.

### 4. REST API — All Endpoints (Day 4)
Built all the API endpoints that the frontend calls:
- `GET /api/controls/all` — get all controls (with pagination)
- `POST /api/controls/create` — create a new control
- `PUT /api/controls/{id}` — update a control
- `DELETE /api/controls/{id}` — soft delete (data is never actually removed)
- `GET /api/controls/search` — search by keyword
- `GET /api/controls/filter` — filter by status, category, risk level, date
- `GET /api/controls/stats` — dashboard statistics
- `GET /api/controls/export` — download everything as a CSV file

### 5. JWT Authentication (Day 5)
Built the entire login system using JWT (JSON Web Tokens):
- `POST /api/auth/register` — create an account
- `POST /api/auth/login` — login and get a token
- `POST /api/auth/refresh` — get a new token when the old one expires

Every protected endpoint checks for a valid token before allowing access. Admin users have extra permissions (like delete).

### 6. Redis Caching + Role-Based Access Control (Day 6)
Added Redis caching so frequently read data (like the controls list and stats) is served from memory instead of hitting the database every time. Cache refreshes automatically every 10 minutes or whenever data changes.

Also added RBAC — `USER` role can read and write, `ADMIN` role can also delete.

### 7. Email Notifications (Day 7)
Built an automated email system using JavaMailSender and Thymeleaf HTML templates:
- Every day at **8:00 AM** — sends reminders for overdue controls
- Every day at **6:00 PM** — sends alerts for controls whose review is due tomorrow

### 8. Error Handling + Unit Tests (Day 8)
Built a `@ControllerAdvice` global exception handler — every error (400, 401, 404, 500) returns the same clean JSON format so the frontend always knows what to expect.

Wrote **20 JUnit 5 unit tests** using Mockito covering every service method — 80%+ code coverage verified with JaCoCo.

### 9. Docker Compose (Day 9)
Set up the `docker-compose.yml` that starts all 5 services with one command. Every service has health checks so they only start when their dependencies are ready.

### 10. Data Seeder + README (Days 12–13)
Built a `DataSeeder` that automatically loads **30 realistic security control records** into the database on first startup — so the app is demo-ready immediately with no manual setup.

---

## How the Project is Built

Think of the project as 5 separate services that all talk to each other:

```
User (Browser)
      │
      ▼
┌─────────────┐      ┌─────────────────────┐      ┌──────────────────┐
│  Frontend   │ ───▶ │   Java Backend      │ ───▶ │   AI Service     │
│  React app  │      │   Spring Boot       │      │   Python + Flask │
│  Port: 80   │      │   Port: 8080        │      │   Port: 5000     │
└─────────────┘      └──────────┬──────────┘      └──────────────────┘
                                │
                    ┌───────────┴────────────┐
                    │                        │
             ┌──────▼──────┐        ┌───────▼──────┐
             │ PostgreSQL  │        │    Redis      │
             │  Database   │        │    Cache      │
             │  Port: 5433 │        │  Port: 6379   │
             └─────────────┘        └──────────────┘
```

### In simple terms:

| Service | What it does | Technology |
|---------|-------------|------------|
| **Frontend** | The web page the user sees and clicks on | React, Tailwind CSS |
| **Java Backend** | Handles all requests, business logic, security, database | Java 17, Spring Boot |
| **AI Service** | Takes control data and uses AI to generate analysis | Python, Flask, Groq API |
| **PostgreSQL** | The main database — stores all controls, users, audit logs | PostgreSQL 15 |
| **Redis** | Fast memory cache — stores recent results to avoid slow DB calls | Redis 7 |

### The AI part explained simply:
When you create a security control, the Java backend sends the control details to the AI service in the background. The AI service sends those details to Groq's LLaMA 3.3 70B model (a large language model, like ChatGPT but free). The model reads the control and writes back a description, 3 recommendations, and a full report. All of this is stored in the database and shown to the user.

---

## How to Run the Project

### What you need first
- **Docker Desktop** — install from docker.com and make sure it is running
- **A Groq API key** — sign up free at console.groq.com, go to API Keys, create one and copy it

### Step 1 — Set your Groq API key
Open the `.env` file in the project root and change this one line:
```
GROQ_API_KEY=your_groq_api_key_here
```
Put your actual key there. Leave everything else as it is.

### Step 2 — Start everything
Open a terminal in the project folder and run:
```bash
docker-compose up --build
```
First time takes about 5 minutes. After that it starts in under a minute.

### Step 3 — Wait for this message
```
tool124-backend | Started ControlEffectivenessRaterApplication
```
Once you see that, everything is running.

### Step 4 — Open in browser

| What | Link |
|------|------|
| Check everything is healthy | http://localhost:8080/actuator/health |
| Try all API endpoints | http://localhost:8080/swagger-ui.html |
| AI service status | http://localhost:5000/health |
| Frontend | http://localhost |

### Default login accounts

| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin123` | Admin — can do everything including delete |
| `analyst` | `analyst123` | User — can create, read, and update |

The database comes pre-loaded with 30 realistic security control records automatically.

### Stop the project
```bash
docker-compose down          # stops everything, keeps database data
docker-compose down -v       # stops everything and wipes the database
```

---

## Project Folder Structure

```
control-effectiveness-rater/
├── backend/                        ← Java Spring Boot (my responsibility)
│   ├── src/main/java/com/internship/tool/
│   │   ├── controller/             ← API endpoints
│   │   ├── service/                ← Business logic
│   │   ├── repository/             ← Database queries
│   │   ├── entity/                 ← Database table models
│   │   ├── config/                 ← Security, JWT, Redis, AOP, DataSeeder
│   │   └── exception/              ← Error handling
│   ├── src/main/resources/
│   │   ├── db/migration/           ← V1__init.sql, V2__audit_log.sql
│   │   ├── templates/              ← Email HTML template
│   │   └── application.yml         ← All configuration
│   └── pom.xml                     ← Dependencies
│
├── ai-service/                     ← Python Flask AI microservice
│   ├── routes/                     ← describe, recommend, report, health
│   ├── services/groq_client.py     ← Groq API connection
│   └── app.py                      ← Entry point
│
├── frontend/                       ← React web UI
├── docker-compose.yml              ← Starts all 5 services
├── .env.example                    ← Environment variable reference
└── README.md                       ← This file
```

---

## All API Endpoints

### Login (no token needed)
| Method | URL | What it does |
|--------|-----|--------------|
| POST | `/api/auth/register` | Create a new account |
| POST | `/api/auth/login` | Login — get back a JWT token |
| POST | `/api/auth/refresh` | Get a new token when old one expires |

### Controls (JWT token required)
| Method | URL | What it does |
|--------|-----|--------------|
| GET | `/api/controls/all` | Get all controls (10 per page) |
| GET | `/api/controls/{id}` | Get one control by its ID |
| POST | `/api/controls/create` | Create a new control (AI runs automatically) |
| PUT | `/api/controls/{id}` | Update an existing control |
| DELETE | `/api/controls/{id}` | Delete a control (admin only, soft delete) |
| GET | `/api/controls/search?q=firewall` | Search by keyword |
| GET | `/api/controls/filter` | Filter by status, category, risk level, date |
| GET | `/api/controls/stats` | Get dashboard numbers (totals, averages) |
| GET | `/api/controls/export` | Download all controls as a CSV file |
| POST | `/api/controls/{id}/ai/recommend` | Ask AI for recommendations on a control |
| POST | `/api/controls/{id}/ai/report` | Ask AI to generate a full report |

### Audit Log (admin only)
| Method | URL | What it does |
|--------|-----|--------------|
| GET | `/api/audit` | See all recorded actions |
| GET | `/api/audit/entity/{type}/{id}` | See actions for one specific record |

---

## Team

| Role | Responsibility |
|------|---------------|
| **Java Developer 1** (me) | Spring Boot backend, JWT auth, Redis caching, email notifications, Docker Compose, data seeder, unit tests |
| Java Developer 2 | Database schema (Flyway migrations), React frontend |
| AI Developer 1 | Flask setup, `/describe` and `/recommend` AI endpoints |
| AI Developer 2 | Groq API client, `/generate-report` endpoint, security review |

---

*Tool-124 — Control Effectiveness Rater | Capstone Project | Sprint: 14 April – 9 May 2026*
