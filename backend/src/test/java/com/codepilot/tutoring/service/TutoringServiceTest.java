package com.codepilot.tutoring.service;

import com.codepilot.tutoring.client.AiServiceClient;
import com.codepilot.tutoring.dto.ProblemContextDto;
import com.codepilot.tutoring.dto.TutoringRequestDto;
import com.codepilot.tutoring.dto.TutoringResponseDto;
import com.codepilot.tutoring.entity.TutoringSession;
import com.codepilot.tutoring.repository.TutoringSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TutoringServiceTest {

    @Mock
    private AiServiceClient aiServiceClient;

    @Mock
    private TutoringSessionRepository tutoringSessionRepository;

    @Mock
    private org.springframework.context.ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private TutoringService tutoringService;

    private UUID userId;
    private TutoringRequestDto requestDto;
    private TutoringResponseDto responseDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        requestDto = new TutoringRequestDto();
        ProblemContextDto pc = new ProblemContextDto();
        pc.setPlatform("LEETCODE");
        pc.setProblemId("two-sum");
        pc.setTitle("Two Sum");
        requestDto.setProblemContext(pc);
        requestDto.setLanguage("python");
        requestDto.setCode("def twoSum(): pass");
        
        responseDto = new TutoringResponseDto();
        responseDto.setHintLevel(1);
        responseDto.setMessage("Hint 1");
        responseDto.setConcept("Hashing");
        responseDto.setConfidence(0.9);
        responseDto.setShouldRevealSolution(false);
    }

    @Test
    void getTutoringHint_Success_Level1() {
        requestDto.setHintLevel(1);
        when(aiServiceClient.getTutoringHint(requestDto)).thenReturn(responseDto);

        TutoringResponseDto result = tutoringService.getTutoringHint(userId, requestDto);

        assertNotNull(result);
        assertEquals(1, result.getHintLevel());

        ArgumentCaptor<TutoringSession> sessionCaptor = ArgumentCaptor.forClass(TutoringSession.class);
        verify(tutoringSessionRepository).save(sessionCaptor.capture());
        
        TutoringSession saved = sessionCaptor.getValue();
        assertEquals(userId, saved.getUserId());
        assertEquals("LEETCODE", saved.getPlatform());
        assertEquals("two-sum", saved.getProblemId());
        assertEquals("Two Sum", saved.getProblemTitle());
        assertEquals(1, saved.getHintLevel());
        assertEquals("Hashing", saved.getConcept());
        assertFalse(saved.isSolutionRevealed());
    }

    @Test
    void getTutoringHint_InvalidProgression_Level3WithoutPrior() {
        requestDto.setHintLevel(3);
        
        when(tutoringSessionRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
                () -> tutoringService.getTutoringHint(userId, requestDto));
        
        assertTrue(ex.getMessage().contains("Invalid hint progression"));
        verifyNoInteractions(aiServiceClient);
    }

    @Test
    void getTutoringHint_ValidProgression_Level2AfterLevel1() {
        requestDto.setHintLevel(2);
        
        TutoringSession priorSession = new TutoringSession();
        priorSession.setProblemId("two-sum");
        priorSession.setHintLevel(1);
        priorSession.setCreatedAt(OffsetDateTime.now().minusMinutes(5));

        when(tutoringSessionRepository.findByUserId(userId)).thenReturn(List.of(priorSession));
        
        responseDto.setHintLevel(2);
        when(aiServiceClient.getTutoringHint(requestDto)).thenReturn(responseDto);

        TutoringResponseDto result = tutoringService.getTutoringHint(userId, requestDto);

        assertEquals(2, result.getHintLevel());
        verify(tutoringSessionRepository).save(any(TutoringSession.class));
    }
}
