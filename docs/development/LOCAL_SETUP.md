# CodePilot Local Development Setup Guide

> **Phase 1 Infrastructure & Local Developer Environment Guide**  
> **Last Updated:** August 2026

---

## 1. System Requirements

Before running CodePilot locally, ensure your environment meets the following software requirements:

| Tool | Minimum Version | Recommended | Notes |
|---|---|---|---|
| **Java JDK** | 21 | 21 | OpenJDK Temurin 21 LTS required |
| **Node.js** | 20.x | 22.x | LTS |
| **Python** | 3.12 | 3.12 | Python 3.12 required (see note below) |
| **Docker & Docker Compose** | 24.0+ | Latest | For PostgreSQL 16 & Redis 7 |
| **Git** | 2.40+ | Latest | Monorepo version control |

> [!IMPORTANT]
> **Java 21** is the canonical version specified in the architecture. The project's `pom.xml` targets `java.version=21`. JDK 17 will not satisfy the Maven compiler settings.

> [!IMPORTANT]
> **Python 3.12** is the canonical version specified in the architecture. Python 3.13 may work but is not the target. CI runs against Python 3.12.

---

## 2. Environment Configuration

1. Clone the repository and navigate to the root directory:
   ```bash
   cd CodePilot
   ```

2. Copy the environment variables template to `.env`:
   ```bash
   cp .env.example .env
   ```

3. Review default settings in `.env`:
   * **PostgreSQL:** `localhost:5432` (`codepilot` / `codepilot_user`)
   * **Redis:** `localhost:6379`
   * **Spring Boot Backend:** `http://localhost:8080`
   * **FastAPI AI Service:** `http://localhost:8000`
   * **Next.js Web App:** `http://localhost:3000`

---

## 3. Starting Local Infrastructure (PostgreSQL & Redis)

Launch the background data services using Docker Compose:

```bash
docker-compose up -d
```

Verify service health:
```bash
docker-compose ps
```

*To stop services:*
```bash
docker-compose down
```

---

## 4. Starting the Spring Boot Backend

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Run compilation and unit tests:
   ```bash
   # On Windows PowerShell / CMD:
   mvnw.cmd test

   # On Linux / macOS:
   ./mvnw test
   ```

3. Start the application:
   ```bash
   # On Windows:
   mvnw.cmd spring-boot:run

   # On Linux / macOS:
   ./mvnw spring-boot:run
   ```
   *The backend starts at `http://localhost:8080`.*

---

## 5. Starting the FastAPI AI & Code Intelligence Service

1. Navigate to the AI service directory:
   ```bash
   cd ai-service
   ```

2. Create and activate a Python virtual environment:
   ```bash
   # Windows:
   python -m venv .venv
   .venv\Scripts\activate

   # Linux / macOS:
   python3 -m venv .venv
   source .venv/bin/activate
   ```

3. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```

4. Run tests:
   ```bash
   pytest
   ```

5. Start the FastAPI development server:
   ```bash
   uvicorn app.main:app --reload --port 8000
   ```
   *The service starts at `http://localhost:8000` (OpenAPI Docs at `http://localhost:8000/docs`).*

---

## 6. Starting the Web Dashboard (Next.js)

1. Navigate to the web app directory:
   ```bash
   cd web
   ```

2. Install dependencies & build:
   ```bash
   npm install
   npm run build
   ```

3. Start dev server:
   ```bash
   npm run dev
   ```
   *Dashboard starts at `http://localhost:3000`.*

---

## 7. Loading the Browser Extension (Manifest V3)

1. Navigate to the extension directory:
   ```bash
   cd extension
   ```

2. Install dependencies & build bundle:
   ```bash
   npm install
   npm run build
   ```

3. Load as Unpacked Extension in Chrome:
   1. Open Chrome and navigate to `chrome://extensions/`.
   2. Enable **Developer mode** in the top-right corner toggle.
   3. Click **Load unpacked**.
   4. Select the `CodePilot/extension/dist` directory.
   5. Pin the **CodePilot** icon and open the Side Panel on any tab.

---

## 8. Service Health Verification Commands

| Service | Health Check Endpoint | Expected HTTP Response |
|---|---|---|
| **Backend** | `curl http://localhost:8080/api/v1/health` | `{"success":true,"message":"Backend service operational",...}` |
| **Actuator** | `curl http://localhost:8080/actuator/health` | `{"status":"UP",...}` |
| **AI Service**| `curl http://localhost:8000/health` | `{"status":"UP","service":"codepilot-ai-service",...}` |
| **Web Dashboard** | `curl http://localhost:3000` | HTTP `200 OK` (HTML) |

---

## 9. Troubleshooting

* **PowerShell Execution Policy Error on Windows:**  
  If `npm` or `.ps1` scripts fail due to execution policy, run command using `cmd /c "..."` or set execution policy:
  ```powershell
  Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
  ```
* **PostgreSQL Connection Refused:**  
  Ensure Docker Desktop is running and `docker-compose up -d` completed without port conflicts on `5432`.
* **Port 8080 already in use:**  
  Change `SERVER_PORT=8081` in your local `.env` file.

---
  
## 10. Authentication Configuration (Phase 2B)  
  
The backend now uses JWTs for authentication. The following environment variables must be defined in your .env file (copied from .env.example):  
- JWT_SECRET: A securely generated secret key (minimum 32 characters, preferably Base64 encoded).  
- JWT_EXPIRATION_SECONDS: Token expiration time in seconds (default is 3600, or 1 hour).  
- CORS_ALLOWED_ORIGINS: Comma-separated list of allowed origins (e.g. http://localhost:3000). 
