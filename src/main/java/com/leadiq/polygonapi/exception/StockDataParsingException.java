package com.leadiq.polygonapi.exception;

/**
 * Represents an exception that is thrown when an error occurs while parsing stock data.
 * This exception is typically used to indicate issues such as malformed data
 * or invalid stock information encountered during the parsing process.
 * It extends the RuntimeException class, making it an unchecked exception.
 */
public class StockDataParsingException extends RuntimeException {

    /**
     * Constructs a new StockDataParsingException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public StockDataParsingException(String message) {
        super(message);
    }

    /**
     * Constructs a new StockDataParsingException with the specified detail message and cause.
     *
     * @param message the detail message explaining the reason for the exception
     * @param cause the cause of the exception, which can be retrieved later using {@link Throwable#getCause()}
     */
    public StockDataParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}

