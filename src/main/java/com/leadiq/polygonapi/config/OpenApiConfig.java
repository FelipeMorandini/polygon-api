package com.leadiq.polygonapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.leadiq.polygonapi.config.OpenApiTags.getTags;
import static com.leadiq.polygonapi.config.ValidationSchemas.getCommonSchemas;

/**
 * OpenApiConfig is a configuration class for setting up OpenAPI specifications and server
 * URLs for the application. This class handles the initialization of important metadata,
 * server environments, and OpenAPI components to support API documentation and client integrations.
 * It integrates with the Spring Framework to retrieve required properties, establish server
 * configurations, and validate the completeness of the OpenAPI setup. The validation process
 * helps ensure that the application is properly configured for different environments (development,
 * staging, production) and appropriate API documentation is generated.
 * The key responsibilities of this class are:
 * - Validating the configuration for OpenAPI setup during application initialization.
 * - Defining metadata, servers, security schemes, and reusable schemas for the OpenAPI specification.
 * - Dynamically configuring server URLs based on active Spring profiles.
 */
@Configuration
public class OpenApiConfig {
    private final String applicationName;
    private final String defaultServerUrl;
    private final String devServerUrl;
    private final String stagingServerUrl;
    private final String prodServerUrl;
    private final Environment environment;
    private final OpenApiProperties properties;
    private static final Logger logger = LoggerFactory.getLogger(OpenApiConfig.class);

    /**
     * Constructs an instance of the OpenApiConfig class, initializing the application name,
     * server URLs for different environments, and OpenAPI properties. These values are
     * typically injected from application properties or determined by the server configuration.
     *
     * @param applicationName the name of the application, injected from the property "spring.application.name"
     * @param serverUrlConfig an instance of ServerUrlConfig containing server URLs for different environments
     * @param environment     the spring Environment object that provides the current active profiles and properties
     * @param properties      an instance of OpenApiProperties containing OpenAPI-specific settings and configurations
     */
    public OpenApiConfig(
            @Value("${spring.application.name}") String applicationName,
            ServerUrlConfig serverUrlConfig,
            Environment environment,
            OpenApiProperties properties) {
        this.applicationName = applicationName;
        this.defaultServerUrl = serverUrlConfig.getDefaultUrl();
        this.devServerUrl = serverUrlConfig.getDevUrl();
        this.stagingServerUrl = serverUrlConfig.getStagingUrl();
        this.prodServerUrl = serverUrlConfig.getProdUrl();
        this.environment = environment;
        this.properties = properties;
    }

    /**
     * Validates the OpenAPI configuration of the application. This method is executed
     * automatically after the object creation and ensures that the default server URL
     * is set. It also checks the presence of development, staging, and production server
     * URLs, logging warnings if any of these values are not configured.
     * If the default server URL is missing, the method throws an IllegalStateException
     * to prevent the application from running with incomplete configuration.
     * Logging is used to provide feedback on the validation process, including warnings
     * for missing optional server URLs and a success message when the validation is
     * completed successfully.
     * This method is intended to ensure API documentation consistency and proper server
     * setup for various environments before the application is fully initialized.
     * Throws:
     * - IllegalStateException: If the default server URL is not configured.
     */
    @PostConstruct
    public void validateConfiguration() {
        logger.info("Validating OpenAPI configuration");
        if (!StringUtils.hasText(defaultServerUrl)) {
            throw new IllegalStateException("Default server URL must be configured");
        }

        if (!StringUtils.hasText(devServerUrl)) {
            logger.warn("[{}] Development server URL is not configured", applicationName);
        }

        if (!StringUtils.hasText(stagingServerUrl)) {
            logger.warn("[{}] Staging server URL is not configured", applicationName);
        }

        if (!StringUtils.hasText(prodServerUrl)) {
            logger.warn("[{}] Production server URL is not configured", applicationName);
        }

        logger.info("[{}] OpenAPI configuration validated successfully", applicationName);
    }

    /**
     * Configures and builds a custom OpenAPI bean for the application. This method
     * sets up various elements of the OpenAPI specification, including the API's
     * metadata, servers, security schemes, reusable schemas, and tags. The method
     * enables integration with the OpenAPI documentation tools and provides detailed
     * information about the API for clients.
     *
     * @return an {@link OpenAPI} object representing the OpenAPI specification for
     *         the application, including metadata, server configurations, security
     *         specifications, reusable schemas, and tag definitions.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        logger.info("[{}] Configuring OpenAPI documentation", applicationName);
        Components components = new Components()
                .addSecuritySchemes("bearer-key",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"));

        logger.debug("Adding common validation schemas to OpenAPI components");

        getCommonSchemas().forEach(components::addSchemas);

        return new OpenAPI()
                .info(new Info()
                        .title(applicationName)
                        .version(properties.getVersion())
                        .description("This API provides access to stock market data through the Polygon.io service.")
                        .termsOfService(properties.getTermsOfServiceUrl())
                        .contact(new Contact()
                                .name(properties.getContact().name())
                                .url(properties.getContact().url())
                                .email(properties.getContact().email()))
                        .license(new License()
                                .name(properties.getLicense().name())
                                .url(properties.getLicense().url())))
                .servers(getServers())
                .components(components)
                .tags(getTags())
                .addSecurityItem(new SecurityRequirement().addList("bearer-key"));
    }

    /**
     * Retrieves a list of servers based on the active application profiles.
     * The method defines a default server and adds profile-specific servers
     * for "dev", "staging", and "prod" environments, if applicable.
     *
     * @return a list of {@link Server} objects where each server includes
     *         a URL and a description corresponding to the active profiles
     *         and the default configuration.
     */
    private List<Server> getServers() {
        List<Server> servers = new ArrayList<>();

        logger.debug("Adding default server URL: {}", defaultServerUrl);

        servers.add(new Server().url(defaultServerUrl).description("Default Server"));

        String[] activeProfiles = environment.getActiveProfiles();

        logger.info("Configuring servers for active profiles: {}", String.join(", ", activeProfiles));

        for (String profile : activeProfiles) {
            switch (profile) {
                case "dev":
                    logger.debug("Adding development server URL: {}", devServerUrl);
                    servers.add(new Server().url(devServerUrl).description("Development Server"));
                    break;
                case "staging":
                    logger.debug("Adding staging server URL: {}", stagingServerUrl);
                    servers.add(new Server().url(stagingServerUrl).description("Staging Server"));
                    break;
                case "prod":
                    logger.debug("Adding production server URL: {}", prodServerUrl);
                    servers.add(new Server().url(prodServerUrl).description("Production Server"));
                    break;
            }
        }

        return servers;
    }
}
