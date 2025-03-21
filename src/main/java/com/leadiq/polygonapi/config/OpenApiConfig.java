package com.leadiq.polygonapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import java.util.ArrayList;
import java.util.List;

import static com.leadiq.polygonapi.config.OpenApiTags.getTags;
import static com.leadiq.polygonapi.config.ValidationSchemas.getCommonSchemas;

/**
 * Configuration class for setting up the OpenAPI specification for the application.
 * This class defines the necessary beans and logic to customize and generate OpenAPI
 * documentation. It leverages application properties, runtime profiles, and custom
 * metadata to build the OpenAPI specification dynamically.
 * The configuration includes:
 * - API metadata such as title, version, description, contact information, and license.
 * - Server definitions based on active profiles (e.g., development, staging, production).
 * - Security schemes, reusable schemas, and custom tags.
 * Integrates with Springdoc OpenAPI and supports JWT bearer token as the default
 * security scheme.
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:Polygon API}")
    private String applicationName;

    @Value("${springdoc.server.url:http://localhost:8080}")
    private String defaultServerUrl;

    @Value("${springdoc.server.dev.url:http://localhost:8080}")
    private String devServerUrl;

    @Value("${springdoc.server.staging.url:https://staging-api.example.com}")
    private String stagingServerUrl;

    @Value("${springdoc.server.prod.url:https://api.example.com}")
    private String prodServerUrl;

    @Autowired
    private Environment environment;

    @Autowired
    private OpenApiProperties properties;

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
        Components components = new Components()
                .addSecuritySchemes("bearer-key",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"));

        // Add validation schemas
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

        servers.add(new Server().url(defaultServerUrl).description("Default Server"));

        String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            switch (profile) {
                case "dev":
                    servers.add(new Server().url(devServerUrl).description("Development Server"));
                    break;
                case "staging":
                    servers.add(new Server().url(stagingServerUrl).description("Staging Server"));
                    break;
                case "prod":
                    servers.add(new Server().url(prodServerUrl).description("Production Server"));
                    break;
            }
        }

        return servers;
    }
}
