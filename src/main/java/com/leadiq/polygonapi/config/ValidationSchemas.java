package com.leadiq.polygonapi.config;

import io.swagger.v3.oas.models.media.Schema;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * The ValidationSchemas class provides predefined and reusable JSON schemas that
 * are commonly used for API validation. These schemas simplify the validation
 * process by defining constraints and formats for frequently used field types,
 * such as stock ticker symbols, ISO date formats, and pagination parameters.
 * The class includes methods to retrieve these schemas as a map, enabling easy
 * integration with OpenAPI or other schema-based validation frameworks.
 */
public class ValidationSchemas {

    /**
     * Retrieves a map of common schemas used for API validation. These schemas
     * define reusable constraints and descriptions for fields like stock ticker
     * symbols, dates in ISO format, and pagination settings. Each schema is
     * identified by a unique key for easy reference.
     *
     * @return a map where the keys are schema identifiers (e.g., "TickerSymbol",
     *         "ISODate", "Pagination") and the values are {@link Schema} objects
     *         representing the corresponding validation definitions.
     */
    public static Map<String, Schema<?>> getCommonSchemas() {
        Map<String, Schema<?>> schemas = new HashMap<>();

        // Stock ticker pattern
        Schema<String> tickerSchema = new Schema<String>()
                .type("string")
                .pattern("^[A-Z]{1,5}$");
        tickerSchema.setDescription("Stock ticker symbol (1-5 uppercase letters)");
        schemas.put("TickerSymbol", tickerSchema);

        Schema<String> dateSchema = new Schema<String>()
                .type("string")
                .format("date")
                .pattern("^\\d{4}-\\d{2}-\\d{2}$");
        dateSchema.setDescription("Date in ISO format (YYYY-MM-DD)");
        schemas.put("ISODate", dateSchema);

        Schema<Object> paginationSchema = new Schema<Object>()
                .type("object");

        Schema<Integer> pageSchema = new Schema<Integer>()
                .type("integer");
        pageSchema.setMinimum(BigDecimal.ZERO);
        pageSchema.setDefault(0);

        Schema<Integer> sizeSchema = new Schema<Integer>()
                .type("integer");
        sizeSchema.setMinimum(BigDecimal.ONE);
        sizeSchema.setMaximum(BigDecimal.valueOf(100));
        sizeSchema.setDefault(20);

        paginationSchema.addProperty("page", pageSchema);
        paginationSchema.addProperty("size", sizeSchema);

        schemas.put("Pagination", paginationSchema);

        return schemas;
    }
}
