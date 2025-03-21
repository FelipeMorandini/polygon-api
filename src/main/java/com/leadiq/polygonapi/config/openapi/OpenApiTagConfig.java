package com.leadiq.polygonapi.config.openapi;

import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Configuration class for customizing OpenAPI tags in the API documentation.
 * This class ensures that tags are sorted alphabetically within the generated
 * OpenAPI specification to improve navigation and readability.
 * The tags are defined in the {@link OpenApiTags} utility class, which provides
 * a centralized set of categories for grouping API endpoints.
 */
@Configuration
public class OpenApiTagConfig {

    public static final String TAG_STOCK_PRICES = OpenApiTags.STOCK_PRICES_TAG;

    /**
     * Customizes the OpenAPI specification by sorting the tags alphabetically.
     * This ensures that the tags in the generated API documentation appear
     * in a consistent, sorted order, improving readability and usability.
     *
     * @return an {@link OpenApiCustomizer} instance that modifies the OpenAPI
     *         tags to be sorted alphabetically by their names.
     */
    @Bean
    public OpenApiCustomizer sortTagsAlphabetically() {
        return openApi -> {
            openApi.setTags(
                    OpenApiTags.getTags().stream()
                            .sorted(Comparator.comparing(Tag::getName))
                            .collect(Collectors.toList())
            );
        };
    }
}
