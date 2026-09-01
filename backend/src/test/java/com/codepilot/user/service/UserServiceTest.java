package com.codepilot.user.service;

import com.codepilot.common.exception.DuplicateResourceException;
import com.codepilot.common.exception.ResourceNotFoundException;
import com.codepilot.user.dto.CreateUserRequest;
import com.codepilot.user.dto.UpdateUserRequest;
import com.codepilot.user.dto.UserResponse;
import com.codepilot.user.mapper.UserMapper;
import com.codepilot.user.model.Role;
import com.codepilot.user.model.User;
import com.codepilot.user.model.UserStatus;
import com.codepilot.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserMapper userMapper;
    private UserService userService;

    private UUID testUserId;
    private User testUser;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
        userService = new UserService(userRepository, userMapper, passwordEncoder);

        testUserId = UUID.randomUUID();
        testUser = new User("Alice Johnson", "alice@example.com", "$2a$10$hashedpassword");
        testUser.setId(testUserId);
        testUser.setRole(Role.USER);
        testUser.setStatus(UserStatus.PENDING);
        testUser.setCreatedAt(Instant.now());
        testUser.setUpdatedAt(Instant.now());
    }

    @Test
    void createUser_shouldCreateSuccessfully() {
        CreateUserRequest request = new CreateUserRequest("Alice Johnson", "alice@example.com", "password123", null);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedpassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals("Alice Johnson", response.getFullName());
        assertEquals("alice@example.com", response.getEmail());
        assertEquals("USER", response.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_shouldHashPassword() {
        CreateUserRequest request = new CreateUserRequest("Alice Johnson", "alice@example.com", "password123", null);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedpassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(testUserId);
            saved.setCreatedAt(Instant.now());
            saved.setUpdatedAt(Instant.now());
            assertEquals("$2a$10$hashedpassword", saved.getPasswordHash());
            return saved;
        });

        userService.createUser(request);

        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowOnDuplicateEmail() {
        CreateUserRequest request = new CreateUserRequest("Alice Johnson", "alice@example.com", "password123", null);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_withRole_shouldApplyRole() {
        CreateUserRequest request = new CreateUserRequest("Admin User", "admin@example.com", "password123", "ADMIN");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedpassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            saved.setCreatedAt(Instant.now());
            saved.setUpdatedAt(Instant.now());
            assertEquals(Role.ADMIN, saved.getRole());
            return saved;
        });

        userService.createUser(request);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_withInvalidRole_shouldThrow() {
        CreateUserRequest request = new CreateUserRequest("Bad Role", "bad@example.com", "password123", "SUPERADMIN");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(request));
    }

    @Test
    void getUserById_shouldReturnUser() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        UserResponse response = userService.getUserById(testUserId);

        assertNotNull(response);
        assertEquals(testUserId, response.getId());
        assertEquals("Alice Johnson", response.getFullName());
    }

    @Test
    void getUserById_shouldThrowWhenNotFound() {
        UUID missingId = UUID.randomUUID();
        when(userRepository.findById(missingId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(missingId));
    }

    @Test
    void getUserByEmail_shouldReturnUser() {
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(testUser));

        UserResponse response = userService.getUserByEmail("alice@example.com");

        assertNotNull(response);
        assertEquals("alice@example.com", response.getEmail());
    }

    @Test
    void getUserByEmail_shouldThrowWhenNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail("unknown@example.com"));
    }

    @Test
    void listUsers_shouldReturnAllUsers() {
        User secondUser = new User("Bob Smith", "bob@example.com", "$2a$10$hashedpassword2");
        secondUser.setId(UUID.randomUUID());
        secondUser.setRole(Role.USER);
        secondUser.setStatus(UserStatus.ACTIVE);
        secondUser.setCreatedAt(Instant.now());
        secondUser.setUpdatedAt(Instant.now());

        when(userRepository.findAll()).thenReturn(List.of(testUser, secondUser));

        List<UserResponse> responses = userService.listUsers();

        assertEquals(2, responses.size());
    }

    @Test
    void updateUser_shouldUpdatePartially() {
        UpdateUserRequest request = new UpdateUserRequest("Alice Updated", null, null, null);
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.updateUser(testUserId, request);

        assertNotNull(response);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_shouldDetectDuplicateEmail() {
        UpdateUserRequest request = new UpdateUserRequest(null, "existing@example.com", null, null);
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.updateUser(testUserId, request));
    }

    @Test
    void updateUser_shouldThrowWhenNotFound() {
        UUID missingId = UUID.randomUUID();
        UpdateUserRequest request = new UpdateUserRequest("Name", null, null, null);
        when(userRepository.findById(missingId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(missingId, request));
    }

    @Test
    void updateUser_withInvalidStatus_shouldThrow() {
        UpdateUserRequest request = new UpdateUserRequest(null, null, null, "BANNED");
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(testUserId, request));
    }

    @Test
    void deleteUser_shouldDeleteSuccessfully() {
        when(userRepository.existsById(testUserId)).thenReturn(true);

        userService.deleteUser(testUserId);

        verify(userRepository).deleteById(testUserId);
    }

    @Test
    void deleteUser_shouldThrowWhenNotFound() {
        UUID missingId = UUID.randomUUID();
        when(userRepository.existsById(missingId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(missingId));
    }
}
