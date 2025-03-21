package com.leadiq.polygonapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuration class used for defining and managing server URLs across
 * different environments such as default, development, staging, and production.
 * These URLs are typically injected from application properties and are utilized
 * throughout the application for defining environment-specific server endpoints.
 * This class is designed to provide easy access to these URLs, ensuring that
 * the correct server URL is used based on the running environment.
 */
@Component
public class ServerUrlConfig {
    private final String defaultUrl;
    private final String devUrl;
    private final String stagingUrl;
    private final String prodUrl;

    /**
     * Constructs a new instance of the ServerUrlConfig class and initializes the
     * server URLs for different environments including default, development,
     * staging, and production environments. These values are injected via
     * application properties.
     *
     * @param defaultUrl the default server URL, typically used when no specific
     *                   environment URL is provided (e.g., <a href="http://localhost:8080">...</a>).
     * @param devUrl     the server URL designated for the development environment.
     * @param stagingUrl the server URL designated for the staging environment.
     * @param prodUrl    the server URL designated for the production environment.
     */
    public ServerUrlConfig(
            @Value("${springdoc.server.url:http://localhost:8080}") String defaultUrl,
            @Value("${springdoc.server.dev.url:}") String devUrl,
            @Value("${springdoc.server.staging.url:}") String stagingUrl,
            @Value("${springdoc.server.prod.url:}") String prodUrl) {
        this.defaultUrl = defaultUrl;
        this.devUrl = devUrl;
        this.stagingUrl = stagingUrl;
        this.prodUrl = prodUrl;
    }

    /**
     * Retrieves the default server URL configured for the application. This URL
     * typically serves as the base URL for API documentation or application endpoints.
     *
     * @return the default server URL as a String
     */
    public String getDefaultUrl() { return defaultUrl; }

    /**
     * Retrieves the developer-specific server URL configured for the application.
     * This URL is typically used for development environments and may differ from
     * production or staging URLs.
     *
     * @return the developer-specific server URL as a String
     */
    public String getDevUrl() { return devUrl; }

    /**
     * Retrieves the staging server URL configured for the application.
     * This URL is typically used for staging environments where applications
     * can be tested before moving to production.
     *
     * @return the staging server URL as a String
     */
    public String getStagingUrl() { return stagingUrl; }

    /**
     * Retrieves the production server URL configured for the application.
     * This URL is typically used in production environments where
     * live application services are provided to end users.
     *
     * @return the production server URL as a String
     */
    public String getProdUrl() { return prodUrl; }
}
