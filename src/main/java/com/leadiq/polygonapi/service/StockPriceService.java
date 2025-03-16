package com.leadiq.polygonapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leadiq.polygonapi.entity.StockPrice;
import com.leadiq.polygonapi.exception.PolygonApiException;
import com.leadiq.polygonapi.exception.StockDataNotFoundException;
import com.leadiq.polygonapi.exception.StockDataParsingException;
import com.leadiq.polygonapi.repository.StockPriceRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for handling stock price data, including fetching, parsing, saving,
 * and retrieving stock prices from and to the repository or external API.
 */
@Service
@RequiredArgsConstructor
public class StockPriceService {

    private static final Logger logger = LoggerFactory.getLogger(StockPriceService.class);

    private final StockPriceRepository stockPriceRepository;
    private final PolygonClient polygonClient;

    /**
     * Fetches stock price data for a given stock symbol within a specified date range,
     * parses the data, and saves it to the repository. Returns the list of saved stock prices.
     *
     * @param symbol The stock symbol for which to fetch the price data. Cannot be null or empty.
     * @param fromDate The start date of the range for which to fetch stock price data. Cannot be null or empty.
     * @param toDate The end date of the range for which to fetch stock price data. Cannot be null or empty.
     * @return A list of StockPrice objects that were successfully fetched and saved.
     * @throws IllegalArgumentException If the input parameters are null or empty.
     * @throws PolygonApiException If there is an error while fetching data from the Polygon API.
     * @throws RuntimeException If an unexpected error occurs during processing.
     */
    public List<StockPrice> fetchAndSavePrices(String symbol, String fromDate, String toDate) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Stock symbol cannot be null or empty");
        }

        if (fromDate == null || fromDate.trim().isEmpty()) {
            throw new IllegalArgumentException("From date cannot be null or empty");
        }

        if (toDate == null || toDate.trim().isEmpty()) {
            throw new IllegalArgumentException("To date cannot be null or empty");
        }

        logger.info("Fetching stock prices for symbol {} from {} to {}", symbol, fromDate, toDate);

        try {
            String polygonResponse = polygonClient.fetchStockData(symbol, fromDate, toDate);
            List<StockPrice> stockPrices = parsePolygonResponse(symbol, polygonResponse);

            if (stockPrices.isEmpty()) {
                logger.warn("No stock price data found for symbol {} in the specified date range", symbol);
                return stockPrices;
            }

            logger.info("Saving {} stock price records for symbol {}", stockPrices.size(), symbol);
            return stockPriceRepository.saveAll(stockPrices);
        } catch (PolygonApiException e) {
            logger.error("Error fetching stock data from Polygon API", e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in fetchAndSavePrices", e);
            throw new RuntimeException("Error processing stock price data: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves the stock price for a given company symbol on a specific date.
     *
     * @param symbol the stock symbol of the company for which the price is to be retrieved; must not be null or empty
     * @param date the date for which the stock price is to be retrieved; must not be null
     * @return the stock price for the specified company symbol and date
     * @throws IllegalArgumentException if the symbol is null, empty, or the date is null
     * @throws StockDataNotFoundException if no stock data is found for the specified symbol and date
     */
    public StockPrice getStockPrice(String symbol, LocalDate date) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Stock symbol cannot be null or empty");
        }

        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

        logger.info("Retrieving stock price for symbol {} on date {}", symbol, date);

        return stockPriceRepository
                .findByCompanySymbolAndDate(symbol, date)
                .orElseThrow(() -> {
                    logger.warn("Stock data not found for symbol {} on date {}", symbol, date);
                    return new StockDataNotFoundException(symbol, date);
                });
    }

    /**
     * Parses the JSON response from the Polygon API and extracts stock price data for a given symbol.
     *
     * @param symbol the stock symbol for which the data is requested
     * @param polygonJson the raw JSON response from the Polygon API
     * @return a list of {@code StockPrice} objects containing parsed stock price data, or an empty list if no data is found
     * @throws StockDataParsingException if an error occurs while parsing the JSON response
     * @throws PolygonApiException if the Polygon API returns an error or a non-OK status
     */
    private List<StockPrice> parsePolygonResponse(String symbol, String polygonJson) {
        List<StockPrice> result = new ArrayList<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(polygonJson);

            // Check for API error response
            if (rootNode.has("error")) {
                String errorMessage = rootNode.get("error").asText();
                logger.error("Polygon API returned an error: {}", errorMessage);
                throw new PolygonApiException("Polygon API error: " + errorMessage);
            }

            // Check for status
            if (rootNode.has("status") && !"OK".equalsIgnoreCase(rootNode.get("status").asText())) {
                String status = rootNode.get("status").asText();
                logger.error("Polygon API returned non-OK status: {}", status);
                throw new PolygonApiException("Polygon API returned status: " + status);
            }

            JsonNode resultsNode = rootNode.get("results");

            if (resultsNode == null || !resultsNode.isArray() || resultsNode.isEmpty()) {
                logger.warn("No results found in Polygon API response for symbol {}", symbol);
                return result;
            }

            for (JsonNode dayData : resultsNode) {
                try {
                    StockPrice sp = new StockPrice();
                    sp.setCompanySymbol(symbol);

                    // Parse timestamp to date
                    if (!dayData.has("t")) {
                        logger.warn("Missing timestamp field in day data for symbol {}", symbol);
                        continue;
                    }

                    String dateStr = dayData.get("t").asText();
                    if (dateStr.matches("\\d+")) {
                        LocalDate date = Instant.ofEpochMilli(Long.parseLong(dateStr))
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                        sp.setDate(date);
                    } else {
                        sp.setDate(LocalDate.parse(dateStr));
                    }

                    // Check for required fields
                    if (!dayData.has("o") || !dayData.has("h") || !dayData.has("l") ||
                            !dayData.has("c") || !dayData.has("v")) {
                        logger.warn("Missing required price fields in day data for symbol {} on date {}",
                                symbol, sp.getDate());
                        continue;
                    }

                    sp.setOpenPrice(dayData.get("o").asDouble());
                    sp.setHighPrice(dayData.get("h").asDouble());
                    sp.setLowPrice(dayData.get("l").asDouble());
                    sp.setClosePrice(dayData.get("c").asDouble());
                    sp.setVolume(dayData.get("v").asLong());

                    result.add(sp);
                } catch (Exception e) {
                    logger.warn("Error parsing individual day data for symbol {}: {}", symbol, e.getMessage());
                    // Continue with next day data instead of failing the entire batch
                }
            }
        } catch (JsonProcessingException e) {
            logger.error("Error parsing Polygon API JSON response", e);
            throw new StockDataParsingException("Error parsing Polygon API JSON response", e);
        } catch (PolygonApiException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error parsing Polygon API response", e);
            throw new StockDataParsingException("Unexpected error parsing Polygon API response: " + e.getMessage(), e);
        }

        return result;
    }
}