package com.codepilot.tutoring.controller;

import com.codepilot.common.ApiResponse;
import com.codepilot.tutoring.dto.TutoringRequestDto;
import com.codepilot.tutoring.dto.TutoringResponseDto;
import com.codepilot.tutoring.service.TutoringService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tutoring")
public class TutoringController {

    private final TutoringService tutoringService;

    public TutoringController(TutoringService tutoringService) {
        this.tutoringService = tutoringService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TutoringResponseDto>> getTutoringHint(
            @Valid @RequestBody TutoringRequestDto requestDto,
            Authentication authentication) {
        
        UUID userId = UUID.fromString(authentication.getName());
        
        TutoringResponseDto response = tutoringService.getTutoringHint(userId, requestDto);
        
        return ResponseEntity.ok(ApiResponse.success("Hint generated successfully", response));
    }
}
