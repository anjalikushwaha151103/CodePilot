package com.codepilot;

import com.codepilot.config.CorsProperties;
import com.codepilot.config.JwtProperties;
import com.codepilot.config.AiServiceProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, CorsProperties.class, AiServiceProperties.class})
public class CodePilotApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodePilotApplication.class, args);
    }
}
