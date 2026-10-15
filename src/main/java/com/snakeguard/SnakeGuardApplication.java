package com.snakeguard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SnakeGuard Application - Stage 1
 * 
 * Entry point for the Spring Boot application.
 * Serves the frontend from static resources and provides REST endpoints for
 * authentication, user management, and session handling.
 */
@SpringBootApplication
public class SnakeGuardApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnakeGuardApplication.class, args);
    }
}
