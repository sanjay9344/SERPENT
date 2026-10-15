package com.snakeguard.repository;

import com.snakeguard.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.KeyHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JdbcUserRepositoryTest {

    @Mock
    private JdbcOperations jdbcTemplate;

    @InjectMocks
    private JdbcUserRepository repository;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(10L);
        sampleUser.setFullName("Arun Kumar");
        sampleUser.setEmail("arun@example.com");
        sampleUser.setPhone("+91 9123456780");
        sampleUser.setCountry("India");
        sampleUser.setPasswordHash("$2a$10$abcdefghijk");
        sampleUser.setRole("ROLE_USER");
        sampleUser.setAccountStatus("ACTIVE");
    }

    @Test
    @DisplayName("Should find user by email")
    void testFindByEmailFound() {
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq("arun@example.com")))
                .thenReturn(sampleUser);

        Optional<User> result = repository.findByEmail("arun@example.com");

        assertTrue(result.isPresent());
        assertEquals("Arun Kumar", result.get().getFullName());
        assertEquals("arun@example.com", result.get().getEmail());
    }

    @Test
    @DisplayName("Should return empty optional when user email is not found")
    void testFindByEmailNotFound() {
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq("notfound@example.com")))
                .thenThrow(new EmptyResultDataAccessException(1));

        Optional<User> result = repository.findByEmail("notfound@example.com");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should correctly check existsByEmail")
    void testExistsByEmail() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("arun@example.com")))
                .thenReturn(1);

        assertTrue(repository.existsByEmail("arun@example.com"));

        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("unknown@example.com")))
                .thenReturn(0);

        assertFalse(repository.existsByEmail("unknown@example.com"));
    }

    @Test
    @DisplayName("Should update existing user when ID is present")
    void testUpdateExistingUser() {
        repository.save(sampleUser);

        verify(jdbcTemplate).update(
                contains("UPDATE users SET"),
                eq("Arun Kumar"),
                eq("arun@example.com"),
                eq("+91 9123456780"),
                eq("India"),
                eq("$2a$10$abcdefghijk"),
                eq("ROLE_USER"),
                eq("ACTIVE"),
                eq(10L)
        );
    }
}
