package com.codepilot.learning.service;

import com.codepilot.learning.dto.ConceptMasteryDto;
import com.codepilot.learning.dto.RecommendationDto;
import com.codepilot.learning.dto.Trend;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecommendationServiceTest {

    private final RecommendationService recommendationService = new RecommendationServiceImpl();

    private ConceptMasteryDto createMastery(String concept, double score, int attempts, Trend trend) {
        ConceptMasteryDto dto = new ConceptMasteryDto();
        dto.setConcept(concept);
        dto.setDisplayName(concept);
        dto.setMasteryScore(score);
        dto.setAttempts(attempts);
        dto.setTrend(trend);
        return dto;
    }

    @Test
    void testInsufficientData() {
        ConceptMasteryDto m = createMastery("ARRAYS", 10.0, 2, Trend.NEW);
        List<RecommendationDto> recs = recommendationService.getRecommendations(List.of(m));
        
        assertEquals(1, recs.size());
        assertEquals("LOW", recs.get(0).getPriority());
    }

    @Test
    void testHighPriorityWeakness() {
        ConceptMasteryDto m = createMastery("DYNAMIC_PROGRAMMING", 30.0, 5, Trend.DECLINING);
        List<RecommendationDto> recs = recommendationService.getRecommendations(List.of(m));
        
        assertEquals(1, recs.size());
        assertEquals("HIGH", recs.get(0).getPriority());
    }

    @Test
    void testMediumPriority() {
        ConceptMasteryDto m = createMastery("STRINGS", 50.0, 5, Trend.STABLE);
        List<RecommendationDto> recs = recommendationService.getRecommendations(List.of(m));
        
        assertEquals(1, recs.size());
        assertEquals("MEDIUM", recs.get(0).getPriority());
    }

    @Test
    void testSortingByPriority() {
        ConceptMasteryDto m1 = createMastery("STRINGS", 85.0, 5, Trend.STABLE); // LOW
        ConceptMasteryDto m2 = createMastery("DYNAMIC_PROGRAMMING", 35.0, 5, Trend.STABLE); // HIGH
        ConceptMasteryDto m3 = createMastery("ARRAYS", 50.0, 5, Trend.STABLE); // MEDIUM
        
        List<RecommendationDto> recs = recommendationService.getRecommendations(List.of(m1, m2, m3));
        
        assertEquals(3, recs.size());
        assertEquals("DYNAMIC_PROGRAMMING", recs.get(0).getConcept()); // HIGH
        assertEquals("ARRAYS", recs.get(1).getConcept()); // MEDIUM
        assertEquals("STRINGS", recs.get(2).getConcept()); // LOW
    }
}
