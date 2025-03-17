package com.leadiq.polygonapi.config;

import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Configuration class for OpenAPI tags.
 * This class defines and organizes the tags used to group API endpoints
 * in the generated documentation.
 */
@Configuration
public class OpenApiTagConfig {

    public static final String TAG_STOCK_PRICES = "Stock Prices";

    /**
     * Customizes the OpenAPI documentation by adding and sorting tags.
     *
     * @return an OpenApiCustomizer that adds and sorts tags
     */
    @Bean
    public OpenApiCustomizer sortTagsAlphabetically() {
        return openApi -> {
            List<Tag> tags = List.of(
                    createTag(TAG_STOCK_PRICES, "Operations related to stock prices")
            );

            List<Tag> sortedTags = tags.stream()
                    .sorted(Comparator.comparing(Tag::getName))
                    .collect(Collectors.toList());

            openApi.setTags(sortedTags);
        };
    }

    private Tag createTag(String name, String description) {
        Tag tag = new Tag();
        tag.setName(name);
        tag.setDescription(description);
        return tag;
    }
}