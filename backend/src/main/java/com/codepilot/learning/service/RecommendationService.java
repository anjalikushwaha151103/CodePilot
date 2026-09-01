package com.codepilot.learning.service;

import com.codepilot.learning.dto.ConceptMasteryDto;
import com.codepilot.learning.dto.RecommendationDto;

import java.util.List;

public interface RecommendationService {
    List<RecommendationDto> getRecommendations(List<ConceptMasteryDto> masteries);
}