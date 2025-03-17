package com.leadiq.polygonapi.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PolygonApiExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String message = "An error occurred";
        PolygonApiException exception = new PolygonApiException(message);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String message = "An error occurred";
        Throwable cause = new RuntimeException("Root cause");
        PolygonApiException exception = new PolygonApiException(message, cause);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testConstructorWithNullMessage() {
        PolygonApiException exception = new PolygonApiException(null);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithNullCause() {
        String message = "An error occurred";
        PolygonApiException exception = new PolygonApiException(message, null);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }
}
