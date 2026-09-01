package com.codepilot.learning.repository;

import com.codepilot.learning.entity.LearningEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LearningEventRepository extends JpaRepository<LearningEvent, UUID> {
    List<LearningEvent> findByUserIdOrderByCreatedAtAsc(UUID userId);
}
