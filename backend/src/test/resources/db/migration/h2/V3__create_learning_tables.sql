-- V3: Create learning tables
-- CodePilot Phase 6 - Learning & Adaptive Progress Engine

CREATE TABLE learning_events (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    session_id UUID NOT NULL,
    concept VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_learning_events_session FOREIGN KEY (session_id) REFERENCES tutoring_sessions(id) ON DELETE CASCADE
);

CREATE INDEX idx_learning_events_user_id ON learning_events(user_id);
