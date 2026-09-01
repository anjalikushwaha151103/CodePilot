package com.codepilot.learning.mastery;

import com.codepilot.learning.dto.Trend;
import com.codepilot.learning.entity.LearningEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EwmaMasteryCalculator implements MasteryCalculator {

    @Override
    public double calculateMastery(List<LearningEvent> events) {
        if (events == null || events.isEmpty()) {
            return 0.0;
        }

        double mastery = -1.0;

        for (LearningEvent event : events) {
            double eventScore = 0.0;
            
            if (event.getTutoringSession() != null && event.getTutoringSession().isSolutionRevealed()) {
                eventScore = 0.0;
            } else {
                int hintLevel = event.getTutoringSession() != null ? event.getTutoringSession().getHintLevel() : 0;
                
                switch (hintLevel) {
                    case 0 -> eventScore = 100.0;
                    case 1 -> eventScore = 85.0;
                    case 2 -> eventScore = 65.0;
                    case 3 -> eventScore = 40.0;
                    case 4 -> eventScore = 20.0;
                    default -> eventScore = 0.0;
                }
                
                if (event.getTutoringSession() != null && event.getTutoringSession().getConfidence() != null) {
                    double confidence = event.getTutoringSession().getConfidence();
                    eventScore = eventScore * (0.8 + 0.2 * confidence);
                }
            }

            if (mastery == -1.0) {
                mastery = eventScore;
            } else {
                mastery = mastery * 0.7 + eventScore * 0.3;
            }
        }

        if (mastery < 0.0) mastery = 0.0;
        if (mastery > 100.0) mastery = 100.0;

        return mastery;
    }

    @Override
    public Trend calculateTrend(List<LearningEvent> events) {
        if (events == null || events.size() <= 2) {
            return Trend.NEW;
        }

        double currentMastery = calculateMastery(events);
        double previousMastery = calculateMastery(events.subList(0, events.size() - 1));
        
        double delta = currentMastery - previousMastery;
        
        // Threshold for change is 2.0
        if (delta >= 2.0) {
            return Trend.IMPROVING;
        } else if (delta <= -2.0) {
            return Trend.DECLINING;
        } else {
            return Trend.STABLE;
        }
    }

    @Override
    public double calculateImpact(List<LearningEvent> events) {
        if (events == null || events.isEmpty()) {
            return 0.0;
        }
        if (events.size() == 1) {
            return calculateMastery(events); // For first event, impact is the full score
        }
        double currentMastery = calculateMastery(events);
        double previousMastery = calculateMastery(events.subList(0, events.size() - 1));
        return currentMastery - previousMastery;
    }
}
