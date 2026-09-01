package com.codepilot.user.controller;

import com.codepilot.common.exception.DuplicateResourceException;
import com.codepilot.common.exception.ResourceNotFoundException;
import com.codepilot.user.dto.CreateUserRequest;
import com.codepilot.user.dto.UpdateUserRequest;
import com.codepilot.user.dto.UserResponse;
import com.codepilot.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.codepilot.auth.jwt.JwtAuthenticationFilter;
import com.codepilot.auth.security.SecurityExceptionHandler;
import com.codepilot.config.CorsProperties;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private SecurityExceptionHandler securityExceptionHandler;

    @MockBean
    private CorsProperties corsProperties;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID testUserId;
    private UserResponse testUserResponse;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUserResponse = new UserResponse(
                testUserId, "Alice Johnson", "alice@example.com",
                "USER", "PENDING", null, Instant.now(), Instant.now()
        );
    }

    @Test
    void createUser_shouldReturn201() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Alice Johnson", "alice@example.com", "password123", null);
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(testUserResponse);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("Alice Johnson"))
                .andExpect(jsonPath("$.data.email").value("alice@example.com"))
                .andExpect(jsonPath("$.data.role").value("USER"));
    }

    @Test
    void createUser_withInvalidEmail_shouldReturn400() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Alice", "not-an-email", "password123", null);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void createUser_withBlankName_shouldReturn400() throws Exception {
        CreateUserRequest request = new CreateUserRequest("", "alice@example.com", "password123", null);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void createUser_withShortPassword_shouldReturn400() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Alice", "alice@example.com", "short", null);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void createUser_withMissingFields_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void createUser_duplicateEmail_shouldReturn409() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Alice", "alice@example.com", "password123", null);
        when(userService.createUser(any(CreateUserRequest.class)))
                .thenThrow(new DuplicateResourceException("User", "email", "alice@example.com"));

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getUser_shouldReturn200() throws Exception {
        when(userService.getUserById(testUserId)).thenReturn(testUserResponse);

        mockMvc.perform(get("/api/v1/users/{id}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(testUserId.toString()))
                .andExpect(jsonPath("$.data.fullName").value("Alice Johnson"));
    }

    @Test
    void getUser_notFound_shouldReturn404() throws Exception {
        UUID missingId = UUID.randomUUID();
        when(userService.getUserById(missingId))
                .thenThrow(new ResourceNotFoundException("User", "id", missingId));

        mockMvc.perform(get("/api/v1/users/{id}", missingId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void listUsers_shouldReturn200() throws Exception {
        when(userService.listUsers()).thenReturn(List.of(testUserResponse));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void updateUser_shouldReturn200() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest("Alice Updated", null, null, null);
        UserResponse updatedResponse = new UserResponse(
                testUserId, "Alice Updated", "alice@example.com",
                "USER", "PENDING", null, Instant.now(), Instant.now()
        );
        when(userService.updateUser(eq(testUserId), any(UpdateUserRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/users/{id}", testUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("Alice Updated"));
    }

    @Test
    void updateUser_notFound_shouldReturn404() throws Exception {
        UUID missingId = UUID.randomUUID();
        UpdateUserRequest request = new UpdateUserRequest("Name", null, null, null);
        when(userService.updateUser(eq(missingId), any(UpdateUserRequest.class)))
                .thenThrow(new ResourceNotFoundException("User", "id", missingId));

        mockMvc.perform(put("/api/v1/users/{id}", missingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deleteUser_shouldReturn200() throws Exception {
        doNothing().when(userService).deleteUser(testUserId);

        mockMvc.perform(delete("/api/v1/users/{id}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteUser_notFound_shouldReturn404() throws Exception {
        UUID missingId = UUID.randomUUID();
        doThrow(new ResourceNotFoundException("User", "id", missingId))
                .when(userService).deleteUser(missingId);

        mockMvc.perform(delete("/api/v1/users/{id}", missingId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}
