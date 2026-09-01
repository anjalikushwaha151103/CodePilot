package com.codepilot.learning.event;

import com.codepilot.tutoring.entity.TutoringSession;
import java.util.List;

public class TutoringSessionCompletedEvent {
    private final TutoringSession session;
    private final List<String> rawTags;

    public TutoringSessionCompletedEvent(TutoringSession session, List<String> rawTags) {
        this.session = session;
        this.rawTags = rawTags;
    }

    public TutoringSession getSession() {
        return session;
    }

    public List<String> getRawTags() {
        return rawTags;
    }
}