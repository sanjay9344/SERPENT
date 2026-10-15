package com.snakeguard.repository;

import com.snakeguard.model.User;
import java.util.Optional;

/**
 * Repository interface defining persistence operations for the User entity.
 */
public interface UserRepository {

    /**
     * Finds a user by primary key ID.
     */
    Optional<User> findById(Long id);

    /**
     * Finds a user by email address (case-insensitive lookup).
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether an account exists with the given email.
     */
    boolean existsByEmail(String email);

    /**
     * Saves a user record (handles both insert and update).
     * @return the saved user with generated ID populated
     */
    User save(User user);
}
