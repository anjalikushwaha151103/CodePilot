package com.codepilot.learning.service;

import com.codepilot.learning.dto.ConceptMasteryDto;
import com.codepilot.learning.dto.RecommendationDto;
import com.codepilot.learning.dto.Trend;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Override
    public List<RecommendationDto> getRecommendations(List<ConceptMasteryDto> masteries) {
        List<RecommendationDto> recommendations = new ArrayList<>();

        for (ConceptMasteryDto mastery : masteries) {
            RecommendationDto rec = new RecommendationDto();
            rec.setConcept(mastery.getConcept());
            rec.setDisplayName(mastery.getDisplayName());

            if (mastery.getAttempts() < 3) {
                // Not enough data
                rec.setPriority("LOW");
                rec.setReason("Insufficient historical data to determine mastery confidently.");
                rec.setSuggestedAction("Continue solving varied problems to build your learning profile.");
            } else if (mastery.getMasteryScore() < 40.0) {
                rec.setPriority("HIGH");
                rec.setReason(buildReasonForWeakness(mastery));
                rec.setSuggestedAction("Focused practice is highly recommended. Try 2-3 easy/medium " + mastery.getDisplayName() + " problems.");
            } else if (mastery.getMasteryScore() >= 40.0 && mastery.getMasteryScore() < 70.0) {
                rec.setPriority("MEDIUM");
                if (mastery.getTrend() == Trend.DECLINING) {
                    rec.setReason("Your mastery in " + mastery.getDisplayName() + " is slipping.");
                    rec.setSuggestedAction("Revisit fundamental " + mastery.getDisplayName() + " techniques to stabilize your score.");
                } else {
                    rec.setReason("You have foundational knowledge, but rely on hints or struggle occasionally.");
                    rec.setSuggestedAction("Practice medium difficulty " + mastery.getDisplayName() + " problems to solidify your understanding.");
                }
            } else if (mastery.getMasteryScore() >= 70.0 && mastery.getMasteryScore() <= 100.0) {
                if (mastery.getTrend() == Trend.DECLINING) {
                    rec.setPriority("MEDIUM");
                    rec.setReason("Although strong, your recent sessions in " + mastery.getDisplayName() + " required more hints.");
                    rec.setSuggestedAction("A quick refresher problem could help reverse this decline.");
                } else {
                    rec.setPriority("LOW");
                    rec.setReason("You have strong, stable mastery of " + mastery.getDisplayName() + ".");
                    rec.setSuggestedAction("Prioritize other weaker concepts before practicing this further.");
                }
            }

            recommendations.add(rec);
        }

        // Sort by priority (HIGH > MEDIUM > LOW) and then by mastery score ascending
        recommendations.sort((a, b) -> {
            int pA = priorityValue(a.getPriority());
            int pB = priorityValue(b.getPriority());
            if (pA != pB) return Integer.compare(pB, pA); // Descending priority
            
            // Find mastery score to secondary sort
            double scoreA = masteries.stream().filter(m -> m.getConcept().equals(a.getConcept())).findFirst().map(ConceptMasteryDto::getMasteryScore).orElse(100.0);
            double scoreB = masteries.stream().filter(m -> m.getConcept().equals(b.getConcept())).findFirst().map(ConceptMasteryDto::getMasteryScore).orElse(100.0);
            return Double.compare(scoreA, scoreB);
        });

        // Return top 3 actionable recommendations
        if (recommendations.size() > 3) {
            return recommendations.subList(0, 3);
        }
        return recommendations;
    }

    private String buildReasonForWeakness(ConceptMasteryDto mastery) {
        if (mastery.getAverageHintLevel() >= 3.0 || mastery.getSolutionReveals() > 0) {
            return "Your recent sessions show low mastery and frequent dependency on stronger hints or solution reveals.";
        }
        return "Your overall mastery remains low, indicating fundamental gaps in understanding.";
    }

    private int priorityValue(String priority) {
        return switch (priority) {
            case "HIGH" -> 3;
            case "MEDIUM" -> 2;
            case "LOW" -> 1;
            default -> 0;
        };
    }
}
