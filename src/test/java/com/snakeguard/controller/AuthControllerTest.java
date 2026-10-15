package com.snakeguard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snakeguard.dto.LoginRequest;
import com.snakeguard.dto.RegisterRequest;
import com.snakeguard.dto.UserResponse;
import com.snakeguard.exception.DuplicateEmailException;
import com.snakeguard.exception.GlobalExceptionHandler;
import com.snakeguard.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private SecurityContextRepository securityContextRepository;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/auth/register - Should return 201 Created on valid registration")
    void testRegisterSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Priya Sharma",
                "priya@example.com",
                "+91 9876543210",
                "India",
                "SecretPass123",
                "SecretPass123"
        );

        UserResponse mockResponse = new UserResponse(
                1L, "Priya Sharma", "priya@example.com", "+91 9876543210",
                "India", "ROLE_USER", "ACTIVE", LocalDateTime.now()
        );

        when(userService.register(any(RegisterRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("Priya Sharma"))
                .andExpect(jsonPath("$.data.email").value("priya@example.com"))
                .andExpect(jsonPath("$.data.password").doesNotExist())
                .andExpect(jsonPath("$.data.passwordHash").doesNotExist());
    }

    @Test
    @DisplayName("POST /api/auth/register - Should return 400 Bad Request on invalid fields")
    void testRegisterValidationFailure() throws Exception {
        // Missing name, invalid email, short password
        RegisterRequest invalidRequest = new RegisterRequest(
                "",
                "invalid-email",
                "123",
                "",
                "short",
                "short"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.email").exists())
                .andExpect(jsonPath("$.error.password").exists());
    }

    @Test
    @DisplayName("POST /api/auth/register - Should return 409 Conflict when email is duplicate")
    void testRegisterDuplicateEmail() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Priya Sharma",
                "priya@example.com",
                "+91 9876543210",
                "India",
                "SecretPass123",
                "SecretPass123"
        );

        when(userService.register(any(RegisterRequest.class)))
                .thenThrow(new DuplicateEmailException("An account with this email address already exists. Please log in."));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("An account with this email address already exists. Please log in."));
    }

    @Test
    @DisplayName("POST /api/auth/login - Should return 200 OK on valid credentials")
    void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest("priya@example.com", "SecretPass123");

        Authentication auth = new UsernamePasswordAuthenticationToken("priya@example.com", "SecretPass123");
        when(authenticationManager.authenticate(any())).thenReturn(auth);

        UserResponse mockResponse = new UserResponse(
                1L, "Priya Sharma", "priya@example.com", "+91 9876543210",
                "India", "ROLE_USER", "ACTIVE", LocalDateTime.now()
        );
        when(userService.getUserResponseByEmail("priya@example.com")).thenReturn(mockResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("priya@example.com"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Should return 401 Unauthorized on invalid credentials")
    void testLoginBadCredentials() throws Exception {
        LoginRequest request = new LoginRequest("priya@example.com", "WrongPassword");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email address or password. Please try again."));
    }

    @Test
    @DisplayName("GET /api/auth/me - Should return 401 Unauthorized when not logged in")
    void testGetCurrentUserUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/auth/logout - Should return 200 OK")
    void testLogoutSuccess() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
    }
}
