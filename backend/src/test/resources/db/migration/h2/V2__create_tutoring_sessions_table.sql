CREATE TABLE tutoring_sessions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    platform VARCHAR(50) NOT NULL,
    problem_id VARCHAR(255) NOT NULL,
    problem_title VARCHAR(255),
    language VARCHAR(50) NOT NULL,
    hint_level INT NOT NULL,
    concept VARCHAR(255),
    confidence DOUBLE PRECISION,
    solution_revealed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_tutoring_sessions_user_id ON tutoring_sessions(user_id);
CREATE INDEX idx_tutoring_sessions_problem_id ON tutoring_sessions(problem_id);
