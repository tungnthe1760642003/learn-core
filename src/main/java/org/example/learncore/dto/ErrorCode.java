package org.example.learncore.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // System Errors
    UNCATEGORIZED_EXCEPTION("9999", "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY("1001", "Uncategorized error", HttpStatus.BAD_REQUEST),
    INVALID_ENUM_VALUE("1002", "Invalid enum value", HttpStatus.BAD_REQUEST),

    // Business Errors
    USER_NOT_FOUND("2001", "User not found", HttpStatus.NOT_FOUND),
    ENTITY_NOT_FOUND("2002", "Entity not found", HttpStatus.NOT_FOUND),
    INSUFFICIENT_FUNDS("3001", "Insufficient funds for this transaction", HttpStatus.BAD_REQUEST),
    ACCOUNT_BLOCKED("3002", "Account is blocked", HttpStatus.BAD_REQUEST),
    OPTIMISTIC_LOCK_STALE("4001", "Data was updated by another user. Please refresh.", HttpStatus.CONFLICT),
    PESSIMISTIC_LOCK_TIMEOUT("4002", "System is busy, please try again later", HttpStatus.REQUEST_TIMEOUT);

    private final String code;
    private final String message;
    private final HttpStatus statusCode;

    ErrorCode(String code, String message, HttpStatus statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
