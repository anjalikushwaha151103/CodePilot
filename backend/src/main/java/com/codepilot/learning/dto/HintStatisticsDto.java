package com.codepilot.learning.dto;

public class HintStatisticsDto {
    private double averageHintLevel;
    private int totalSolutionReveals;
    private int highHintDependencyCount; // Number of sessions where hint level >= 3

    public HintStatisticsDto() {}

    public HintStatisticsDto(double averageHintLevel, int totalSolutionReveals, int highHintDependencyCount) {
        this.averageHintLevel = averageHintLevel;
        this.totalSolutionReveals = totalSolutionReveals;
        this.highHintDependencyCount = highHintDependencyCount;
    }

    public double getAverageHintLevel() { return averageHintLevel; }
    public void setAverageHintLevel(double averageHintLevel) { this.averageHintLevel = averageHintLevel; }

    public int getTotalSolutionReveals() { return totalSolutionReveals; }
    public void setTotalSolutionReveals(int totalSolutionReveals) { this.totalSolutionReveals = totalSolutionReveals; }

    public int getHighHintDependencyCount() { return highHintDependencyCount; }
    public void setHighHintDependencyCount(int highHintDependencyCount) { this.highHintDependencyCount = highHintDependencyCount; }
}
