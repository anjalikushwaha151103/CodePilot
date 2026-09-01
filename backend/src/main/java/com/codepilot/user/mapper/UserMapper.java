package com.codepilot.user.mapper;

import com.codepilot.user.dto.UserResponse;
import com.codepilot.user.model.User;
import org.springframework.stereotype.Component;

/**
 * Manual mapper between User entity and User DTOs.
 * Centralizes all entity-to-DTO and DTO-to-entity conversion logic.
 */
@Component
public class UserMapper {

    /**
     * Converts a User entity to a UserResponse DTO.
     * Never exposes passwordHash.
     */
    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setStatus(user.getStatus().name());
        response.setProfilePictureUrl(user.getProfilePictureUrl());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}
