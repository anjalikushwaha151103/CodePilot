package com.codepilot.learning.dto;

import java.time.OffsetDateTime;

public class RecentActivityDto {
    private String concept;
    private String displayName;
    private int hintLevel;
    private double masteryDelta; // The impact this specific event had on mastery
    private OffsetDateTime date;

    public RecentActivityDto() {}

    public String getConcept() { return concept; }
    public void setConcept(String concept) { this.concept = concept; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public int getHintLevel() { return hintLevel; }
    public void setHintLevel(int hintLevel) { this.hintLevel = hintLevel; }

    public double getMasteryDelta() { return masteryDelta; }
    public void setMasteryDelta(double masteryDelta) { this.masteryDelta = masteryDelta; }

    public OffsetDateTime getDate() { return date; }
    public void setDate(OffsetDateTime date) { this.date = date; }
}
