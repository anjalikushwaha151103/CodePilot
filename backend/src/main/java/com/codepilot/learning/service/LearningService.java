package com.codepilot.learning.service;

import com.codepilot.learning.domain.Concept;
import com.codepilot.learning.domain.ConceptNormalizer;
import com.codepilot.learning.dto.*;
import com.codepilot.learning.entity.LearningEvent;
import com.codepilot.learning.event.TutoringSessionCompletedEvent;
import com.codepilot.learning.mastery.MasteryCalculator;
import com.codepilot.learning.repository.LearningEventRepository;
import com.codepilot.tutoring.entity.TutoringSession;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LearningService {

    private final LearningEventRepository learningEventRepository;
    private final ConceptNormalizer conceptNormalizer;
    private final MasteryCalculator masteryCalculator;
    private final RecommendationService recommendationService;

    public LearningService(LearningEventRepository learningEventRepository,
                           ConceptNormalizer conceptNormalizer,
                           MasteryCalculator masteryCalculator,
                           RecommendationService recommendationService) {
        this.learningEventRepository = learningEventRepository;
        this.conceptNormalizer = conceptNormalizer;
        this.masteryCalculator = masteryCalculator;
        this.recommendationService = recommendationService;
    }

    @EventListener
    @Transactional
    public void onTutoringSessionCompleted(TutoringSessionCompletedEvent event) {
        TutoringSession session = event.getSession();
        Set<Concept> concepts = conceptNormalizer.normalize(event.getRawTags(), session.getConcept());

        for (Concept concept : concepts) {
            if (concept != Concept.UNKNOWN) {
                LearningEvent le = new LearningEvent();
                le.setUserId(session.getUserId());
                le.setTutoringSession(session);
                le.setConcept(concept);
                learningEventRepository.save(le);
            }
        }
    }

    @Transactional(readOnly = true)
    public LearningProfileDto getLearningProfile(UUID userId) {
        List<LearningEvent> allEvents = learningEventRepository.findByUserIdOrderByCreatedAtAsc(userId);
        
        LearningProfileDto profile = new LearningProfileDto();
        
        if (allEvents.isEmpty()) {
            profile.setTotalSessions(0);
            profile.setConceptsTracked(0);
            profile.setAverageMastery(0.0);
            profile.setHintStatistics(new HintStatisticsDto(0.0, 0, 0));
            profile.setStrongestConcepts(Collections.emptyList());
            profile.setWeakestConcepts(Collections.emptyList());
            profile.setConceptMastery(Collections.emptyList());
            profile.setRecentActivity(Collections.emptyList());
            profile.setRecommendations(Collections.emptyList());
            return profile;
        }

        Map<Concept, List<LearningEvent>> eventsByConcept = allEvents.stream()
                .collect(Collectors.groupingBy(LearningEvent::getConcept));

        List<ConceptMasteryDto> masteries = new ArrayList<>();
        double totalMastery = 0;
        int totalReveals = 0;
        double sumHintLevel = 0;
        int highHintCount = 0;
        Set<UUID> uniqueSessions = new HashSet<>();
        List<RecentActivityDto> recentActivities = new ArrayList<>();

        for (Map.Entry<Concept, List<LearningEvent>> entry : eventsByConcept.entrySet()) {
            Concept concept = entry.getKey();
            List<LearningEvent> conceptEvents = entry.getValue();
            
            ConceptMasteryDto dto = new ConceptMasteryDto();
            dto.setConcept(concept.name());
            dto.setDisplayName(concept.getDisplayName());
            dto.setAttempts(conceptEvents.size());
            
            double score = masteryCalculator.calculateMastery(conceptEvents);
            dto.setMasteryScore(Math.round(score * 100.0) / 100.0);
            dto.setLevel(getMasteryLevel(score));
            dto.setTrend(masteryCalculator.calculateTrend(conceptEvents));
            
            int reveals = 0;
            int successful = 0;
            double hints = 0;
            
            for (int i = 0; i < conceptEvents.size(); i++) {
                LearningEvent e = conceptEvents.get(i);
                TutoringSession ts = e.getTutoringSession();
                boolean isNewSession = uniqueSessions.add(ts.getId());
                
                hints += ts.getHintLevel();
                if (isNewSession) {
                    sumHintLevel += ts.getHintLevel();
                    if (ts.getHintLevel() >= 3) {
                        highHintCount++;
                    }
                }
                
                if (ts.isSolutionRevealed()) {
                    reveals++;
                    if (isNewSession) totalReveals++;
                } else {
                    successful++;
                }

                // Calculate activity impact for the last 10 events (optimization)
                if (i >= conceptEvents.size() - 10) {
                    RecentActivityDto act = new RecentActivityDto();
                    act.setConcept(concept.name());
                    act.setDisplayName(concept.getDisplayName());
                    act.setHintLevel(ts.getHintLevel());
                    act.setDate(e.getCreatedAt());
                    List<LearningEvent> historyAtTime = conceptEvents.subList(0, i + 1);
                    act.setMasteryDelta(Math.round(masteryCalculator.calculateImpact(historyAtTime) * 100.0) / 100.0);
                    recentActivities.add(act);
                }
            }
            
            dto.setSolutionReveals(reveals);
            dto.setSuccessfulSessions(successful);
            dto.setAverageHintLevel(Math.round((hints / conceptEvents.size()) * 100.0) / 100.0);
            dto.setLastPracticedAt(conceptEvents.get(conceptEvents.size() - 1).getCreatedAt());
            
            masteries.add(dto);
            totalMastery += score;
        }

        masteries.sort(Comparator.comparing(ConceptMasteryDto::getMasteryScore).reversed());
        recentActivities.sort(Comparator.comparing(RecentActivityDto::getDate).reversed());

        profile.setTotalSessions(uniqueSessions.size());
        profile.setConceptsTracked(masteries.size());
        profile.setAverageMastery(Math.round((totalMastery / masteries.size()) * 10.0) / 10.0);
        
        double overallAvgHint = Math.round((sumHintLevel / uniqueSessions.size()) * 10.0) / 10.0;
        profile.setHintStatistics(new HintStatisticsDto(overallAvgHint, totalReveals, highHintCount));
        
        profile.setConceptMastery(masteries);
        profile.setStrongestConcepts(masteries.stream().limit(5).collect(Collectors.toList()));
        
        List<ConceptMasteryDto> weakest = new ArrayList<>(masteries);
        Collections.reverse(weakest);
        profile.setWeakestConcepts(weakest.stream().limit(5).collect(Collectors.toList()));
        
        profile.setRecentActivity(recentActivities.stream().limit(10).collect(Collectors.toList()));
        profile.setRecommendations(recommendationService.getRecommendations(masteries));

        return profile;
    }

    @Transactional(readOnly = true)
    public List<ConceptMasteryDto> getConceptMasteries(UUID userId) {
        return getLearningProfile(userId).getConceptMastery();
    }

    private String getMasteryLevel(double score) {
        if (score < 30) return "Beginner";
        if (score < 50) return "Developing";
        if (score < 70) return "Familiar";
        if (score < 85) return "Proficient";
        return "Strong";
    }
}