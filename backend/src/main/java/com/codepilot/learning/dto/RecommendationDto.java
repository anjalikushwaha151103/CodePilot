package com.codepilot.learning.dto;

public class RecommendationDto {
    private String concept;
    private String displayName;
    private String priority; // HIGH, MEDIUM, LOW
    private String reason;
    private String suggestedAction;

    public RecommendationDto() {}

    public String getConcept() { return concept; }
    public void setConcept(String concept) { this.concept = concept; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getSuggestedAction() { return suggestedAction; }
    public void setSuggestedAction(String suggestedAction) { this.suggestedAction = suggestedAction; }
}
