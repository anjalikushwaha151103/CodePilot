-- V1: Create users table (H2-compatible version for testing)
-- CodePilot Phase 2A - User Domain Foundation

CREATE TABLE users (
    id              UUID            PRIMARY KEY,
    full_name       VARCHAR(255)    NOT NULL,
    email           VARCHAR(320)    NOT NULL,
    password_hash   VARCHAR(255)    NOT NULL,
    role            VARCHAR(20)     NOT NULL DEFAULT 'USER',
    status          VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    profile_picture_url VARCHAR(2048),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_status ON users (status);
