package com.leadiq.polygonapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Represents the configuration properties for OpenAPI. This class encapsulates metadata
 * such as the version, terms of service URL, contact information, and license details.
 * The class is annotated to load properties with the specified prefix from the application's
 * configuration file.
 */
@Component
@ConfigurationProperties(prefix = "openapi")
public class OpenApiProperties {
    private String version = "1.0.0";
    private String termsOfServiceUrl = "https://www.example.com/terms";
    private Contact contact = new Contact();
    private License license = new License();

    /**
     * Represents the contact information associated with an API or service.
     * This class provides fields and methods to access and configure the name,
     * URL, and email address for the contact information.
     */
    public static class Contact {
        private String name = "API Support";
        private String url = "https://www.example.com/support";
        private String email = "support@example.com";

        /**
         * Retrieves the name associated with the contact.
         *
         * @return the name of the contact as a String
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the name for the contact.
         *
         * @param name the name to be set
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Retrieves the URL associated with the contact.
         *
         * @return the URL as a String
         */
        public String getUrl() {
            return url;
        }

        /**
         * Sets the URL for the contact.
         *
         * @param url the URL address to be set
         */
        public void setUrl(String url) {
            this.url = url;
        }

        /**
         * Retrieves the email address of the contact.
         *
         * @return the email address as a String
         */
        public String getEmail() {
            return email;
        }

        /**
         * Sets the email address for the contact.
         *
         * @param email the email address to be set
         */
        public void setEmail(String email) {
            this.email = email;
        }
    }

    /**
     * Represents the license information associated with an API or service.
     * This class defines fields and methods to access and modify the name
     * and URL of the license.
     */
    public static class License {
        private String name = "Apache 2.0";
        private String url = "https://www.apache.org/licenses/LICENSE-2.0.html";

        /**
         * Retrieves the name of the license.
         *
         * @return the name of the license as a string
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the name for the license.
         *
         * @param name the name to be set for the license
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Retrieves the URL associated with the license.
         *
         * @return the URL as a string
         */
        public String getUrl() {
            return url;
        }

        /**
         * Sets the URL associated with the license.
         *
         * @param url the URL to be set for the license
         */
        public void setUrl(String url) {
            this.url = url;
        }
    }

    /**
     * Retrieves the version of the OpenAPI properties.
     *
     * @return the version as a string
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version of the OpenAPI properties.
     *
     * @param version the version to be set as a String
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Retrieves the terms of service URL for the OpenAPI configuration.
     *
     * @return the terms of service URL as a string
     */
    public String getTermsOfServiceUrl() {
        return termsOfServiceUrl;
    }

    /**
     * Sets the terms of service URL for the OpenAPI configuration.
     *
     * @param termsOfServiceUrl the URL of the terms of service to be set
     */
    public void setTermsOfServiceUrl(String termsOfServiceUrl) {
        this.termsOfServiceUrl = termsOfServiceUrl;
    }

    /**
     * Retrieves the contact information associated with the OpenAPI properties.
     *
     * @return the contact details as a Contact object
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * Sets the contact information for the OpenAPI properties.
     *
     * @param contact the contact details to be set as a Contact object
     */
    public void setContact(Contact contact) {
        this.contact = contact;
    }

    /**
     * Retrieves the license information associated with the OpenAPI properties.
     *
     * @return the license information as a License object
     */
    public License getLicense() {
        return license;
    }

    /**
     * Sets the license information for the OpenAPI properties.
     *
     * @param license the license details to be set as a License object
     */
    public void setLicense(License license) {
        this.license = license;
    }
}
