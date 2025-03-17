package com.leadiq.polygonapi.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StockDataParsingExceptionTest {

    @Test
    void testExceptionMessage() {
        String message = "Error parsing stock data";
        StockDataParsingException exception = new StockDataParsingException(message);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testExceptionMessageAndCause() {
        String message = "Error parsing stock data";
        Throwable cause = new NullPointerException("Null value encountered");
        StockDataParsingException exception = new StockDataParsingException(message, cause);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testExceptionWithNullMessage() {
        StockDataParsingException exception = new StockDataParsingException(null);
        assertNull(exception.getMessage());
    }

    @Test
    void testExceptionWithNullMessageAndCause() {
        StockDataParsingException exception = new StockDataParsingException(null, null);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }
}
