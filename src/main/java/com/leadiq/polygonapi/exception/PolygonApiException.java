package com.leadiq.polygonapi.exception;

/**
 * Represents an exception specific to the Polygon API.
 * This exception is used to indicate errors or issues encountered while interacting with the API.
 * It extends the RuntimeException class, making it an unchecked exception.
 */
public class PolygonApiException extends RuntimeException {

    /**
     * Constructs a new PolygonApiException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public PolygonApiException(String message) {
        super(message);
    }

    /**
     * Constructs a new PolygonApiException with the specified detail message and cause.
     *
     * @param message the detail message explaining the reason for the exception
     * @param cause the cause of the exception, which can be retrieved later using {@link Throwable#getCause()}
     */
    public PolygonApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

