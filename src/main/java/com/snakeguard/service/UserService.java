package com.snakeguard.service;

import com.snakeguard.dto.RegisterRequest;
import com.snakeguard.dto.UserResponse;
import com.snakeguard.model.User;
import org.springframework.security.core.Authentication;

import java.util.Optional;

/**
 * Service interface defining user management contracts.
 */
public interface UserService {

    /**
     * Registers a new user.
     */
    UserResponse register(RegisterRequest request);

    /**
     * Looks up user by email.
     */
    Optional<User> findByEmail(String email);

    /**
     * Looks up user by email and returns a safe UserResponse.
     */
    UserResponse getUserResponseByEmail(String email);

    /**
     * Returns safe UserResponse for the currently authenticated user.
     */
    UserResponse getCurrentUser(Authentication authentication);
}
