package com.codepilot.tutoring.dto;

public class TutoringResponseDto {
    private int hintLevel;
    private String message;
    private String concept;
    private Double confidence;
    private boolean shouldRevealSolution;

    public int getHintLevel() {
        return hintLevel;
    }

    public void setHintLevel(int hintLevel) {
        this.hintLevel = hintLevel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public boolean isShouldRevealSolution() {
        return shouldRevealSolution;
    }

    public void setShouldRevealSolution(boolean shouldRevealSolution) {
        this.shouldRevealSolution = shouldRevealSolution;
    }
}
