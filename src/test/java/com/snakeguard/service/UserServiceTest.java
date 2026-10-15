package com.snakeguard.service;

import com.snakeguard.dto.RegisterRequest;
import com.snakeguard.dto.UserResponse;
import com.snakeguard.exception.DuplicateEmailException;
import com.snakeguard.exception.ResourceNotFoundException;
import com.snakeguard.model.User;
import com.snakeguard.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new RegisterRequest(
                "Jane Doe",
                "jane.doe@example.com",
                "+91 9876543210",
                "India",
                "SecurePass123",
                "SecurePass123"
        );
    }

    @Test
    @DisplayName("Should successfully register a new user and hash the password")
    void testRegisterSuccess() {
        when(userRepository.existsByEmail("jane.doe@example.com")).thenReturn(false);
        when(passwordEncoder.encode("SecurePass123")).thenReturn("$2a$10$hashedPasswordSample");

        User savedMockUser = new User(
                1L,
                "Jane Doe",
                "jane.doe@example.com",
                "+91 9876543210",
                "India",
                "$2a$10$hashedPasswordSample",
                "ROLE_USER",
                "ACTIVE",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(userRepository.save(any(User.class))).thenReturn(savedMockUser);

        UserResponse response = userService.register(validRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Jane Doe", response.getFullName());
        assertEquals("jane.doe@example.com", response.getEmail());
        assertEquals("+91 9876543210", response.getPhone());
        assertEquals("India", response.getCountry());
        assertEquals("ROLE_USER", response.getRole());
        assertEquals("ACTIVE", response.getAccountStatus());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User captured = captor.getValue();
        assertEquals("$2a$10$hashedPasswordSample", captured.getPasswordHash());
        assertNotEquals("SecurePass123", captured.getPasswordHash(), "Plaintext password must never be stored");
    }

    @Test
    @DisplayName("Should reject registration when passwords do not match")
    void testRegisterPasswordMismatch() {
        validRequest.setConfirmPassword("DifferentPassword456");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.register(validRequest);
        });

        assertTrue(ex.getMessage().contains("Passwords do not match"));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject registration when email already exists")
    void testRegisterDuplicateEmail() {
        when(userRepository.existsByEmail("jane.doe@example.com")).thenReturn(true);

        DuplicateEmailException ex = assertThrows(DuplicateEmailException.class, () -> {
            userService.register(validRequest);
        });

        assertTrue(ex.getMessage().contains("already exists"));
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return safe user details for authenticated user")
    void testGetCurrentUserSuccess() {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "jane.doe@example.com",
                null,
                java.util.Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"))
        );
        User user = new User(1L, "Jane Doe", "jane.doe@example.com", "+91 9876543210", "India",
                "hash", "ROLE_USER", "ACTIVE", LocalDateTime.now(), LocalDateTime.now());

        when(userRepository.findByEmail("jane.doe@example.com")).thenReturn(Optional.of(user));

        UserResponse response = userService.getCurrentUser(auth);

        assertNotNull(response);
        assertEquals("Jane Doe", response.getFullName());
        assertEquals("jane.doe@example.com", response.getEmail());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when authentication is null")
    void testGetCurrentUserUnauthenticated() {
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getCurrentUser(null);
        });
    }
}
