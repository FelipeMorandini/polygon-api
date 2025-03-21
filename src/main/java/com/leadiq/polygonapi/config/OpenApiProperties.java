package com.leadiq.polygonapi.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration class for defining OpenAPI properties. This class is used to
 * load and manage OpenAPI-related metadata such as API version, terms of
 * service URL, contact information, and license information. These properties
 * are standardized and immutable, providing consistency in API documentation.
 * The properties are loaded with a predefined prefix "openapi" for clear and
 * structured configuration through external configuration files and environments.
 */
@Getter
@Component
@ConfigurationProperties(prefix = "openapi")
public class OpenApiProperties {
    /**
     * -- GETTER --
     *  Retrieves the version of the OpenAPI properties.
     */
    private final String version = "1.0.0";
    /**
     * -- GETTER --
     *  Retrieves the terms of service URL for the OpenAPI configuration.
     */
    private final String termsOfServiceUrl = "https://www.example.com/terms";
    /**
     * -- GETTER --
     *  Retrieves the contact information associated with the OpenAPI properties.
     */
    private final Contact contact = new Contact();
    /**
     * -- GETTER --
     *  Retrieves the license information associated with the OpenAPI properties.
     */
    private final License license = new License();

    /**
     * Represents contact information associated with the OpenAPI properties.
     * This class provides immutable fields to store the contact's name, URL,
     * and email address. The values are pre-defined and cannot be modified.
     */
    @Getter
    public static class Contact {
        /**
         * -- GETTER --
         *  Retrieves the name associated with the contact.
         */
        private final String name = "API Support";
        /**
         * -- GETTER --
         *  Retrieves the URL associated with the contact.
         */
        private final String url = "https://www.example.com/support";
        /**
         * -- GETTER --
         *  Retrieves the email address of the contact.
         */
        private final String email = "support@example.com";
    }

    /**
     * Represents the license information associated with an API or service.
     * This class provides immutable fields to store the license name and
     * its corresponding URL. The values are pre-defined and cannot be modified.
     */
    @Getter
    public static class License {
        /**
         * -- GETTER --
         *  Retrieves the name of the license.
         */
        private final String name = "Apache 2.0";
        /**
         * -- GETTER --
         *  Retrieves the URL associated with the license.
         */
        private final String url = "https://www.apache.org/licenses/LICENSE-2.0.html";
    }
}
