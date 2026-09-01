package com.codepilot.auth.service;

import com.codepilot.auth.dto.AuthResponse;
import com.codepilot.auth.dto.LoginRequest;
import com.codepilot.auth.dto.RegisterRequest;
import com.codepilot.auth.exception.AuthenticationException;
import com.codepilot.auth.jwt.JwtTokenProvider;
import com.codepilot.common.exception.DuplicateResourceException;
import com.codepilot.user.dto.UserResponse;
import com.codepilot.user.mapper.UserMapper;
import com.codepilot.user.model.Role;
import com.codepilot.user.model.User;
import com.codepilot.user.model.UserStatus;
import com.codepilot.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final String GENERIC_AUTH_ERROR = "Invalid email or password";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userMapper = userMapper;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(request.getFullName(), request.getEmail(), hashedPassword);
        user.setRole(Role.USER);
        user.setStatus(UserStatus.ACTIVE);

        User saved = userRepository.save(user);
        log.info("Registered user id={} email={}", saved.getId(), saved.getEmail());

        String token = jwtTokenProvider.generateToken(saved);
        UserResponse userResponse = userMapper.toResponse(saved);

        return new AuthResponse(token, jwtTokenProvider.getExpirationSeconds(), userResponse);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException(GENERIC_AUTH_ERROR));

        if (user.getStatus() == UserStatus.DISABLED) {
            throw new AuthenticationException(GENERIC_AUTH_ERROR);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AuthenticationException(GENERIC_AUTH_ERROR);
        }

        String token = jwtTokenProvider.generateToken(user);
        UserResponse userResponse = userMapper.toResponse(user);

        log.info("User logged in id={} email={}", user.getId(), user.getEmail());

        return new AuthResponse(token, jwtTokenProvider.getExpirationSeconds(), userResponse);
    }
}
