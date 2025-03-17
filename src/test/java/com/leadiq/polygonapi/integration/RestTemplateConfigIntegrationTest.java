package com.leadiq.polygonapi.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RestTemplateConfigIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void testRestTemplateIsConfigured() {
        assertNotNull(restTemplate, "RestTemplate should be configured and available");
    }
}

