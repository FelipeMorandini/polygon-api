package com.leadiq.polygonapi.exception;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a standardized error response for an API, providing details
 * about the error that occurred. This class is designed to encapsulate
 * error information in a structured format, which can be returned to the client.
 * The error response contains the following attributes:
 * - code: A unique identifier or code representing the specific error.
 * - message: A human-readable message describing the error in detail.
 * - timestamp: The time when the error occurred, represented as a {@link LocalDateTime}.
 * - requestId: A unique identifier for the specific request, aiding in debugging.
 * - path: The path of the API endpoint where the error occurred.
 * Instances of this class are immutable, ensuring thread safety and consistency
 * in representing error details.
 */
public class ErrorResponse {
    private final String code;
    private final String message;
    private final LocalDateTime timestamp;
    private final String requestId;
    private final String path;

    public ErrorResponse(String code, String message, String path) {
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.requestId = UUID.randomUUID().toString();
        this.path = path;
    }

    /**
     * Retrieves the unique error code associated with this error response.
     * The code represents a standardized identifier for the specific error.
     *
     * @return the error code as a {@code String}
     */
    public String getCode() { return code; }

    /**
     * Retrieves the error message associated with this error response.
     * The message provides a human-readable description of the error
     * intended to convey more details about the issue.
     *
     * @return the error message as a {@code String}
     */
    public String getMessage() {
        return message;
    }

    /**
     * Retrieves the timestamp indicating the exact time when the error occurred.
     *
     * @return the timestamp of the error occurrence as a {@code LocalDateTime}
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Retrieves the unique identifier for the specific request associated with this error response.
     * This ID is useful for correlating logs and debugging issues.
     *
     * @return the unique request ID as a {@code String}
     */
    public String getRequestId() { return requestId; }

    /**
     * Retrieves the API path where the error occurred.
     *
     * @return the API path as a {@code String}
     */
    public String getPath() { return path; }
}
