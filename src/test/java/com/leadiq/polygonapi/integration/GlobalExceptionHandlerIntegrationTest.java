package com.leadiq.polygonapi.integration;

import com.leadiq.polygonapi.exception.PolygonApiException;
import com.leadiq.polygonapi.exception.StockDataNotFoundException;
import com.leadiq.polygonapi.exception.StockDataParsingException;
import com.leadiq.polygonapi.service.StockPriceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class GlobalExceptionHandlerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockPriceService stockPriceService;

    @Test
    public void testHandlePolygonApiException() throws Exception {
        // Mock service to throw PolygonApiException
        when(stockPriceService.fetchAndSavePrices(anyString(), anyString(), anyString(), any()))
                .thenThrow(new PolygonApiException("Polygon API connection error"));

        // Execute request and verify response
        mockMvc.perform(get("/api/v1/stocks/fetch")
                        .param("companySymbol", "AAPL")
                        .param("fromDate", "2023-01-01")
                        .param("toDate", "2023-01-31"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.error").value("Polygon API Error"))
                .andExpect(jsonPath("$.message").value("Polygon API connection error"));
    }

    @Test
    public void testHandleStockDataParsingException() throws Exception {
        // Mock service to throw StockDataParsingException
        when(stockPriceService.fetchAndSavePrices(anyString(), anyString(), anyString(), any()))
                .thenThrow(new StockDataParsingException("Error parsing stock data"));

        // Execute request and verify response
        mockMvc.perform(get("/api/v1/stocks/fetch")
                        .param("companySymbol", "AAPL")
                        .param("fromDate", "2023-01-01")
                        .param("toDate", "2023-01-31"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Data Processing Error"))
                .andExpect(jsonPath("$.message").value("Error parsing stock data"));
    }

    @Test
    public void testHandleStockDataNotFoundException() throws Exception {
        // Mock service to throw StockDataNotFoundException
        when(stockPriceService.getStockPrice(anyString(), any(LocalDate.class)))
                .thenThrow(new StockDataNotFoundException("AAPL", LocalDate.of(2023, 1, 15)));

        // Execute request and verify response
        mockMvc.perform(get("/api/v1/stocks/AAPL")
                        .param("date", "2023-01-15"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Stock data not found for symbol 'AAPL' on date '2023-01-15'"));
    }

    @Test
    public void testHandleIllegalArgumentException() throws Exception {
        // Mock service to throw IllegalArgumentException
        when(stockPriceService.fetchAndSavePrices(anyString(), anyString(), anyString(), any()))
                .thenThrow(new IllegalArgumentException("Invalid date format"));

        // Execute request and verify response
        mockMvc.perform(get("/api/v1/stocks/fetch")
                        .param("companySymbol", "AAPL")
                        .param("fromDate", "2023-01-01")
                        .param("toDate", "2023-01-31"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Invalid date format"));
    }

    @Test
    public void testHandleGenericException() throws Exception {
        // Mock service to throw a generic RuntimeException
        when(stockPriceService.fetchAndSavePrices(anyString(), anyString(), anyString(), any()))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Execute request and verify response
        mockMvc.perform(get("/api/v1/stocks/fetch")
                        .param("companySymbol", "AAPL")
                        .param("fromDate", "2023-01-01")
                        .param("toDate", "2023-01-31"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
    }
}