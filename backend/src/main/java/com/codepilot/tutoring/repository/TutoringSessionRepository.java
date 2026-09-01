package com.codepilot.tutoring.repository;

import com.codepilot.tutoring.entity.TutoringSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TutoringSessionRepository extends JpaRepository<TutoringSession, UUID> {
    List<TutoringSession> findByUserId(UUID userId);
    List<TutoringSession> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
