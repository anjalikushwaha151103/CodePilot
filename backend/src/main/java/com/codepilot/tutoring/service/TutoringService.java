package com.codepilot.tutoring.service;

import com.codepilot.tutoring.client.AiServiceClient;
import com.codepilot.tutoring.dto.TutoringRequestDto;
import com.codepilot.tutoring.dto.TutoringResponseDto;
import com.codepilot.tutoring.entity.TutoringSession;
import com.codepilot.tutoring.repository.TutoringSessionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TutoringService {

    private final AiServiceClient aiServiceClient;
    private final TutoringSessionRepository tutoringSessionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public TutoringService(AiServiceClient aiServiceClient, TutoringSessionRepository tutoringSessionRepository, ApplicationEventPublisher eventPublisher) {
        this.aiServiceClient = aiServiceClient;
        this.tutoringSessionRepository = tutoringSessionRepository;
        this.eventPublisher = eventPublisher;
    }

    public TutoringResponseDto getTutoringHint(UUID userId, TutoringRequestDto requestDto) {
        validateProgression(userId, requestDto);

        TutoringResponseDto response = aiServiceClient.getTutoringHint(requestDto);

        saveSessionAndPublishEvent(userId, requestDto, response);

        return response;
    }

    @Transactional
    protected void saveSessionAndPublishEvent(UUID userId, TutoringRequestDto requestDto, TutoringResponseDto response) {
        TutoringSession session = saveTutoringSession(userId, requestDto, response);

        eventPublisher.publishEvent(new com.codepilot.learning.event.TutoringSessionCompletedEvent(
                session, 
                requestDto.getProblemContext().getTags()
        ));
    }

    private void validateProgression(UUID userId, TutoringRequestDto requestDto) {
        int requestedLevel = requestDto.getHintLevel();
        
        if (requestedLevel <= 1) {
            return; // Level 0 and 1 are always allowed
        }

        List<TutoringSession> sessions = tutoringSessionRepository.findByUserId(userId);
        
        Optional<TutoringSession> maxSessionOpt = sessions.stream()
                .filter(s -> s.getProblemId().equals(requestDto.getProblemContext().getProblemId()))
                .max(Comparator.comparing(TutoringSession::getCreatedAt));

        int maxPreviousLevel = maxSessionOpt.map(TutoringSession::getHintLevel).orElse(0);

        if (requestedLevel > maxPreviousLevel + 1) {
            throw new IllegalArgumentException(
                    "Invalid hint progression. Requested level " + requestedLevel + 
                    ", but highest previous level for this problem was " + maxPreviousLevel + ".");
        }
    }

    private TutoringSession saveTutoringSession(UUID userId, TutoringRequestDto request, TutoringResponseDto response) {
        TutoringSession session = new TutoringSession();
        session.setUserId(userId);
        session.setPlatform(request.getProblemContext().getPlatform());
        session.setProblemId(request.getProblemContext().getProblemId());
        session.setProblemTitle(request.getProblemContext().getTitle());
        session.setLanguage(request.getLanguage());
        session.setHintLevel(response.getHintLevel());
        session.setConcept(response.getConcept());
        session.setConfidence(response.getConfidence());
        session.setSolutionRevealed(response.isShouldRevealSolution());
        
        return tutoringSessionRepository.save(session);
    }
}
