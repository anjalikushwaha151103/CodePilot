from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.core.config import settings
from app.core.logging import setup_logging
from app.api.health import router as health_router
from app.api.analyze import router as analyze_router
from app.api.tutor import router as tutor_router

setup_logging()

app = FastAPI(
    title=settings.PROJECT_NAME,
    version=settings.VERSION,
    openapi_url=f"{settings.API_V1_STR}/openapi.json"
)

import os

# CORS Middleware baseline setup
cors_origins = os.environ.get("CORS_ALLOWED_ORIGINS", "http://localhost:8080,http://localhost:3000").split(",")
app.add_middleware(
    CORSMiddleware,
    allow_origins=cors_origins,
    allow_credentials=True,
    allow_methods=["GET", "POST"],
    allow_headers=["Content-Type", "Authorization"],
)

# Include Routers
app.include_router(health_router, prefix=settings.API_V1_STR, tags=["Health"])
app.include_router(analyze_router, prefix=settings.API_V1_STR, tags=["Code Analysis"])
app.include_router(tutor_router, prefix=settings.API_V1_STR, tags=["Tutoring"])

@app.get("/")
async def root():
    return {
        "service": settings.PROJECT_NAME,
        "version": settings.VERSION,
        "status": "OPERATIONAL",
        "docs": "/docs"
    }
