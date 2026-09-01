package com.codepilot.tutoring.client;

import com.codepilot.config.AiServiceProperties;
import com.codepilot.tutoring.dto.TutoringRequestDto;
import com.codepilot.tutoring.dto.TutoringResponseDto;
import com.codepilot.tutoring.exception.AiServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(AiServiceClient.class)
@EnableConfigurationProperties(AiServiceProperties.class)
class AiServiceClientTest {

    @Autowired
    private AiServiceClient aiServiceClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Test
    void getTutoringHint_Success() {
        mockServer.expect(requestTo("http://localhost:8000/api/v1/tutor"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(
                        "{\"hintLevel\": 1, \"message\": \"Try using a hash map.\", \"concept\": \"Hashing\", \"confidence\": 0.9, \"shouldRevealSolution\": false}",
                        MediaType.APPLICATION_JSON
                ));

        TutoringRequestDto request = new TutoringRequestDto();
        
        TutoringResponseDto response = aiServiceClient.getTutoringHint(request);
        
        assertNotNull(response);
        assertEquals(1, response.getHintLevel());
        assertEquals("Try using a hash map.", response.getMessage());
        mockServer.verify();
    }

    @Test
    void getTutoringHint_5xxError() {
        mockServer.expect(requestTo("http://localhost:8000/api/v1/tutor"))
                .andRespond(withServerError());

        TutoringRequestDto request = new TutoringRequestDto();
        
        assertThrows(AiServiceException.class, () -> aiServiceClient.getTutoringHint(request));
        mockServer.verify();
    }
}
