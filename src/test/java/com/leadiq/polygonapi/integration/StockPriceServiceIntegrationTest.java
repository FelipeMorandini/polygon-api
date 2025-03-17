package com.leadiq.polygonapi.integration;

import com.leadiq.polygonapi.entity.StockPrice;
import com.leadiq.polygonapi.exception.PolygonApiException;
import com.leadiq.polygonapi.exception.StockDataNotFoundException;
import com.leadiq.polygonapi.exception.StockDataParsingException;
import com.leadiq.polygonapi.repository.StockPriceRepository;
import com.leadiq.polygonapi.service.StockPriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

public class StockPriceServiceIntegrationTest extends PolygonApiMockTest {

    @Autowired
    private StockPriceService stockPriceService;

    @Autowired
    private StockPriceRepository stockPriceRepository;

    @BeforeEach
    public void setup() {
        stockPriceRepository.deleteAll();
        wireMockServer.resetAll();
    }

    @Test
    public void testFetchAndSavePrices_Success() {
        String sampleResponse = "{\n" +
                "  \"ticker\": \"AAPL\",\n" +
                "  \"queryCount\": 1,\n" +
                "  \"resultsCount\": 2,\n" +
                "  \"adjusted\": true,\n" +
                "  \"results\": [\n" +
                "    {\n" +
                "      \"v\": 77287356,\n" +
                "      \"vw\": 173.7209,\n" +
                "      \"o\": 173.97,\n" +
                "      \"c\": 173.57,\n" +
                "      \"h\": 174.3,\n" +
                "      \"l\": 173.12,\n" +
                "      \"t\": 1672531200000,\n" +
                "      \"n\": 445868\n" +
                "    },\n" +
                "    {\n" +
                "      \"v\": 80123456,\n" +
                "      \"vw\": 175.1234,\n" +
                "      \"o\": 174.50,\n" +
                "      \"c\": 176.20,\n" +
                "      \"h\": 176.80,\n" +
                "      \"l\": 174.10,\n" +
                "      \"t\": 1672617600000,\n" +
                "      \"n\": 456789\n" +
                "    }\n" +
                "  ],\n" +
                "  \"status\": \"OK\",\n" +
                "  \"request_id\": \"test-request-id\",\n" +
                "  \"count\": 2\n" +
                "}";

        String symbol = "AAPL";
        String fromDate = "2025-03-13";
        String toDate = "2025-03-14";

        wireMockServer.stubFor(get(urlPathMatching("/v2/aggs/ticker/" + symbol + "/range/1/day/" + fromDate + "/" + toDate))
                .withQueryParam("adjusted", equalTo("true"))
                .withQueryParam("sort", equalTo("asc"))
                .withQueryParam("limit", equalTo("120"))
                .withQueryParam("apiKey", equalTo(polygonApiConfig.getKey()))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(sampleResponse)));

        Page<StockPrice> result = stockPriceService.fetchAndSavePrices(
                symbol, fromDate, toDate, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, stockPriceRepository.count());

        StockPrice firstPrice = result.getContent().get(0);
        assertEquals(symbol, firstPrice.getCompanySymbol());
        assertEquals(LocalDate.of(2025, 3, 13), firstPrice.getDate());
        assertEquals(215.95, firstPrice.getOpenPrice());
        assertEquals(209.68, firstPrice.getClosePrice());

        StockPrice secondPrice = result.getContent().get(1);
        assertEquals(symbol, secondPrice.getCompanySymbol());
        assertEquals(LocalDate.of(2025, 3, 14), secondPrice.getDate());
        assertEquals(211.25, secondPrice.getOpenPrice());
        assertEquals(213.49, secondPrice.getClosePrice());
    }

    @Test
    public void testFetchAndSavePrices_EmptyResults() {
        String emptyResponse = "{\n" +
                "  \"ticker\": \"AAPL\",\n" +
                "  \"queryCount\": 0,\n" +
                "  \"resultsCount\": 0,\n" +
                "  \"adjusted\": true,\n" +
                "  \"results\": [],\n" +
                "  \"status\": \"OK\",\n" +
                "  \"request_id\": \"test-request-id\",\n" +
                "  \"count\": 0\n" +
                "}";

        String symbol = "AAPL";
        String fromDate = "2025-03-01";
        String toDate = "2025-03-02";

        wireMockServer.stubFor(get(urlPathMatching("/v2/aggs/ticker/" + symbol + "/range/1/day/" + fromDate + "/" + toDate))
                .withQueryParam("adjusted", equalTo("true"))
                .withQueryParam("sort", equalTo("asc"))
                .withQueryParam("limit", equalTo("120"))
                .withQueryParam("apiKey", equalTo(polygonApiConfig.getKey()))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(emptyResponse)));

        Page<StockPrice> result = stockPriceService.fetchAndSavePrices(
                symbol, fromDate, toDate, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertEquals(0, stockPriceRepository.count());
    }

    @Test
    public void testGetStockPrice_Success() {
        StockPrice stockPrice = StockPrice.builder()
                .companySymbol("AAPL")
                .date(LocalDate.of(2025, 3, 13))
                .openPrice(173.97)
                .closePrice(173.57)
                .highPrice(174.3)
                .lowPrice(173.12)
                .volume(77287356L)
                .build();

        stockPriceRepository.save(stockPrice);

        StockPrice result = stockPriceService.getStockPrice("AAPL", LocalDate.of(2025, 3, 13));

        assertNotNull(result);
        assertEquals("AAPL", result.getCompanySymbol());
        assertEquals(LocalDate.of(2025, 3, 13), result.getDate());
        assertEquals(173.97, result.getOpenPrice());
        assertEquals(173.57, result.getClosePrice());
    }

    @Test
    public void testGetStockPrice_NotFound() {
        StockDataNotFoundException exception = assertThrows(StockDataNotFoundException.class, () -> {
            stockPriceService.getStockPrice("MISSING", LocalDate.of(2023, 1, 15));
        });

        assertEquals("MISSING", exception.getSymbol());
        assertEquals(LocalDate.of(2023, 1, 15), exception.getDate());
    }

    @Test
    public void testGetStockPrice_InvalidInput() {
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () -> {
            stockPriceService.getStockPrice(null, LocalDate.of(2023, 1, 15));
        });

        assertTrue(exception1.getMessage().contains("Stock symbol cannot be null or empty"));

        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () -> {
            stockPriceService.getStockPrice("AAPL", null);
        });

        assertTrue(exception2.getMessage().contains("Date cannot be null"));
    }
}