package com.leadiq.polygonapi.integration;

import com.leadiq.polygonapi.config.PolygonApiConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PolygonApiConfigIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private PolygonApiConfig polygonApiConfig;

    @Test
    public void testPolygonApiConfigProperties() {
        assertNotNull(polygonApiConfig, "PolygonApiConfig should be configured and available");

        // In test environment, these values come from the BaseIntegrationTest configuration
        assertEquals("http://localhost:9999", polygonApiConfig.getBaseUrl());

        // These values should come from the test configuration in PolygonApiMockTest
        assertNotNull(polygonApiConfig.getKey(), "API key should not be null");
        assertEquals(30000, polygonApiConfig.getTimeout());
        assertEquals(3, polygonApiConfig.getMaxRetries());
    }
}

