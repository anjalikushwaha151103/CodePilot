package com.codepilot.learning.dto;

import java.time.OffsetDateTime;

public class ConceptMasteryDto {
    private String concept;
    private String displayName;
    private double masteryScore;
    private String level;
    private Trend trend;
    private int attempts;
    private int successfulSessions;
    private int solutionReveals;
    private double averageHintLevel;
    private OffsetDateTime lastPracticedAt;

    public String getConcept() { return concept; }
    public void setConcept(String concept) { this.concept = concept; }
    
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public double getMasteryScore() { return masteryScore; }
    public void setMasteryScore(double masteryScore) { this.masteryScore = masteryScore; }
    
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    
    public Trend getTrend() { return trend; }
    public void setTrend(Trend trend) { this.trend = trend; }

    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }
    
    public int getSuccessfulSessions() { return successfulSessions; }
    public void setSuccessfulSessions(int successfulSessions) { this.successfulSessions = successfulSessions; }
    
    public int getSolutionReveals() { return solutionReveals; }
    public void setSolutionReveals(int solutionReveals) { this.solutionReveals = solutionReveals; }
    
    public double getAverageHintLevel() { return averageHintLevel; }
    public void setAverageHintLevel(double averageHintLevel) { this.averageHintLevel = averageHintLevel; }
    
    public OffsetDateTime getLastPracticedAt() { return lastPracticedAt; }
    public void setLastPracticedAt(OffsetDateTime lastPracticedAt) { this.lastPracticedAt = lastPracticedAt; }
}
