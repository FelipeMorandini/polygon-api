package com.leadiq.polygonapi.integration;

import com.leadiq.polygonapi.entity.StockPrice;
import com.leadiq.polygonapi.repository.StockPriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDate;

import com.github.tomakehurst.wiremock.client.WireMock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureMockMvc
public class StockPriceControllerIntegrationTest extends PolygonApiMockTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StockPriceRepository stockPriceRepository;

    @BeforeEach
    public void setup() {
        stockPriceRepository.deleteAll();
        // Reset WireMock to ensure clean state for each test
        wireMockServer.resetAll();
    }

    @Test
    public void testFetchAndSaveStockPrices() throws Exception {
        String sampleResponse = "{\n" +
                "  \"ticker\": \"AAPL\",\n" +
                "  \"queryCount\": 1,\n" +
                "  \"resultsCount\": 1,\n" +
                "  \"adjusted\": true,\n" +
                "  \"results\": [\n" +
                "    {\n" +
                "      \"v\": 77287356,\n" +
                "      \"vw\": 173.7209,\n" +
                "      \"o\": 173.97,\n" +
                "      \"c\": 173.57,\n" +
                "      \"h\": 174.3,\n" +
                "      \"l\": 173.12,\n" +
                "      \"t\": 1710374400000,\n" +
                "      \"n\": 445868\n" +
                "    }\n" +
                "  ],\n" +
                "  \"status\": \"OK\",\n" +
                "  \"request_id\": \"test-request-id\",\n" +
                "  \"count\": 1\n" +
                "}";

        // Setup WireMock to match any URL for the API endpoint
        // This is critical to ensure WireMock responds to the request
        wireMockServer.stubFor(
                WireMock.get(WireMock.anyUrl())
                        .willReturn(
                                WireMock.aResponse()
                                        .withStatus(HttpStatus.OK.value())
                                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                        .withBody(sampleResponse)
                        )
        );

        // Execute the test with debug output to diagnose issues
        mockMvc.perform(get("/api/v1/stocks/fetch")
                        .param("companySymbol", "AAPL")
                        .param("fromDate", "2025-03-14")
                        .param("toDate", "2025-03-14"))
                .andDo(MockMvcResultHandlers.print())  // Print details for debugging
                .andExpect(status().isOk());

        // Verify that data was saved to the repository
        StockPrice savedPrice = stockPriceRepository.findByCompanySymbolAndDate("AAPL", LocalDate.parse("2025-03-14"))
                .orElse(null);

        assertNotNull(savedPrice);
        assertEquals("AAPL", savedPrice.getCompanySymbol());
    }

    @Test
    public void testGetStockPrice_WhenExists() throws Exception {
        // Create a stock price in the repository
        StockPrice stockPrice = StockPrice.builder()
                .companySymbol("AAPL")
                .date(LocalDate.parse("2025-03-14"))
                .openPrice(173.97)
                .closePrice(173.57)
                .highPrice(174.3)
                .lowPrice(173.12)
                .volume(77287356L)
                .build();
        stockPriceRepository.save(stockPrice);

        // Test API call
        mockMvc.perform(get("/api/v1/stocks/AAPL").param("date", "2025-03-14"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value("AAPL"))
                .andExpect(jsonPath("$.openPrice").value(173.97));
    }

    @Test
    public void testGetStockPrice_WhenNotExists() throws Exception {
        // Test API call for non-existent data
        mockMvc.perform(get("/api/v1/stocks/MISSING").param("date", "2025-03-14"))
                .andExpect(status().isNotFound());
    }
}