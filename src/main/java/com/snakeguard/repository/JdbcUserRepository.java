package com.snakeguard.repository;

import com.snakeguard.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Spring JDBC implementation of UserRepository using JdbcTemplate.
 * 
 * All queries use parameterized SQL placeholders (?) to guarantee protection
 * against SQL Injection attacks. User inputs are never concatenated directly
 * into SQL strings.
 * 
 * Includes an automated in-memory fallback for local student demonstrations
 * when MySQL is not yet installed or active on localhost:3306.
 */
@Repository
public class JdbcUserRepository implements UserRepository {

    private static final Logger log = LoggerFactory.getLogger(JdbcUserRepository.class);

    private final JdbcOperations jdbcTemplate;

    // Resilient in-memory fallback store when MySQL service is offline
    private final Map<String, User> memoryStore = new ConcurrentHashMap<>();
    private final AtomicLong memoryIdGenerator = new AtomicLong(100);

    public JdbcUserRepository(JdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Maps database ResultSet rows into User domain instances.
     */
    private final RowMapper<User> userRowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setFullName(rs.getString("full_name"));
            user.setEmail(rs.getString("email"));
            user.setPhone(rs.getString("phone"));
            user.setCountry(rs.getString("country"));
            user.setPasswordHash(rs.getString("password_hash"));
            user.setRole(rs.getString("role"));
            user.setAccountStatus(rs.getString("account_status"));

            Timestamp createdAtTs = rs.getTimestamp("created_at");
            if (createdAtTs != null) {
                user.setCreatedAt(createdAtTs.toLocalDateTime());
            }

            Timestamp updatedAtTs = rs.getTimestamp("updated_at");
            if (updatedAtTs != null) {
                user.setUpdatedAt(updatedAtTs.toLocalDateTime());
            }

            return user;
        }
    };

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT id, full_name, email, phone, country, password_hash, role, account_status, created_at, updated_at " +
                     "FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, id);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (DataAccessException e) {
            log.warn("MySQL database is not reachable at localhost:3306. Reading from in-memory fallback store.");
            return memoryStore.values().stream()
                    .filter(u -> u.getId().equals(id))
                    .findFirst();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        String normalizedEmail = email.trim().toLowerCase();

        String sql = "SELECT id, full_name, email, phone, country, password_hash, role, account_status, created_at, updated_at " +
                     "FROM users WHERE LOWER(email) = LOWER(?)";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, normalizedEmail);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (DataAccessException e) {
            log.warn("MySQL database is not reachable at localhost:3306. Reading from in-memory fallback store.");
            return Optional.ofNullable(memoryStore.get(normalizedEmail));
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null) return false;
        String normalizedEmail = email.trim().toLowerCase();

        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(email) = LOWER(?)";
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, normalizedEmail);
            return count != null && count > 0;
        } catch (DataAccessException e) {
            log.warn("MySQL database is not reachable at localhost:3306. Checking in-memory fallback store.");
            return memoryStore.containsKey(normalizedEmail);
        }
    }

    @Override
    public User save(User user) {
        String normalizedEmail = user.getEmail().trim().toLowerCase();

        try {
            if (user.getId() == null) {
                // INSERT new user record into MySQL
                String sql = "INSERT INTO users (full_name, email, phone, country, password_hash, role, account_status) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?)";

                KeyHolder keyHolder = new GeneratedKeyHolder();

                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, user.getFullName());
                    ps.setString(2, normalizedEmail);
                    ps.setString(3, user.getPhone());
                    ps.setString(4, user.getCountry());
                    ps.setString(5, user.getPasswordHash());
                    ps.setString(6, user.getRole() != null ? user.getRole() : "ROLE_USER");
                    ps.setString(7, user.getAccountStatus() != null ? user.getAccountStatus() : "ACTIVE");
                    return ps;
                }, keyHolder);

                Number generatedKey = keyHolder.getKey();
                if (generatedKey != null) {
                    user.setId(generatedKey.longValue());
                }
                if (user.getCreatedAt() == null) {
                    user.setCreatedAt(LocalDateTime.now());
                }
                return user;
            } else {
                // UPDATE existing user record in MySQL
                String sql = "UPDATE users SET full_name = ?, email = ?, phone = ?, country = ?, " +
                             "password_hash = ?, role = ?, account_status = ? WHERE id = ?";

                jdbcTemplate.update(sql,
                        user.getFullName(),
                        normalizedEmail,
                        user.getPhone(),
                        user.getCountry(),
                        user.getPasswordHash(),
                        user.getRole(),
                        user.getAccountStatus(),
                        user.getId()
                );
                return user;
            }
        } catch (DataAccessException e) {
            log.warn("MySQL database is not reachable at localhost:3306 ({}). Falling back to in-memory store for this session.", e.getMessage());
            if (user.getId() == null) {
                user.setId(memoryIdGenerator.incrementAndGet());
            }
            if (user.getCreatedAt() == null) {
                user.setCreatedAt(LocalDateTime.now());
            }
            memoryStore.put(normalizedEmail, user);
            return user;
        }
    }
}
