package com.codepilot.tutoring.client;

import com.codepilot.config.AiServiceProperties;
import com.codepilot.tutoring.dto.TutoringRequestDto;
import com.codepilot.tutoring.dto.TutoringResponseDto;
import com.codepilot.tutoring.exception.AiServiceException;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;

@Component
public class AiServiceClient {

    private final RestClient restClient;

    public AiServiceClient(AiServiceProperties aiServiceProperties, RestClient.Builder restClientBuilder) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(5))
                .withReadTimeout(Duration.ofSeconds(aiServiceProperties.getTimeoutSeconds()));
        
        this.restClient = restClientBuilder
                .baseUrl(aiServiceProperties.getUrl())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public TutoringResponseDto getTutoringHint(TutoringRequestDto requestDto) {
        try {
            return restClient.post()
                    .uri("/api/v1/tutor")
                    .body(requestDto)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        throw new AiServiceException("AI Service rejected the request: " + response.getStatusCode());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new AiServiceException("AI Service encountered an error: " + response.getStatusCode());
                    })
                    .body(TutoringResponseDto.class);
        } catch (RestClientException e) {
            throw new AiServiceException("Failed to communicate with AI service: " + e.getMessage(), e);
        }
    }
}
