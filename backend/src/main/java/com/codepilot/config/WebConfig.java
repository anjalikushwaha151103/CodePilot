package com.codepilot.config;

import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

/**
 * MVC and HTTP client configuration.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public RestClientCustomizer restClientCustomizer(AiServiceProperties aiServiceProperties) {
        return restClientBuilder -> {
            ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                    .withConnectTimeout(Duration.ofSeconds(5))
                    .withReadTimeout(Duration.ofSeconds(aiServiceProperties.getTimeoutSeconds()));
            restClientBuilder.requestFactory(ClientHttpRequestFactories.get(settings));
        };
    }
}
