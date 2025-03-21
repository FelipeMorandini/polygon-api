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
 * Configuration class for setting up OpenAPI documentation in the application.
 * This class defines the settings and components for the OpenAPI specification,
 * including metadata, servers, security schemes, and reusable schemas. It also
 * validates the required server configuration during application startup to
 * ensure proper environment setup.
 * Features:
 * - Provides descriptions and metadata for the API, including title, version,
 *   contact information, and license information.
 * - Configures security schemes for securing the API.
 * - Automatically sets up server details based on active Spring profiles.
 * - Validates the server configuration to ensure completeness and consistency.
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
     * Constructs an OpenApiConfig instance, initializing the configuration of the OpenAPI
     * documentation with application-specific details such as server URLs, environment
     * metadata, and application properties.
     *
     * @param applicationName the name of the application, injected from the configuration property `spring.application.name`
     * @param defaultServerUrl the default server URL for the OpenAPI documentation, injected from the configuration property `springdoc.server.url`
     * @param devServerUrl the server URL specific to the development environment, injected from the configuration property `springdoc.server.dev.url`
     * @param stagingServerUrl the server URL specific to the staging environment, injected from the configuration property `springdoc.server.staging.url`
     * @param prodServerUrl the server URL specific to the production environment, injected from the configuration property `springdoc.server.prod.url`
     * @param environment an instance of the {@link Environment} that provides access to application profiles and environment properties
     * @param properties an instance of {@link OpenApiProperties} that holds the OpenAPI configuration properties such as metadata and contact information
     */
    public OpenApiConfig(
            @Value("${spring.application.name}") String applicationName,
            @Value("${springdoc.server.url}") String defaultServerUrl,
            @Value("${springdoc.server.dev.url}") String devServerUrl,
            @Value("${springdoc.server.staging.url}") String stagingServerUrl,
            @Value("${springdoc.server.prod.url}") String prodServerUrl,
            Environment environment,
            OpenApiProperties properties) {
        this.applicationName = applicationName;
        this.defaultServerUrl = defaultServerUrl;
        this.devServerUrl = devServerUrl;
        this.stagingServerUrl = stagingServerUrl;
        this.prodServerUrl = prodServerUrl;
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
            logger.warn("Development server URL is not configured");
        }

        if (!StringUtils.hasText(stagingServerUrl)) {
            logger.warn("Staging server URL is not configured");
        }

        if (!StringUtils.hasText(prodServerUrl)) {
            logger.warn("Production server URL is not configured");
        }

        logger.info("OpenAPI configuration validated successfully");
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
        logger.info("Configuring OpenAPI documentation for {}", applicationName);
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
                                .name(properties.getContact().getName())
                                .url(properties.getContact().getUrl())
                                .email(properties.getContact().getEmail()))
                        .license(new License()
                                .name(properties.getLicense().getName())
                                .url(properties.getLicense().getUrl())))
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
