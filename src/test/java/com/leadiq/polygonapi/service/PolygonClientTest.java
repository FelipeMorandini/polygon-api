package com.leadiq.polygonapi.service;

import com.leadiq.polygonapi.config.PolygonApiConfig;
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
        // Given
        String symbol = "AAPL";
        String fromDate = "2025-03-13";
        String toDate = "2025-03-14";
        int limit = 120;
        String expectedResponse = "{\"data\": []}";

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(expectedResponse);

        // When
        String result = polygonClient.fetchStockData(symbol, fromDate, toDate, limit);

        // Then
        assertEquals(expectedResponse, result);
    }

    @Test
    void fetchStockData_ApiNotFound_ThrowsPolygonApiException() {
        // Given
        String symbol = "AAPL";
        String fromDate = "2025-03-13";
        String toDate = "2025-03-14";
        int limit = 120;

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // When & Then
        PolygonApiException exception = assertThrows(PolygonApiException.class, () -> {
            polygonClient.fetchStockData(symbol, fromDate, toDate, limit);
        });
        assertEquals("Stock data not found for symbol: AAPL", exception.getMessage());
    }

    @Test
    void fetchStockData_ApiServerError_ThrowsPolygonApiException() {
        // Given
        String symbol = "AAPL";
        String fromDate = "2025-03-13";
        String toDate = "2025-03-14";
        int limit = 120;

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        // When & Then
        PolygonApiException exception = assertThrows(PolygonApiException.class, () -> {
            polygonClient.fetchStockData(symbol, fromDate, toDate, limit);
        });
        assertEquals("Polygon API server error: 500 INTERNAL_SERVER_ERROR", exception.getMessage());
    }

    @Test
    void fetchStockData_NetworkError_ThrowsPolygonApiException() {
        // Given
        String symbol = "AAPL";
        String fromDate = "2025-03-13";
        String toDate = "2025-03-14";
        int limit = 120;

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new ResourceAccessException("Network error"));

        // When & Then
        PolygonApiException exception = assertThrows(PolygonApiException.class, () -> {
            polygonClient.fetchStockData(symbol, fromDate, toDate, limit);
        });
        assertEquals("Network error when connecting to Polygon API", exception.getMessage());
    }
}