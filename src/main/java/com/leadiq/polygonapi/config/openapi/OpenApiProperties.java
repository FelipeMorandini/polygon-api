package com.leadiq.polygonapi.config.openapi;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for OpenAPI. This class encapsulates metadata related to the OpenAPI specification,
 * including the API version, terms of service URL, contact information, and licensing details. These properties
 * are typically used to enrich the API documentation with crucial information for users.
 */
@Getter
@ConfigurationProperties(prefix = "openapi")
public class OpenApiProperties {
    /**
     * -- GETTER --
     *  Retrieves the version of the OpenAPI properties.
     */
    private final String version;
    /**
     * -- GETTER --
     *  Retrieves the terms of service URL for the OpenAPI configuration.
     */
    private final String termsOfServiceUrl;
    /**
     * -- GETTER --
     *  Retrieves the contact information associated with the OpenAPI properties.
     */
    private final Contact contact;
    /**
     * -- GETTER --
     *  Retrieves the license information associated with the OpenAPI properties.
     */
    private final License license;

    /**
     * Constructs an instance of the OpenApiProperties class with specified values
     * for version, terms of service URL, contact information, and license details.
     *
     * @param version the version of the OpenAPI properties.
     * @param termsOfServiceUrl the URL for the terms of service associated with the OpenAPI configuration.
     * @param contact the contact information relevant to the OpenAPI properties.
     * @param license the license details associated with the OpenAPI configuration.
     */
    public OpenApiProperties(String version, String termsOfServiceUrl, Contact contact, License license) {
        this.version = version;
        this.termsOfServiceUrl = termsOfServiceUrl;
        this.contact = contact;
        this.license = license;
    }

    /**
     * Represents contact information for OpenAPI properties.
     * This record encapsulates details about a contact person or organization,
     * including a name, a URL for further information, and an email address for communication.
     * The contact information helps users to reach out with queries or concerns
     * regarding the API or its usage. Typically included in API documentation as part
     * of OpenAPI metadata.
     * @param name the name of the contact person or organization.
     * @param url the URL providing additional details about the contact or organization.
     * @param email the email address for contacting the person or organization.
     */
    public record Contact(String name, String url, String email) {}

    /**
     * Represents licensing information that is associated with the OpenAPI configuration.
     * This record encapsulates the name of the license and a corresponding URL that provides
     * further details about the license.
     * The licensing information helps in specifying the terms and conditions under which
     * the API can be used. It is commonly included in the OpenAPI documentation to indicate
     * the legal aspects of using the API.
     * @param name the name of the license.
     * @param url the URL for the full text or details about the license.
     */
    public record License(String name, String url) {}
}
