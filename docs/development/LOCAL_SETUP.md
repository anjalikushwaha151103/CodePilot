# CodePilot Local Development Setup Guide

> **Production Setup & Local Developer Environment Guide**  
> **Last Updated:** Phase 8 Production Hardening

---

## 1. System Requirements

Before running CodePilot locally, ensure your environment meets the following software requirements:

| Tool | Minimum Version | Recommended | Notes |
|---|---|---|---|
| **Java JDK** | 21 | 21 | OpenJDK Temurin 21 LTS required |
| **Node.js** | 20.x | 20.x LTS | Node 20 LTS recommended |
| **Python** | 3.12 | 3.12 | Python 3.12 required |
| **Docker & Docker Compose** | 24.0+ | Latest | For full-stack containerization |
| **Git** | 2.40+ | Latest | Monorepo version control |

> [!IMPORTANT]
> **Java 21** is the canonical version specified in the architecture. The project's `pom.xml` targets `java.version=21`.

---

## 2. Environment Configuration

1. Clone the repository and navigate to the root directory:
   ```bash
   git clone https://github.com/anjalikushwaha151103/CodePilot.git
   cd CodePilot
   ```

2. Copy the environment variables template to `.env`:
   ```bash
   cp .env.example .env
   ```

3. Configure required secrets in `.env`:
   * **`JWT_SECRET`**: Required. Generate with `openssl rand -base64 64`
   * **`LLM_PROVIDER`**: Defaults to `mock`. Set to `gemini` if using Google Gemini
   * **`GEMINI_API_KEY`**: Required only if `LLM_PROVIDER=gemini`

---

## 3. Running with Docker Compose (Recommended)

To start the entire CodePilot distributed system (PostgreSQL, Redis, Spring Boot Backend, FastAPI AI Service, Next.js Web Dashboard):

```bash
docker compose up --build
```

### Service Map

| Service | Container Name | Port | Health Endpoint |
|---|---|---|---|
| **Spring Boot Backend** | `codepilot-backend` | `8080` | `http://localhost:8080/api/v1/health` |
| **FastAPI AI Service** | `codepilot-ai-service` | `8000` | `http://localhost:8000/api/v1/health` |
| **Next.js Web App** | `codepilot-web` | `3000` | `http://localhost:3000/api/health` |
| **PostgreSQL 16** | `codepilot-postgres` | `5432` | `pg_isready` (internal) |
| **Redis 7** | `codepilot-redis` | `6379` | `redis-cli ping` (internal) |

*To stop all containers and retain data volume:*
```bash
docker compose down
```

*To stop and wipe database:*
```bash
docker compose down -v
```

---

## 4. Running Services Individually (Development Mode)

If you prefer running components on bare metal for active code iteration:

### 4.1. Start Database & Cache
```bash
docker compose up -d postgres redis
```

### 4.2. Run Backend (Spring Boot 3 / Java 21)
```bash
cd backend
# Windows:
.\mvnw.cmd spring-boot:run

# Linux / macOS:
./mvnw spring-boot:run
```
*Backend runs at `http://localhost:8080`.*

### 4.3. Run AI Service (FastAPI / Python 3.12)
```bash
cd ai-service

# Create virtual environment:
python -m venv .venv
# Windows:
.venv\Scripts\activate
# Linux/macOS:
source .venv/bin/activate

pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```
*AI service runs at `http://localhost:8000` (Swagger docs at `http://localhost:8000/docs`).*

### 4.4. Run Web Dashboard (Next.js 14)
```bash
cd web
npm install
npm run dev
```
*Web dashboard runs at `http://localhost:3000`.*

### 4.5. Build & Load Browser Extension (Manifest V3)
```bash
cd extension
npm install
npm run build
```
Load the extension in Chrome:
1. Open `chrome://extensions/`
2. Enable **Developer mode** (top right)
3. Click **Load unpacked**
4. Select `CodePilot/extension/dist`

---

## 5. Verification & Health Probes

Run health checks across all services:

```bash
# Backend liveness
curl http://localhost:8080/api/v1/health

# AI Service liveness & readiness
curl http://localhost:8000/api/v1/health
curl http://localhost:8000/api/v1/readiness

# Web Dashboard liveness
curl http://localhost:3000/api/health
```

---

## 6. Running Test Suites

```bash
# Backend (71 tests)
cd backend && ./mvnw test

# AI Service (12 tests)
cd ai-service && python -m pytest -v

# Web Dashboard (7 tests)
cd web && npm test

# Browser Extension (8 tests)
cd extension && npm test
```
