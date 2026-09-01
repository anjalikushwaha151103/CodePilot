package com.codepilot.learning.controller;

import com.codepilot.common.ApiResponse;
import com.codepilot.learning.dto.ConceptMasteryDto;
import com.codepilot.learning.dto.LearningProfileDto;
import com.codepilot.learning.service.LearningService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/learning")
public class LearningController {

    private final LearningService learningService;

    public LearningController(LearningService learningService) {
        this.learningService = learningService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<LearningProfileDto>> getProfile(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(learningService.getLearningProfile(userId)));
    }

    @GetMapping("/concepts")
    public ResponseEntity<ApiResponse<List<ConceptMasteryDto>>> getConcepts(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(learningService.getConceptMasteries(userId)));
    }

    @GetMapping("/weak-areas")
    public ResponseEntity<ApiResponse<List<ConceptMasteryDto>>> getWeakAreas(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(learningService.getLearningProfile(userId).getWeakestConcepts()));
    }

    @GetMapping("/strong-areas")
    public ResponseEntity<ApiResponse<List<ConceptMasteryDto>>> getStrongAreas(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(learningService.getLearningProfile(userId).getStrongestConcepts()));
    }
}