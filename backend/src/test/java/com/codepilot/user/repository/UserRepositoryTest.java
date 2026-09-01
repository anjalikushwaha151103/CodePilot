package com.codepilot.user.repository;

import com.codepilot.user.model.Role;
import com.codepilot.user.model.User;
import com.codepilot.user.model.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        testUser = new User("Alice Johnson", "alice@example.com", "$2a$10$hashedpasswordplaceholder");
        testUser.setRole(Role.USER);
        testUser.setStatus(UserStatus.ACTIVE);
    }

    @Test
    void shouldSaveAndRetrieveUser() {
        User saved = userRepository.save(testUser);

        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
        assertEquals("Alice Johnson", saved.getFullName());
        assertEquals("alice@example.com", saved.getEmail());
        assertEquals(Role.USER, saved.getRole());
        assertEquals(UserStatus.ACTIVE, saved.getStatus());
    }

    @Test
    void shouldFindByEmail() {
        userRepository.save(testUser);

        Optional<User> found = userRepository.findByEmail("alice@example.com");

        assertTrue(found.isPresent());
        assertEquals("Alice Johnson", found.get().getFullName());
    }

    @Test
    void shouldReturnEmptyWhenEmailNotFound() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        assertFalse(found.isPresent());
    }

    @Test
    void shouldCheckExistsByEmail() {
        userRepository.save(testUser);

        assertTrue(userRepository.existsByEmail("alice@example.com"));
        assertFalse(userRepository.existsByEmail("bob@example.com"));
    }

    @Test
    void shouldDeleteUser() {
        User saved = userRepository.save(testUser);

        userRepository.deleteById(saved.getId());

        assertFalse(userRepository.findById(saved.getId()).isPresent());
    }
}
