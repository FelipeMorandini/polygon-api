package com.leadiq.polygonapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.leadiq.polygonapi.config.client.PolygonApiConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public abstract class PolygonApiMockTest extends BaseIntegrationTest {

    protected static WireMockServer wireMockServer;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected PolygonApiConfig polygonApiConfig;

    @TestConfiguration
    static class PolygonApiTestConfig {

        @Value("${polygon.api.key}")
        private String apiKey;

        @Bean
        @Primary
        public PolygonApiConfig testPolygonApiConfig() {
            PolygonApiConfig config = new PolygonApiConfig();
            config.setBaseUrl("http://localhost:9999");
            config.setKey(apiKey);
            config.setTimeout(1000);
            config.setMaxRetries(1);
            return config;
        }
    }

    @BeforeEach
    void setupWireMock() {
        wireMockServer = new WireMockServer(wireMockConfig().port(9999));
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterEach
    void tearDownWireMock() {
        wireMockServer.stop();
    }

    protected void setupMockSuccessResponse(String symbol, String fromDate, String toDate, String responseBody) {
        wireMockServer.stubFor(get(urlPathMatching("/v2/aggs/ticker/" + symbol + "/range/1/day/" + fromDate + "/" + toDate))
                .withQueryParam("adjusted", equalTo("true"))
                .withQueryParam("sort", equalTo("asc"))
                .withQueryParam("limit", equalTo("120"))
                .withQueryParam("apiKey", equalTo(polygonApiConfig.getKey()))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(responseBody)));
    }


    protected void setupMockErrorResponse(String symbol, int statusCode, String errorMessage) {
        wireMockServer.stubFor(get(urlPathMatching("/v2/aggs/ticker/" + symbol + "/range/1/day/.+/.+"))
                .withQueryParam("adjusted", equalTo("true"))
                .withQueryParam("sort", equalTo("asc"))
                .withQueryParam("limit", equalTo("120"))
                .withQueryParam("apiKey", equalTo(polygonApiConfig.getKey()))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"status\":\"ERROR\", \"message\":\"" + errorMessage + "\"}")));
    }

}