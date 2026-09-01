# CodePilot — AI-Powered Coding Mentor

> **"Teach the student how to think, not just what to type."**

CodePilot is an AI-powered coding mentor delivered primarily as a browser extension that operates alongside existing competitive programming and interview preparation platforms (e.g., LeetCode, Codeforces). Rather than generating raw solutions or spoiling problem answers, CodePilot provides progressive Socratic hints, complexity analysis, bug explanations, conceptual guidance, and adaptive skill profile tracking.

---

## Architecture Overview

CodePilot uses a **Modular Monolith Backend + Dedicated AI Microservice** topology:

* **Browser Extension (`/extension`):** Manifest V3 Chrome/Chromium extension built with React, TypeScript, and Vite. Encapsulated in a Shadow DOM wrapper for complete CSS isolation.
* **Core Backend (`/backend`):** Modular Monolith built with Spring Boot 3 (Java 21), Spring Data JPA, PostgreSQL 16, and Redis 7 caching.
* **AI & Code Intelligence Service (`/ai-service`):** FastAPI microservice (Python 3.12) providing Tree-sitter AST static analysis and LLM routing (Google Gemini API with prompt guardrails).
* **Web Dashboard (`/web`):** Next.js 14 App Router application with TypeScript and Tailwind CSS for skill tracking radar charts and longitudinal analytics.

Detailed architecture specifications:
* [ARCHITECTURE.md](docs/architecture/ARCHITECTURE.md)
* [ARCHITECTURE_DECISIONS.md](docs/architecture/ARCHITECTURE_DECISIONS.md)

---

## Monorepo Directory Structure

```
CodePilot/
├── extension/             # Manifest V3 Browser Extension (React + TypeScript)
├── web/                   # Next.js 14 Web Dashboard
├── backend/               # Spring Boot 3 Java Core Backend
├── ai-service/            # FastAPI Python AI & Code Intelligence Service
├── infrastructure/        # Docker Compose, Nginx, PostgreSQL config
├── docs/
│   ├── architecture/      # Architecture Specifications & ADRs
│   └── development/       # Developer Local Setup Guide
├── .github/workflows/     # GitHub Actions CI Pipelines
├── docker-compose.yml     # Local Infrastructure Orchestration
├── .env.example           # Environment Variable Template
└── README.md
```

---

## Current Development Status

| Phase | Description | Status |
|---|---|---|
| **Phase 0** | Architecture Specification & System Blueprint | ✅ Completed |
| **Phase 1** | Monorepo Setup & Infrastructure Scaffolding | ✅ Completed |
| **Phase 2A** | Database Foundation & User Domain | ✅ Completed |
| **Phase 2B** | Authentication & Security | ✅ Completed |
| **Phase 3** | Browser Extension Foundation & Message Router | ✅ Completed |
| **Phase 4** | AI & Code Intelligence Service | ✅ Completed |
| **Phase 5** | AI Service & Static Code Intelligence | ⏳ Pending |

---

## Quick Start (Local Setup)

1. **Start Infrastructure:**
   ```bash
   docker-compose up -d
   ```

2. **Run Spring Boot Backend:**
   ```bash
   cd backend
   mvnw.cmd test               # On Windows
   mvnw.cmd spring-boot:run
   ```

3. **Run FastAPI AI Service:**
   ```bash
   cd ai-service
   python -m venv .venv
   .venv\Scripts\activate
   pip install -r requirements.txt
   pytest
   uvicorn app.main:app --port 8000
   ```

4. **Run Web Dashboard:**
   ```bash
   cd web
   npm install
   npm run build
   npm run dev
   ```

5. **Build Extension:**
   ```bash
   cd extension
   npm install
   npm run build
   ```
   *Load `extension/dist` as an unpacked extension in `chrome://extensions/`.*

Detailed instructions available in [docs/development/LOCAL_SETUP.md](docs/development/LOCAL_SETUP.md).

---

## Database Architecture

### Migration Strategy

CodePilot uses **Flyway** for database schema management:

* **Production** migrations: `backend/src/main/resources/db/migration/`
* **Test** migrations (H2-compatible): `backend/src/test/resources/db/migration/h2/`
* Hibernate is set to `validate` — it verifies entities match the schema but never generates DDL.
* Flyway runs automatically on application startup.

### Users Table Schema

```sql
CREATE TABLE users (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name           VARCHAR(255)  NOT NULL,
    email               VARCHAR(320)  NOT NULL UNIQUE,
    password_hash       VARCHAR(255)  NOT NULL,
    role                VARCHAR(20)   NOT NULL DEFAULT 'USER',
    status              VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    profile_picture_url VARCHAR(2048),
    created_at          TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);
```

**Indexes:** `idx_users_email` (email), `idx_users_status` (status)

---

## User Domain (Phase 2A)

### Enums

| Enum | Values |
|---|---|
| `Role` | `USER`, `ADMIN` |
| `UserStatus` | `ACTIVE`, `DISABLED`, `PENDING` |

### REST API Endpoints

All responses are wrapped in the standard `ApiResponse` envelope.

| Method | Endpoint | Description | Status Codes |
|---|---|---|---|
| `POST` | `/api/v1/users` | Create a new user | `201`, `400`, `409` |
| `GET` | `/api/v1/users/{id}` | Get user by UUID | `200`, `404` |
| `GET` | `/api/v1/users` | List all users | `200` |
| `PUT` | `/api/v1/users/{id}` | Update user (partial) | `200`, `400`, `404`, `409` |
| `DELETE` | `/api/v1/users/{id}` | Delete user | `200`, `404` |

> **Note:** These endpoints are temporary internal/admin endpoints. They will be protected with JWT authentication in Phase 2B.

### Running Tests

```bash
cd backend
mvnw.cmd test       # Windows
./mvnw test         # Linux/macOS
```

Test categories:
* **FlywayMigrationTest** — verifies migrations apply and `users` table exists
* **UserRepositoryTest** — JPA repository CRUD with H2
* **UserServiceTest** — unit tests with Mockito
* **UserControllerTest** — MockMvc integration tests for all endpoints and validation

---

## Authentication & Security (Phase 2B)

### Overview
CodePilot uses **Stateless JWT Authentication** with Spring Security.
- Passwords are encrypted using **BCrypt**.
- Tokens are issued upon login and must be passed as a Bearer token in the Authorization header for protected endpoints.
- Sessions are disabled (SessionCreationPolicy.STATELESS).

### Auth REST API Endpoints

All responses are wrapped in the standard ApiResponse envelope.

| Method | Endpoint | Description | Status Codes |
|---|---|---|---|
| POST | /api/v1/auth/register | Register a new user | 201, 400, 409 |
| POST | /api/v1/auth/login | Authenticate user and get JWT | 200, 400, 401 |

> **Note:** Failed login attempts return a generic 401 Unauthorized response to prevent user enumeration attacks.

### Security Configurations
- **Public Endpoints:** /api/v1/auth/**, /api/v1/health, /actuator/health
- **Protected Endpoints:** /api/v1/users/**
- **Role Requirements:** DELETE /api/v1/users/{id} requires the ADMIN role.
  
## Browser Extension Foundation (Phase 3)  
  
The Manifest V3 extension serves as the primary integration point.  
  
**Architecture:**  
- **Platform Adapters:** Detect and extract context dynamically from LeetCode and Codeforces without scraping private APIs.  
- **Normalized Model:** ProblemContext abstracts away platform-specific DOM structures.  
- **Content Script:** Uses PlatformRegistry to detect the platform and broadcast context via typed messages.  
- **Side Panel:** Provides a React-based UI isolated in a Shadow DOM to prevent CSS leaks.  
- **Background Service Worker:** Orchestrates side panel toggling and handles extension lifecycle. 
  
## AI & Code Intelligence Service (Phase 4)  
  
The FastAPI microservice performs deterministic static analysis using Tree-sitter and orchestrates LLM API calls with prompt guardrails.  
  
**Architecture:**  
- **Code Parser:** Uses Tree-sitter for C++, Java, Python, and JavaScript AST generation.  
- **AST Analyzer:** Deterministically extracts loops, nesting depth, recursion, function count, and branches.  
- **Complexity Heuristics:** Translates AST signals into O(n) estimates for LLM context.  
- **LLM Providers:** Abstracted interface supporting Gemini, OpenAI, and Mock implementations.  
- **Pedagogical Guardrails:** 5-Level Hint System (Socratic Probe to Full Solution). 


## Getting Started

For local development setup instructions, see [LOCAL_SETUP.md](docs/development/LOCAL_SETUP.md).
