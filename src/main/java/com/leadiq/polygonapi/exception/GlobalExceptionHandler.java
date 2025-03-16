package com.leadiq.polygonapi.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A centralized exception handler for managing exceptions across the application.
 * This class is annotated with @ControllerAdvice to handle exceptions globally
 * and map specific exception types to corresponding HTTP responses.
 *
 * Each handler method logs the exception and provides a structured error response
 * including details such as timestamp, HTTP status, error type, and a specific
 * error message. This ensures consistent error handling and improves debuggability.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Logger instance used for recording log messages within the GlobalExceptionHandler class.
     * This static and final logger is instantiated to provide consistent logging behavior
     * for various exception handling methods in this class.
     *
     * The logger helps track application behavior and captures details about exceptions,
     * making debugging and monitoring more efficient.
     */
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles exceptions of type {@code PolygonApiException} thrown during the application runtime.
     * This method is triggered when a {@code PolygonApiException} is thrown
     * and constructs a structured error response containing details about the exception.
     *
     * @param ex the {@code PolygonApiException} that was thrown
     * @param request the {@code WebRequest} in the context of which the exception occurred
     * @return a {@code ResponseEntity<Object>} containing the error details and an HTTP status of 503 (Service Unavailable)
     */
    @ExceptionHandler(PolygonApiException.class)
    public ResponseEntity<Object> handlePolygonApiException(PolygonApiException ex, WebRequest request) {
        logger.error("Polygon API exception", ex);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        body.put("error", "Polygon API Error");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Handles exceptions of type StockDataParsingException and returns a structured error response.
     * This method captures details about the exception, such as the timestamp, error message, and
     * relevant request information, and constructs a standardized response to be sent back to the client.
     *
     * @param ex the exception object of type StockDataParsingException that provides details of the error
     * @param request the WebRequest object containing information about the HTTP request that caused the error
     * @return a ResponseEntity containing a map with error details and an HTTP status of INTERNAL_SERVER_ERROR
     */
    @ExceptionHandler(StockDataParsingException.class)
    public ResponseEntity<Object> handleStockDataParsingException(StockDataParsingException ex, WebRequest request) {
        logger.error("Stock data parsing exception", ex);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Data Processing Error");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles exceptions of type {@code StockDataNotFoundException} which occur when stock data
     * for a specific symbol and date cannot be found. This method constructs a response entity
     * containing error details such as the timestamp, status code, error message, and additional
     * information about the requested stock symbol and date.
     *
     * @param ex the exception that was thrown, containing details about the missing stock data
     * @param request the web request during which the exception was encountered, providing contextual information
     * @return a {@code ResponseEntity} containing the error details and HTTP status of {@code NOT_FOUND}
     */
    @ExceptionHandler(StockDataNotFoundException.class)
    public ResponseEntity<Object> handleStockDataNotFoundException(StockDataNotFoundException ex, WebRequest request) {
        logger.warn("Stock data not found: {}", ex.getMessage());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        body.put("symbol", ex.getSymbol());
        body.put("date", ex.getDate());
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles exceptions of type IllegalArgumentException, providing a structured response
     * containing error details such as timestamp, status, error message, and the request path.
     *
     * @param ex the exception that was thrown, representing an illegal argument error
     * @param request the web request during which the exception was raised
     * @return a ResponseEntity containing a detailed error response with status code BAD_REQUEST
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        logger.warn("Invalid request parameters: {}", ex.getMessage());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles generic exceptions that are not explicitly handled by other exception handler methods.
     * This method logs the error and returns a standardized response with a generic error message.
     *
     * @param ex the exception that was thrown
     * @param request the current web request during which the exception occurred
     * @return a ResponseEntity containing error details and an HTTP status of 500 (Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        logger.error("Unhandled exception", ex);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "An unexpected error occurred");
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}