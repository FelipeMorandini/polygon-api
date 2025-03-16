package com.leadiq.polygonapi.exception;

import java.time.LocalDateTime;

/**
 * Represents an error response to be returned in case of exceptions or failures within the application.
 * This class encapsulates details about the error, such as an error code, a descriptive message,
 * and the timestamp when the error occurred.
 * The ErrorResponse object is typically used to provide a structured error representation
 * in API responses, ensuring a consistent and informative output for error scenarios.
 */
public class ErrorResponse {
    private final String code;
    private final String message;
    private final LocalDateTime timestamp;

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
