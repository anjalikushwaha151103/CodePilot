# CodePilot — AI-Powered Coding Mentor

> **"Teach the student how to think, not just what to type."**

CodePilot is an AI-powered coding tutor delivered as a Chrome browser extension that works alongside competitive programming platforms like **LeetCode** and **Codeforces**. Instead of generating solutions, CodePilot provides progressive Socratic hints, code complexity analysis, conceptual guidance, and adaptive skill tracking — helping students learn to solve problems independently.

---

## Architecture

```
┌────────────────────┐     ┌────────────────────┐     ┌────────────────────┐
│  Browser Extension │     │   Web Dashboard    │     │   Chrome Browser   │
│  (Manifest V3)     │     │   (Next.js 14)     │     │                    │
│  React + TypeScript│     │   Tailwind CSS     │     │  LeetCode /        │
│  + Vite + Shadow   │     │                    │     │  Codeforces        │
│  DOM Isolation     │     │                    │     │                    │
└────────┬───────────┘     └────────┬───────────┘     └────────────────────┘
         │ REST + JWT               │ REST + JWT
         ▼                          ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                      Spring Boot 3 Backend (Java 21)                    │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐ │
│  │   Auth   │  │ Tutoring │  │ Learning │  │   User   │  │  Health  │ │
│  │  Module  │  │  Module  │  │  Engine  │  │  Module  │  │ /Config  │ │
│  └──────────┘  └────┬─────┘  └──────────┘  └──────────┘  └──────────┘ │
│                     │ REST                                             │
│  ┌──────────────────┴──────────────────────────────────────┐           │
│  │  PostgreSQL 16 (Flyway)          Redis 7 (optional)     │           │
│  └─────────────────────────────────────────────────────────┘           │
└─────────────────────────────┬────────────────────────────────────────┘
                              │ REST
                              ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                   FastAPI AI Service (Python 3.12)                       │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐               │
│  │Tree-sitter│  │   AST    │  │Complexity│  │   LLM    │               │
│  │  Parser  │  │ Analyzer │  │Heuristics│  │ Provider │               │
│  └──────────┘  └──────────┘  └──────────┘  │(Gemini/  │               │
│                                             │ Mock)    │               │
│  ┌──────────────────────────────────────┐  └──────────┘               │
│  │  5-Level Pedagogical Guardrails      │                              │
│  │  (Socratic Probe → Full Solution)    │                              │
│  └──────────────────────────────────────┘                              │
└──────────────────────────────────────────────────────────────────────────┘
```

### Technology Stack

| Component | Technology |
|---|---|
| **Backend** | Spring Boot 3.3, Java 21, Spring Security, Spring Data JPA |
| **Database** | PostgreSQL 16, Flyway migrations |
| **Cache** | Redis 7 (optional, graceful degradation) |
| **AI Service** | FastAPI, Python 3.12, Tree-sitter, Google Gemini API |
| **Web Dashboard** | Next.js 14, React 18, TypeScript, Tailwind CSS |
| **Browser Extension** | Manifest V3, React, TypeScript, Vite, Shadow DOM |
| **Authentication** | JWT (HMAC-SHA256), BCrypt password hashing |
| **CI/CD** | GitHub Actions |
| **Containerization** | Docker, Docker Compose |

---

## Features

### Tutoring System
- **Progressive Hint System** — 5 levels from Socratic questioning to full solution reveal
- **Platform Detection** — Automatically detects problems on LeetCode and Codeforces
- **Static Code Analysis** — Tree-sitter AST parsing for Python, Java, C++, and JavaScript
- **Complexity Estimation** — Algorithmic complexity heuristics based on code structure
- **Prompt Guardrails** — Prevents the AI from revealing answers prematurely

### Learning Engine
- **EWMA Mastery Tracking** — Exponentially weighted moving average of concept mastery scores
- **Concept Normalization** — Maps raw AI-generated tags to a standardized concept taxonomy
- **Trend Detection** — Tracks whether a student is improving, stable, or declining per concept
- **Personalized Recommendations** — Deterministic rule engine suggesting what to practice next

### Web Dashboard
- **Mastery Overview** — Visual progress bars for each tracked concept
- **Recent Activity** — Session history with mastery impact metrics
- **Recommendations** — Priority-sorted practice suggestions based on performance data

### Security
- **Stateless JWT Authentication** — No server-side sessions
- **BCrypt Password Hashing** — Industry-standard password security
- **Role-Based Access Control** — USER and ADMIN roles
- **Input Validation** — Size limits on code, problem descriptions, and all request fields
- **CORS Configuration** — Environment-configurable allowed origins
- **Request Correlation** — X-Request-ID header for request tracing

---

## Quick Start with Docker Compose

### Prerequisites
- [Docker](https://docs.docker.com/get-docker/) and Docker Compose
- [Git](https://git-scm.com/)

### 1. Clone and Configure

```bash
git clone https://github.com/anjalikushwaha151103/CodePilot.git
cd CodePilot
cp .env.example .env
```

Edit `.env` and set `JWT_SECRET` (required):
```bash
# Generate a secure secret:
openssl rand -base64 64
```

### 2. Start the Full Stack

```bash
docker compose up --build
```

This starts all services:

| Service | URL | Description |
|---|---|---|
| **Backend API** | http://localhost:8080 | Spring Boot REST API |
| **AI Service** | http://localhost:8000 | FastAPI AI & code analysis |
| **Web Dashboard** | http://localhost:3000 | Next.js learning dashboard |
| **PostgreSQL** | localhost:5432 (dev only) | Database |
| **Redis** | localhost:6379 (dev only) | Cache (optional) |

### 3. Verify Health

```bash
curl http://localhost:8080/api/v1/health   # Backend
curl http://localhost:8000/api/v1/health   # AI Service
curl http://localhost:3000/api/health      # Web Dashboard
```

### 4. Build the Browser Extension

```bash
cd extension
npm install
npm run build
```

Then load `extension/dist` as an unpacked extension in `chrome://extensions/` (Developer Mode enabled).

### 5. Use CodePilot

1. Open a problem on [LeetCode](https://leetcode.com) or [Codeforces](https://codeforces.com)
2. Click the CodePilot extension icon to open the side panel
3. Sign in (or register via the API: `POST /api/v1/auth/register`)
4. Paste your code and click **"Ask CodePilot"**
5. View your learning progress at http://localhost:3000/dashboard

---

## Local Development (Without Docker)

For running individual services without Docker, see [LOCAL_SETUP.md](docs/development/LOCAL_SETUP.md).

---

## Project Structure

```
CodePilot/
├── backend/               # Spring Boot 3 (Java 21)
│   ├── src/main/java/     #   Application source
│   ├── src/main/resources/ #   Config, Flyway migrations
│   ├── src/test/          #   Unit & integration tests
│   ├── Dockerfile         #   Multi-stage Docker build
│   └── pom.xml
├── ai-service/            # FastAPI (Python 3.12)
│   ├── app/               #   Application modules
│   ├── tests/             #   Pytest test suite
│   ├── Dockerfile         #   Docker build
│   └── requirements.txt
├── web/                   # Next.js 14 Dashboard
│   ├── src/               #   App Router pages & components
│   ├── __tests__/         #   Jest/RTL tests
│   ├── Dockerfile         #   Standalone Docker build
│   └── package.json
├── extension/             # Chrome Extension (Manifest V3)
│   ├── src/               #   Extension source
│   ├── tests/             #   Vitest tests
│   └── package.json
├── infrastructure/        # Nginx config, PostgreSQL init
├── docs/                  # Architecture docs & setup guide
├── .github/workflows/     # CI pipeline
├── docker-compose.yml     # Full-stack orchestration
├── .env.example           # Environment template
└── README.md
```

---

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/auth/register` | Register new user |
| `POST` | `/api/v1/auth/login` | Login and get JWT |

### Tutoring
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/api/v1/tutoring/hint` | JWT | Request a progressive hint |

### Learning Profile
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/api/v1/learning/profile` | JWT | Full learning profile |
| `GET` | `/api/v1/learning/weak-areas` | JWT | Concepts needing practice |
| `GET` | `/api/v1/learning/strong-areas` | JWT | Mastered concepts |

### Health
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/v1/health` | Backend liveness |
| `GET` | `http://localhost:8000/api/v1/health` | AI service liveness |
| `GET` | `http://localhost:3000/api/health` | Web dashboard liveness |

---

## Testing

```bash
# Backend (Java 21 / JUnit 5)
cd backend && ./mvnw clean test

# AI Service (Python 3.12 / pytest)
cd ai-service && python -m pytest -v

# Web Dashboard (Jest / React Testing Library)
cd web && npm test

# Browser Extension (Vitest)
cd extension && npm test
```

---

## CI/CD

GitHub Actions runs on every push/PR to `main` and `develop`:

- **backend-ci** — Maven compile + full test suite (H2 in-memory DB)
- **ai-service-ci** — pip install + pytest (mock LLM provider)
- **web-ci** — TypeScript check + Next.js build + Jest tests
- **extension-ci** — TypeScript check + Vite build + Vitest
- **docker-validate** — Docker Compose configuration validation

No external services (PostgreSQL, Redis, Gemini API) are required in CI.

---

## Environment Variables

See [`.env.example`](.env.example) for the complete list. Key variables:

| Variable | Required | Description |
|---|---|---|
| `JWT_SECRET` | **Yes** | HMAC-SHA256 signing key (≥32 chars) |
| `POSTGRES_PASSWORD` | Yes | Database password |
| `LLM_PROVIDER` | No | `mock` (default) or `gemini` |
| `GEMINI_API_KEY` | If gemini | Google Gemini API key |
| `NEXT_PUBLIC_API_URL` | No | Backend URL for dashboard (default: `http://localhost:8080`) |
| `CORS_ALLOWED_ORIGINS` | No | Comma-separated allowed origins |

---

## Security Considerations

- JWT secrets are **never** hardcoded — always loaded from environment variables
- Passwords are hashed with **BCrypt** before storage
- Failed login attempts return generic errors to prevent user enumeration
- CORS origins are configurable and never use wildcard in production
- Error responses never expose stack traces, database errors, or internal URLs
- Student code is **not** stored in the database
- AI prompts include injection defense guardrails
- Request correlation IDs (`X-Request-ID`) enable safe debugging without exposing internals
- All request payloads have configurable size limits

---

## Current Limitations

- **No automatic code extraction** — Students paste code manually (Monaco/Ace integration is a planned future phase)
- **No code execution** — CodePilot analyzes code statically; it does not run student code
- **Single LLM provider** — Only Gemini and Mock are implemented; OpenAI support is stubbed
- **No OAuth/social login** — Only email/password authentication
- **No HTTPS in development** — TLS termination is handled by the deployment environment
- **Browser extension is Chrome-only** — Firefox/Safari support not yet implemented

---

## Roadmap

- [ ] Automatic code extraction from Monaco/Ace editors
- [ ] Code execution sandbox
- [ ] OAuth 2.0 social login (Google, GitHub)
- [ ] OpenAI provider implementation
- [ ] Kubernetes deployment manifests
- [ ] Prometheus/Grafana observability stack
- [ ] Rate limiting on authentication endpoints

---

## Documentation

- [Architecture Specification](docs/architecture/ARCHITECTURE.md)
- [Architecture Decision Records](docs/architecture/ARCHITECTURE_DECISIONS.md)
- [Local Development Setup](docs/development/LOCAL_SETUP.md)

---

## License

This project is part of an academic portfolio. All rights reserved.
