package com.leadiq.polygonapi.service;

import com.leadiq.polygonapi.config.client.PolygonApiConfig;
import com.leadiq.polygonapi.exception.PolygonApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PolygonClientTest {

    @Mock
    private PolygonApiConfig config;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PolygonClient polygonClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(config.getKey()).thenReturn("testApiKey");
    }

    @Test
    void fetchStockData_ValidInput_ReturnsData() {
        String symbol = "AAPL";
        String fromDate = "2025-03-13";
        String toDate = "2025-03-14";
        int limit = 120;
        String expectedResponse = "{\"data\": []}";

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(expectedResponse);

        String result = polygonClient.fetchStockData(symbol, fromDate, toDate, limit);

        assertEquals(expectedResponse, result);
    }

    @Test
    void fetchStockData_ApiNotFound_ThrowsPolygonApiException() {
        String symbol = "AAPL";
        String fromDate = "2025-03-13";
        String toDate = "2025-03-14";
        int limit = 120;

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        PolygonApiException exception = assertThrows(PolygonApiException.class, () -> {
            polygonClient.fetchStockData(symbol, fromDate, toDate, limit);
        });
        assertEquals("Stock data not found for symbol: AAPL", exception.getMessage());
    }

    @Test
    void fetchStockData_ApiServerError_ThrowsPolygonApiException() {
        String symbol = "AAPL";
        String fromDate = "2025-03-13";
        String toDate = "2025-03-14";
        int limit = 120;

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        PolygonApiException exception = assertThrows(PolygonApiException.class, () -> {
            polygonClient.fetchStockData(symbol, fromDate, toDate, limit);
        });
        assertEquals("Polygon API server error: 500 INTERNAL_SERVER_ERROR", exception.getMessage());
    }

    @Test
    void fetchStockData_NetworkError_ThrowsPolygonApiException() {
        String symbol = "AAPL";
        String fromDate = "2025-03-13";
        String toDate = "2025-03-14";
        int limit = 120;

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new ResourceAccessException("Network error"));

        PolygonApiException exception = assertThrows(PolygonApiException.class, () -> {
            polygonClient.fetchStockData(symbol, fromDate, toDate, limit);
        });
        assertEquals("Network error when connecting to Polygon API", exception.getMessage());
    }

    @Test
    void fetchStockData_EmptyResponse_ThrowsPolygonApiException() {
        String symbol = "AAPL";
        String fromDate = "2025-03-13";
        String toDate = "2025-03-14";
        int limit = 120;

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn("");

        PolygonApiException exception = assertThrows(PolygonApiException.class, () -> {
            polygonClient.fetchStockData(symbol, fromDate, toDate, limit);
        });
        assertEquals("Unexpected error when fetching stock data: Received empty response from Polygon API", exception.getMessage());
    }

    @Test
    void fetchStockData_RateLimitExceeded_ThrowsPolygonApiException() {
        String symbol = "AAPL";
        String fromDate = "2025-03-13";
        String toDate = "2025-03-14";
        int limit = 120;

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS));

        PolygonApiException exception = assertThrows(PolygonApiException.class, () -> {
            polygonClient.fetchStockData(symbol, fromDate, toDate, limit);
        });
        assertEquals("Rate limit exceeded for Polygon API", exception.getMessage());
    }

    @Test
    void fetchStockData_UnauthorizedAccess_ThrowsPolygonApiException() {
        String symbol = "AAPL";
        String fromDate = "2025-03-13";
        String toDate = "2025-03-14";
        int limit = 120;

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        PolygonApiException exception = assertThrows(PolygonApiException.class, () -> {
            polygonClient.fetchStockData(symbol, fromDate, toDate, limit);
        });
        assertEquals("Authentication error with Polygon API. Check your API key.", exception.getMessage());
    }

    @Test
    void fetchStockData_NullSymbol_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            polygonClient.fetchStockData(null, "2023-01-01", "2023-01-31", 120);
        });
        assertEquals("Stock symbol cannot be null or empty", exception.getMessage());
    }

    @Test
    void fetchStockData_EmptySymbol_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            polygonClient.fetchStockData("", "2023-01-01", "2023-01-31", 120);
        });
        assertEquals("Stock symbol cannot be null or empty", exception.getMessage());
    }

    @Test
    void fetchStockData_NullFromDate_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            polygonClient.fetchStockData("AAPL", null, "2023-01-31", 120);
        });
        assertEquals("From date cannot be null or empty", exception.getMessage());
    }

    @Test
    void fetchStockData_EmptyFromDate_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            polygonClient.fetchStockData("AAPL", "", "2023-01-31", 120);
        });
        assertEquals("From date cannot be null or empty", exception.getMessage());
    }

    @Test
    void fetchStockData_NullToDate_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            polygonClient.fetchStockData("AAPL", "2023-01-01", null, 120);
        });
        assertEquals("To date cannot be null or empty", exception.getMessage());
    }

    @Test
    void fetchStockData_EmptyToDate_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            polygonClient.fetchStockData("AAPL", "2023-01-01", "", 120);
        });
        assertEquals("To date cannot be null or empty", exception.getMessage());
    }

    @Test
    void fetchStockData_DefaultLimit_UsesCorrectLimit() {
        String symbol = "AAPL";
        String fromDate = "2023-01-01";
        String toDate = "2023-01-31";
        String expectedResponse = "{\"data\": []}";

        PolygonClient spyClient = spy(polygonClient);
        doReturn(expectedResponse).when(spyClient).fetchStockData(symbol, fromDate, toDate, 120);

        String result = spyClient.fetchStockData(symbol, fromDate, toDate);

        verify(spyClient).fetchStockData(symbol, fromDate, toDate, 120);
        assertEquals(expectedResponse, result);
    }
}