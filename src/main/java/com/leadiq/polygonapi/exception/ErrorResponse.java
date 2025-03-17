package com.leadiq.polygonapi.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a standardized error response for an API, providing details
 * about the error that occurred.
 */
@Schema(description = "Standard error response structure")
public class ErrorResponse {

    @Schema(description = "Error code identifying the type of error", example = "NOT_FOUND")
    private final String code;

    @Schema(description = "Detailed error message", example = "Stock data not found for symbol 'AAPL' on date '2023-01-15'")
    private final String message;

    @Schema(description = "Timestamp when the error occurred", example = "2023-01-15T14:30:15.123")
    private final LocalDateTime timestamp;

    @Schema(description = "Unique identifier for tracking the error", example = "550e8400-e29b-41d4-a716-446655440000")
    private final String requestId;

    @Schema(description = "API path where the error occurred", example = "/api/v1/stocks/AAPL")
    private final String path;

    public ErrorResponse(String code, String message, String path) {
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.requestId = UUID.randomUUID().toString();
        this.path = path;
    }

    // Getters
    public String getCode() { return code; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getRequestId() { return requestId; }
    public String getPath() { return path; }
}