package com.snakeguard.service;

import com.snakeguard.dto.RegisterRequest;
import com.snakeguard.dto.UserResponse;
import com.snakeguard.exception.DuplicateEmailException;
import com.snakeguard.exception.ResourceNotFoundException;
import com.snakeguard.model.User;
import com.snakeguard.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service implementation handling user business logic: registration, password hashing,
 * duplicate verification, and authenticated user profile retrieval.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse register(RegisterRequest request) {
        // 1. Enforce that passwords match
        if (!request.isPasswordMatching()) {
            throw new IllegalArgumentException("Passwords do not match. Please re-enter.");
        }

        String normalizedEmail = request.getEmail().trim().toLowerCase();

        // 2. Prevent duplicate email registration
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateEmailException("An account with this email address already exists. Please log in.");
        }

        // 3. Hash the password using BCrypt - NEVER store plaintext passwords
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // 4. Assemble the User entity
        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(normalizedEmail);
        user.setPhone(request.getPhone().trim());
        user.setCountry(request.getCountry().trim());
        user.setPasswordHash(hashedPassword);
        user.setRole("ROLE_USER");
        user.setAccountStatus("ACTIVE");

        // 5. Persist via Spring JDBC
        User savedUser = userRepository.save(user);
        log.info("Successfully registered new user with ID: {} and email: {}", savedUser.getId(), savedUser.getEmail());

        // 6. Return safe DTO without password hash
        return UserResponse.fromUser(savedUser);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        return userRepository.findByEmail(email.trim().toLowerCase());
    }

    @Override
    public UserResponse getUserResponseByEmail(String email) {
        return findByEmail(email)
                .map(UserResponse::fromUser)
                .orElseThrow(() -> new ResourceNotFoundException("User account not found for email: " + email));
    }

    @Override
    public UserResponse getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResourceNotFoundException("No authenticated user session found");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User account not found for email: " + email));

        return UserResponse.fromUser(user);
    }
}
