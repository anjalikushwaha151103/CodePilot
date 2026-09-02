-- Add foreign key from tutoring_sessions to users
ALTER TABLE tutoring_sessions
    ADD CONSTRAINT fk_tutoring_sessions_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- Add foreign key from learning_events to users
ALTER TABLE learning_events
    ADD CONSTRAINT fk_learning_events_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- Add composite index for hint progression queries
CREATE INDEX idx_tutoring_user_problem_created
    ON tutoring_sessions (user_id, problem_id, created_at DESC);

-- Add index for learning events by user
CREATE INDEX idx_learning_events_user_concept
    ON learning_events (user_id, concept);
