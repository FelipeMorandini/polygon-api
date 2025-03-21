package com.leadiq.polygonapi.config.openapi;

import io.swagger.v3.oas.models.media.Schema;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code ValidationSchemas} class provides utility methods for generating commonly
 * used JSON schemas for data validation. These schemas can be employed in various
 * contexts to validate inputs or responses adhering to specific formats or rules.
 */
public class ValidationSchemas {

    /**
     * Retrieves a map of commonly used JSON schemas for validation purposes.
     * The schemas include predefined patterns and formats, such as a stock ticker
     * symbol schema, an ISO date schema, and a pagination schema.
     *
     * @return a map where the keys are schema identifiers (e.g., "TickerSymbol",
     *         "ISODate", "Pagination") and the values are corresponding schema
     *         definitions.
     */
    public static Map<String, Schema<?>> getCommonSchemas() {
        Map<String, Schema<?>> schemas = new HashMap<>();

        Schema<String> tickerSchema = new Schema<>();
        tickerSchema.type("string");
        tickerSchema.pattern("^[A-Z]{1,5}$");
        tickerSchema.setDescription("Stock ticker symbol (1-5 uppercase letters)");
        schemas.put("TickerSymbol", tickerSchema);

        Schema<String> dateSchema = new Schema<>();
        dateSchema.type("string");
        dateSchema.format("date");
        dateSchema.pattern("^\\d{4}-\\d{2}-\\d{2}$");
        dateSchema.setDescription("Date in ISO format (YYYY-MM-DD)");
        schemas.put("ISODate", dateSchema);

        Schema<Object> paginationSchema = getObjectSchema();

        schemas.put("Pagination", paginationSchema);

        return schemas;
    }

    /**
     * Creates and returns a predefined JSON schema representing an object with pagination properties.
     * The schema includes two properties: "page", which denotes the page number, and "size", which
     * specifies the number of items per page. The "page" property is an integer with a minimum value
     * of 0 and a default value of 0. The "size" property is an integer with a minimum value of 1,
     * a maximum value of 100, and a default value of 20.
     *
     * @return a {@link Schema} object representing a pagination schema with properties for "page"
     *         and "size"
     */
    private static Schema<Object> getObjectSchema() {
        Schema<Object> paginationSchema = new Schema<>();
        paginationSchema.type("object");

        Schema<Integer> pageSchema = new Schema<>();
        pageSchema.type("integer");
        pageSchema.setMinimum(BigDecimal.ZERO);
        pageSchema.setDefault(0);

        Schema<Integer> sizeSchema = new Schema<>();
        sizeSchema.type("integer");
        sizeSchema.setMinimum(BigDecimal.ONE);
        sizeSchema.setMaximum(BigDecimal.valueOf(100));
        sizeSchema.setDefault(20);

        paginationSchema.addProperty("page", pageSchema);
        paginationSchema.addProperty("size", sizeSchema);
        return paginationSchema;
    }
}
