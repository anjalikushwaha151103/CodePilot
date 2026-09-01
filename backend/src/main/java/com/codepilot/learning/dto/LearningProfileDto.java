package com.codepilot.learning.dto;

import java.util.List;

public class LearningProfileDto {
    private int totalSessions;
    private int conceptsTracked;
    private double averageMastery;
    
    private HintStatisticsDto hintStatistics;
    
    private List<ConceptMasteryDto> strongestConcepts;
    private List<ConceptMasteryDto> weakestConcepts;
    private List<ConceptMasteryDto> conceptMastery;
    private List<RecentActivityDto> recentActivity;
    private List<RecommendationDto> recommendations;

    public int getTotalSessions() { return totalSessions; }
    public void setTotalSessions(int totalSessions) { this.totalSessions = totalSessions; }

    public int getConceptsTracked() { return conceptsTracked; }
    public void setConceptsTracked(int conceptsTracked) { this.conceptsTracked = conceptsTracked; }

    public double getAverageMastery() { return averageMastery; }
    public void setAverageMastery(double averageMastery) { this.averageMastery = averageMastery; }

    public HintStatisticsDto getHintStatistics() { return hintStatistics; }
    public void setHintStatistics(HintStatisticsDto hintStatistics) { this.hintStatistics = hintStatistics; }

    public List<ConceptMasteryDto> getStrongestConcepts() { return strongestConcepts; }
    public void setStrongestConcepts(List<ConceptMasteryDto> strongestConcepts) { this.strongestConcepts = strongestConcepts; }

    public List<ConceptMasteryDto> getWeakestConcepts() { return weakestConcepts; }
    public void setWeakestConcepts(List<ConceptMasteryDto> weakestConcepts) { this.weakestConcepts = weakestConcepts; }

    public List<ConceptMasteryDto> getConceptMastery() { return conceptMastery; }
    public void setConceptMastery(List<ConceptMasteryDto> conceptMastery) { this.conceptMastery = conceptMastery; }

    public List<RecentActivityDto> getRecentActivity() { return recentActivity; }
    public void setRecentActivity(List<RecentActivityDto> recentActivity) { this.recentActivity = recentActivity; }

    public List<RecommendationDto> getRecommendations() { return recommendations; }
    public void setRecommendations(List<RecommendationDto> recommendations) { this.recommendations = recommendations; }
}
