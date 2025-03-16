package com.leadiq.polygonapi.service;

import com.leadiq.polygonapi.config.PolygonApiConfig;
import com.leadiq.polygonapi.exception.PolygonApiException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * The PolygonClient class is responsible for communicating with the Polygon API to fetch stock market data.
 * It provides functionality to retrieve aggregated stock data for a given ticker symbol within a specified date range.
 * The class uses a configured API key and handles errors such as authentication, resource not found, rate limiting,
 * server errors, and network issues.
 */
@Service
@RequiredArgsConstructor
public class PolygonClient {
    private final PolygonApiConfig config;
    private static final Logger logger = LoggerFactory.getLogger(PolygonClient.class);
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Fetches stock market data for a given ticker symbol within a specified date range.
     * The method communicates with Polygon API and returns the aggregated stock data.
     *
     * @param symbol   the ticker symbol of the stock (e.g., "AAPL" for Apple Inc.). Cannot be null or empty.
     * @param fromDate the start date for fetching data, in the format "yyyy-MM-dd". Cannot be null or empty.
     * @param toDate   the end date for fetching data, in the format "yyyy-MM-dd". Cannot be null or empty.
     * @param limit    the maximum number of results to fetch. Must be a valid integer.
     * @return a JSON-formatted string containing the stock market data retrieved from the Polygon API.
     * @throws IllegalArgumentException if any of the input parameters are null or invalid.
     * @throws PolygonApiException      if an error occurs during the API call, such as network issues,
     *                                   invalid API key, resource not found, rate limit exceeded, or server errors.
     */
    public String fetchStockData(String symbol, String fromDate, String toDate, int limit) {
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
                "https://api.polygon.io/v2/aggs/ticker/%s/range/1/day/%s/%s?adjusted=true&sort=asc&limit=%d&apiKey=%s",
                symbol, fromDate, toDate, limit, config.getKey()
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

    /**
     * Fetches stock market data with default limit of 120 results.
     */
    public String fetchStockData(String symbol, String fromDate, String toDate) {
        return fetchStockData(symbol, fromDate, toDate, 120);
    }
}