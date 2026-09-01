package com.codepilot.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UpdateUserRequest {

    @Size(min = 1, max = 255, message = "Full name must be between 1 and 255 characters")
    private String fullName;

    @Email(message = "Email must be a valid email address")
    @Size(max = 320, message = "Email must not exceed 320 characters")
    private String email;

    @Size(max = 2048, message = "Profile picture URL must not exceed 2048 characters")
    private String profilePictureUrl;

    private String status;

    public UpdateUserRequest() {
    }

    public UpdateUserRequest(String fullName, String email, String profilePictureUrl, String status) {
        this.fullName = fullName;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
        this.status = status;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
