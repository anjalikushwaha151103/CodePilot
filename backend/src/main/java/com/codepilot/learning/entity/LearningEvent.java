package com.codepilot.learning.entity;

import com.codepilot.learning.domain.Concept;
import com.codepilot.tutoring.entity.TutoringSession;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "learning_events")
public class LearningEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "session_id")
    private TutoringSession tutoringSession;

    @Enumerated(EnumType.STRING)
    @Column(name = "concept", nullable = false)
    private Concept concept;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public LearningEvent() {}

    @jakarta.persistence.PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public TutoringSession getTutoringSession() { return tutoringSession; }
    public void setTutoringSession(TutoringSession tutoringSession) { this.tutoringSession = tutoringSession; }

    public Concept getConcept() { return concept; }
    public void setConcept(Concept concept) { this.concept = concept; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
