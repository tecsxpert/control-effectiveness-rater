# Tool-124 — Control Effectiveness Rater


## Overview
Full-stack web application to track and manage security control effectiveness.

## Architecture
React Frontend (port 5173) → Spring Boot Backend (port 8080) → H2 Database

## Tech Stack
- Java 17 + Spring Boot 3.x
- React 18 + Vite + Tailwind CSS
- H2 In-Memory Database
- Swagger UI

## Prerequisites
- Java 17
- Node.js 18+
- Maven

## Setup

### Backend
cd backend
mvn spring-boot:run

### Frontend
cd frontend
npm install
npm run dev

## Access
- Frontend: http://localhost:5173
- Swagger: http://localhost:8080/swagger-ui.html
