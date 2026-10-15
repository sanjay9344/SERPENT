package com.snakeguard.exception;

/**
 * Exception thrown when attempting to register a user with an email
 * address that is already associated with an existing account.
 */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String message) {
        super(message);
    }
}
