package com.leadiq.polygonapi.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.util.Map;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    public void testHandlePolygonApiException() {
        PolygonApiException exception = new PolygonApiException("Error occurred");
        when(webRequest.getDescription(false)).thenReturn("/test/path");

        ResponseEntity<Object> response = globalExceptionHandler.handlePolygonApiException(exception, webRequest);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Polygon API Error", body.get("error"));
        assertEquals("Error occurred", body.get("message"));
        assertEquals("/test/path", body.get("path"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    public void testHandleStockDataParsingException() {
        StockDataParsingException exception = new StockDataParsingException("Parsing error");
        when(webRequest.getDescription(false)).thenReturn("/test/path");

        ResponseEntity<Object> response = globalExceptionHandler.handleStockDataParsingException(exception, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Data Processing Error", body.get("error"));
        assertEquals("Parsing error", body.get("message"));
        assertEquals("/test/path", body.get("path"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    public void testHandleStockDataNotFoundException() {
        String symbol = "AAPL";
        LocalDate date = LocalDate.of(2025, 3, 17);
        StockDataNotFoundException exception = new StockDataNotFoundException(symbol, date);
        when(webRequest.getDescription(false)).thenReturn("/test/path");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleStockDataNotFoundException(exception, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertEquals("NOT_FOUND", errorResponse.getCode());
        assertEquals("Stock data not found for symbol 'AAPL' on date '2025-03-17'", errorResponse.getMessage());
        assertEquals("/test/path", errorResponse.getPath());
    }
}
