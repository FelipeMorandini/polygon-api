package com.leadiq.polygonapi.config.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "polygon.api")
@Data
public class PolygonApiConfig {
    private String key;
    private String baseUrl;
    private int timeout = 30000;
    private int maxRetries = 3;
}
