package com.codepilot.learning.mastery;

import com.codepilot.learning.dto.Trend;
import com.codepilot.learning.entity.LearningEvent;
import java.util.List;

public interface MasteryCalculator {
    double calculateMastery(List<LearningEvent> events);
    Trend calculateTrend(List<LearningEvent> events);
    double calculateImpact(List<LearningEvent> events); // Impact of the very last event
}
