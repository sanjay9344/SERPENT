package com.snakeguard.exception;

/**
 * Exception thrown when a requested resource (such as a User) cannot be located.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
