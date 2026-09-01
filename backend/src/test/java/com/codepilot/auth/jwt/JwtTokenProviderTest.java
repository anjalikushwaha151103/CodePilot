package com.codepilot.auth.jwt;

import com.codepilot.config.JwtProperties;
import com.codepilot.user.model.Role;
import com.codepilot.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private User user;

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties();
        props.setSecret("this-is-a-very-long-secret-key-for-testing-purposes-only-which-must-be-at-least-256-bits");
        props.setExpirationSeconds(3600);
        jwtTokenProvider = new JwtTokenProvider(props);

        user = new User("Test User", "test@example.com", "hashedpassword");
        user.setId(UUID.randomUUID());
        user.setRole(Role.USER);
    }

    @Test
    void generateToken_shouldReturnValidToken() {
        String token = jwtTokenProvider.generateToken(user);
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void validateToken_withInvalidToken_shouldReturnFalse() {
        assertFalse(jwtTokenProvider.validateToken("invalid.token.string"));
    }

    @Test
    void extractClaims_shouldReturnCorrectValues() {
        String token = jwtTokenProvider.generateToken(user);
        
        assertEquals(user.getId(), jwtTokenProvider.getUserIdFromToken(token));
        assertEquals(user.getEmail(), jwtTokenProvider.getEmailFromToken(token));
        assertEquals("USER", jwtTokenProvider.getRoleFromToken(token));
    }
}
