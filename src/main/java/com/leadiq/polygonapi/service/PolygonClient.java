package com.leadiq.polygonapi.service;

import com.leadiq.polygonapi.exception.PolygonApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * The PolygonClient class is a service-level component designed to interact with the Polygon API.
 * It allows fetching aggregated stock market data for a specific ticker symbol and date range.
 * This class handles API authentication, constructs the required API request, and manages error handling
 * for various failure scenarios during the communication with the external API.
 *
 * The main responsibilities of this class include:
 * - Validating input parameters such as stock symbol and date range.
 * - Building HTTP requests to fetch data from the Polygon API.
 * - Handling errors such as authentication issues, rate-limiting, network problems, and API server errors.
 *
 * Instances of this class leverage Spring's dependency injection mechanism to use configuration properties
 * and external dependencies.
 */
@Service
public class PolygonClient {

    private static final Logger logger = LoggerFactory.getLogger(PolygonClient.class);

    @Value("${polygon.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Fetches aggregated stock market data for a given ticker symbol and date range
     * from the Polygon API.
     *
     * @param symbol the stock ticker symbol for which data is to be fetched; must not be null or empty
     * @param fromDate the start date of the data range in yyyy-MM-dd format; must not be null or empty
     * @param toDate the end date of the data range in yyyy-MM-dd format; must not be null or empty
     * @return the stock market data as a JSON string
     * @throws IllegalArgumentException if any of the input parameters are null or empty
     * @throws PolygonApiException if an error occurs while fetching data from the Polygon API, such as
     *                              authentication issues, rate-limiting, or server/network errors
     */
    public String fetchStockData(String symbol, String fromDate, String toDate) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Stock symbol cannot be null or empty");
        }

        if (fromDate == null || fromDate.trim().isEmpty()) {
            throw new IllegalArgumentException("From date cannot be null or empty");
        }

        if (toDate == null || toDate.trim().isEmpty()) {
            throw new IllegalArgumentException("To date cannot be null or empty");
        }

        String url = String.format(
                "https://api.polygon.io/v2/aggs/ticker/%s/range/1/day/%s/%s?adjusted=true&sort=asc&limit=120&apiKey=%s",
                symbol, fromDate, toDate, apiKey
        );

        try {
            logger.info("Fetching stock data for symbol {} from {} to {}", symbol, fromDate, toDate);
            String response = restTemplate.getForObject(url, String.class);

            if (response == null || response.isEmpty()) {
                throw new PolygonApiException("Received empty response from Polygon API");
            }

            return response;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.FORBIDDEN) {
                logger.error("Authentication error with Polygon API. Check your API key.", e);
                throw new PolygonApiException("Authentication error with Polygon API. Check your API key.", e);
            } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.error("Resource not found for symbol: {}", symbol, e);
                throw new PolygonApiException("Stock data not found for symbol: " + symbol, e);
            } else if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                logger.error("Rate limit exceeded for Polygon API", e);
                throw new PolygonApiException("Rate limit exceeded for Polygon API", e);
            } else {
                logger.error("Client error when calling Polygon API: {}", e.getMessage(), e);
                throw new PolygonApiException("Error fetching stock data: " + e.getMessage(), e);
            }
        } catch (HttpServerErrorException e) {
            logger.error("Polygon API server error: {}", e.getMessage(), e);
            throw new PolygonApiException("Polygon API server error: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            logger.error("Network error when connecting to Polygon API", e);
            throw new PolygonApiException("Network error when connecting to Polygon API", e);
        } catch (Exception e) {
            logger.error("Unexpected error when fetching stock data", e);
            throw new PolygonApiException("Unexpected error when fetching stock data: " + e.getMessage(), e);
        }
    }
}