package com.codepilot.tutoring.controller;

import com.codepilot.auth.jwt.JwtAuthenticationFilter;
import com.codepilot.auth.jwt.JwtTokenProvider;
import com.codepilot.tutoring.dto.ProblemContextDto;
import com.codepilot.tutoring.dto.TutoringRequestDto;
import com.codepilot.tutoring.dto.TutoringResponseDto;
import com.codepilot.tutoring.service.TutoringService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TutoringController.class)
@AutoConfigureMockMvc(addFilters = false) // Disabling security filters for controller slice test, we use @WithMockUser
class TutoringControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TutoringService tutoringService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000")
    void getTutoringHint_Success() throws Exception {
        TutoringRequestDto request = new TutoringRequestDto();
        ProblemContextDto pc = new ProblemContextDto();
        pc.setPlatform("LEETCODE");
        pc.setProblemId("two-sum");
        request.setProblemContext(pc);
        request.setLanguage("python");
        request.setCode("def twoSum(): pass");
        request.setHintLevel(1);

        TutoringResponseDto response = new TutoringResponseDto();
        response.setHintLevel(1);
        response.setMessage("Hint");

        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        when(tutoringService.getTutoringHint(eq(userId), any(TutoringRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/tutoring")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(userId.toString(), ""))
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.hintLevel").value(1))
                .andExpect(jsonPath("$.data.message").value("Hint"));
    }
}
