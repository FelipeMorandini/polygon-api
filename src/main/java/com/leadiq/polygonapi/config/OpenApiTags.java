package com.leadiq.polygonapi.config;

import io.swagger.v3.oas.models.tags.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for defining and managing OpenAPI tags used in API documentation.
 * The tags categorize and organize API endpoints to improve readability and
 * usability in the documentation.
 * The tags provided by this class represent high-level categories for API operations,
 * which include stocks, options, market data, administration, and stock prices.
 * These tags can be utilized to group endpoints for better navigation in OpenAPI
 * documentation.
 */
public class OpenApiTags {
    public static final String STOCKS_TAG = "stocks";
    public static final String OPTIONS_TAG = "options";
    public static final String MARKET_DATA_TAG = "market-data";
    public static final String ADMIN_TAG = "admin";
    public static final String STOCK_PRICES_TAG = "Stock Prices";

    /**
     * Retrieves a list of defined tags that categorize and describe different
     * operations related to the application. These tags are used to group and
     * organize API endpoints in the documentation.
     *
     * @return a list of {@link Tag} objects where each tag includes a name and
     *         a description for a specific category of operations.
     */
    public static List<Tag> getTags() {
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag().name(STOCKS_TAG).description("Operations related to stocks"));
        tags.add(new Tag().name(OPTIONS_TAG).description("Operations related to options"));
        tags.add(new Tag().name(MARKET_DATA_TAG).description("Operations related to market data"));
        tags.add(new Tag().name(ADMIN_TAG).description("Administrative operations"));
        tags.add(new Tag().name(STOCK_PRICES_TAG).description("Operations related to stock prices"));
        return tags;
    }
}
