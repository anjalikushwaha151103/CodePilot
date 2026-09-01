# CodePilot Architecture Decision Records (ADRs)

> **Document Status:** Approved  
> **Project:** CodePilot (AI-Powered Coding Mentor)  
> **Date:** August 2026  

---

## Overview

This document formalizes the key architectural decisions made for the **CodePilot** platform. Each record details the context, architectural decision, rationale, trade-offs, and alternatives evaluated.

---

## Index of Architecture Decision Records

* [ADR-001: Monorepo Repository Structure](#adr-001-monorepo-repository-structure)
* [ADR-002: Modular Monolith Backend + Dedicated AI Service Topology](#adr-002-modular-monolith-backend--dedicated-ai-service-topology)
* [ADR-003: FastAPI (Python 3.12) for AI & Code Intelligence Service](#adr-003-fastapi-python-312-for-ai--code-intelligence-service)
* [ADR-004: Manifest V3 Extension with Side Panel & Shadow DOM Isolation](#adr-004-manifest-v3-extension-with-side-panel--shadow-dom-isolation)
* [ADR-005: Deterministic AST Analysis + LLM Hybrid Pipeline](#adr-005-deterministic-ast-analysis--llm-hybrid-pipeline)
* [ADR-006: Modified Bayesian Knowledge Tracing for Concept Mastery](#adr-006-modified-bayesian-knowledge-tracing-for-concept-mastery)
* [ADR-007: 5-Tier Progressive Socratic Hint Guardrail Strategy](#adr-007-5-tier-progressive-socratic-hint-guardrail-strategy)

---

## ADR-001: Monorepo Repository Structure

### Context
CodePilot comprises four distinct subsystems: Browser Extension, Spring Boot Core Backend, FastAPI AI Service, and Next.js Web Dashboard. We needed to choose between a Polyrepo layout (separate git repository for each component) and a unified Monorepo layout.

### Decision
We choose a **Unified Monorepo** layout housed within a single Git repository.

### Rationale & Benefits
* **Atomic Versioning:** Enables synchronized cross-component updates (e.g., modifying the normalized `ProblemContext` API schema updates backend DTOs, extension types, and AI Pydantic models in a single pull request).
* **Developer Experience:** Simplifies local development using Docker Compose; developers can spin up the entire ecosystem with a single command.
* **Shared Documentation & CI/CD:** Consolidates deployment pipelines and centralizes architecture documentation in `docs/architecture/`.

### Consequences & Trade-offs
* Requires careful directory-level access control and build caching in CI/CD (e.g., using Turbo / Nx / custom GitHub Actions paths to prevent rebuilding all services on isolated changes).

---

## ADR-002: Modular Monolith Backend + Dedicated AI Service Topology

### Context
We evaluated whether to construct CodePilot as:
1. A microservices cluster (Auth Service, Problem Service, Learning Service, AI Service, Recommendation Service).
2. A single monolithic backend containing all logic in one language.
3. A **Modular Monolith for Core Domain Logic** + a **Dedicated AI Microservice**.

### Decision
We select **Option 3: Modular Monolith Backend (Spring Boot 3 / Java 21) + Dedicated AI Microservice (FastAPI / Python 3.12)**.

```
+-------------------------------------------------------------+
|                 SPRING BOOT MODULAR MONOLITH                |
|  [Auth Domain] <---> [Problem Domain] <---> [Learning Domain] |
+------------------------------+------------------------------+
                               | REST / Internal Token
                               v
               +-------------------------------+
               |     FASTAPI AI SERVICE        |
               | (Static Analysis & Gemini API)|
               +-------------------------------+
```

### Rationale & Benefits
* **Avoids Overengineering:** A full microservices mesh introduces excessive operational overhead, network latency, and distributed transaction complexity for an MVP.
* **Language Best-Fit:** Java 21 offers unmatched robustness, type safety, multi-threading (Virtual Threads), and enterprise ORM capabilities for core domain logic. Python offers the richest ecosystem for compiler tools (Tree-sitter, AST manipulation) and LLM SDKs.
* **Maintainable Isolation:** Package boundaries in Spring Boot (`com.codepilot.domain.*`) keep domains decoupled, allowing future extraction into standalone microservices if traffic demands it.

---

## ADR-003: FastAPI (Python 3.12) for AI & Code Intelligence Service

### Context
While Spring Boot handles backend business logic, we required a lightweight, high-performance engine for code parsing, prompt orchestration, and LLM communication.

### Decision
We select **FastAPI with Python 3.12** for the dedicated AI Service.

### Rationale & Benefits
* **Asynchronous Concurrency:** Built-in `asyncio` support allows handling hundreds of concurrent streaming LLM HTTP requests without blocking OS threads.
* **Rich AST Ecosystem:** Native access to Tree-sitter bindings and Python's built-in `ast` module enables high-speed static code analysis.
* **Data Validation:** Pydantic provides seamless runtime data validation for LLM JSON outputs.

---

## ADR-004: Manifest V3 Extension with Side Panel & Shadow DOM Isolation

### Context
Browser extensions operate inside host pages (e.g., LeetCode, Codeforces). We needed to determine how the UI is presented and how host DOM CSS leakage is prevented.

### Decision
We adopt **Chrome Manifest V3**, utilizing the official `chrome.sidePanel` API with fallback to a **Shadow DOM Floating Drawer**.

### Rationale & Benefits
* **CSS Encapsulation:** Hosting the React extension UI inside a Shadow Root ensures host page stylesheets (e.g., LeetCode's global Tailwind or dark theme rules) cannot corrupt CodePilot UI rendering.
* **Modern Side Panel UX:** Manifest V3's side panel provides a permanent side-by-side view next to target coding editors without obstructing the problem text or code editor.
* **Compliance & Security:** Meets Chrome Web Store Manifest V3 security requirements, forbidding dynamic remote code execution in background service workers.

---

## ADR-005: Deterministic AST Analysis + LLM Hybrid Pipeline

### Context
Relying solely on LLMs for analyzing student code can lead to hallucinations, incorrect complexity estimations, missing base case detection, and high token costs.

### Decision
We implement a **Hybrid Pipeline**: Deterministic Static Code Analysis runs first, injecting objective AST findings into the prompt context before calling the LLM.

```
Student Code ---> Tree-sitter AST Parser ---> Static Report ---> LLM Prompt ---> Socratic Hint
```

### Rationale & Benefits
* **Factual Grounding:** Gives the LLM exact data (e.g., `nested_loop_depth: 3`, `has_recursive_call: true`, `base_case_present: false`) so it doesn't hallucinate code structure.
* **Latency & Cost Optimization:** Allows short-circuiting LLM calls for syntax errors or trivial static flaws, returning immediate feedback.

---

## ADR-006: Modified Bayesian Knowledge Tracing for Concept Mastery

### Context
We needed a mathematical model to represent student concept mastery (e.g., Dynamic Programming: 31%, Arrays: 82%) that dynamically updates based on problem outcomes and hint usage.

### Decision
We adopt a **Modified Bayesian Knowledge Tracing (BKT) + Decay-Weighted Elo Rating Model**.

### Rationale & Benefits
* **Pedagogical Accuracy:** Distinguishes between solving a problem independently vs. requesting 4 hint levels to reach a solution.
* **Time Decay:** Incorporates memory retention decay so inactive concepts gradually decrease in score, prompting timely review.

---

## ADR-007: 5-Tier Progressive Socratic Hint Guardrail Strategy

### Context
Generative AI tutors often jump straight to revealing complete code solutions when asked for help, destroying the learning opportunity.

### Decision
We enforce a strict **5-Tier Progressive Hint State Machine** (Level 0: Socratic Probe through Level 4: Full Code Solution), enforced via system prompt guardrails and API level parameters.

### Rationale & Benefits
* **Fosters Critical Thinking:** Prompts the student to identify their own logic gaps before giving conceptual or code-level answers.
* **Strict Control:** Level 4 (full solution) can only be accessed through an explicit secondary confirmation click by the user.

---
