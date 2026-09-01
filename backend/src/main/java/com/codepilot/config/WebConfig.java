package com.codepilot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVC configuration. CORS is handled by Spring Security's CorsConfigurationSource
 * in SecurityConfig. This class is retained for future MVC-specific configuration.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
}
