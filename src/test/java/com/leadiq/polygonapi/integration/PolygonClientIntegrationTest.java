package com.leadiq.polygonapi.integration;

import com.leadiq.polygonapi.exception.PolygonApiException;
import com.leadiq.polygonapi.service.PolygonClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

public class PolygonClientIntegrationTest extends PolygonApiMockTest {

    @Autowired
    private PolygonClient polygonClient;

    @Test
    public void testFetchStockData_Success() {
        // Sample response
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

        // Setup mock
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

        // Execute test
        String response = polygonClient.fetchStockData(symbol, fromDate, toDate);

        // Verify
        assertNotNull(response);
        assertTrue(response.contains("AAPL"));
        assertTrue(response.contains("OK"));
    }
}
