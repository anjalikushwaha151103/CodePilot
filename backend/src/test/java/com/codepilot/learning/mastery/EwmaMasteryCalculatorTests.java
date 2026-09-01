package com.codepilot.learning.mastery;

import com.codepilot.learning.dto.Trend;
import com.codepilot.learning.entity.LearningEvent;
import com.codepilot.tutoring.entity.TutoringSession;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EwmaMasteryCalculatorTests {

    private final EwmaMasteryCalculator calculator = new EwmaMasteryCalculator();

    private LearningEvent createEvent(int hintLevel, boolean solutionRevealed, Double confidence) {
        TutoringSession session = new TutoringSession();
        session.setHintLevel(hintLevel);
        session.setSolutionRevealed(solutionRevealed);
        session.setConfidence(confidence);
        
        LearningEvent event = new LearningEvent();
        event.setTutoringSession(session);
        return event;
    }

    @Test
    void testNoHistory() {
        assertEquals(0.0, calculator.calculateMastery(List.of()));
        assertEquals(Trend.NEW, calculator.calculateTrend(List.of()));
    }

    @Test
    void testSingleSuccessfulSession() {
        LearningEvent event = createEvent(0, false, null);
        assertEquals(100.0, calculator.calculateMastery(List.of(event)));
        assertEquals(Trend.NEW, calculator.calculateTrend(List.of(event)));
    }

    @Test
    void testSolutionReveal() {
        LearningEvent event = createEvent(0, true, null);
        assertEquals(0.0, calculator.calculateMastery(List.of(event)));
    }

    @Test
    void testRepeatedSuccessfulSessions() {
        LearningEvent e1 = createEvent(2, false, null);
        LearningEvent e2 = createEvent(0, false, null);
        LearningEvent e3 = createEvent(0, false, null);
        
        // e1: 65
        // e2: 65*0.7 + 100*0.3 = 45.5 + 30 = 75.5
        // e3: 75.5*0.7 + 100*0.3 = 52.85 + 30 = 82.85
        double score = calculator.calculateMastery(List.of(e1, e2, e3));
        assertTrue(score > 82.0 && score < 83.0);
        
        // Trend should be IMPROVING because score went from 75.5 to 82.85
        assertEquals(Trend.IMPROVING, calculator.calculateTrend(List.of(e1, e2, e3)));
    }

    @Test
    void testDecliningTrend() {
        LearningEvent e1 = createEvent(0, false, null);
        LearningEvent e2 = createEvent(0, false, null);
        LearningEvent e3 = createEvent(4, false, null); // 20
        
        // score goes down significantly
        assertEquals(Trend.DECLINING, calculator.calculateTrend(List.of(e1, e2, e3)));
    }
}