package com.leadiq.polygonapi.controller;

import com.leadiq.polygonapi.entity.StockPrice;
import com.leadiq.polygonapi.service.StockPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * StockPriceController is a REST controller responsible for managing stock price data.
 * It provides endpoints for fetching and saving stock prices as well as retrieving stock prices
 * for a specific company symbol on a given date.
 */
@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockPriceController {

    private final StockPriceService stockPriceService;

    /**
     * Fetches stock price data for a given company symbol within the specified date range,
     * saves the data to the database, and returns the list of saved stock prices.
     *
     * @param companySymbol The stock symbol representing the company (e.g., "AAPL" for Apple Inc.).
     *                       Must be a non-empty string.
     * @param fromDate       The start date for the stock data retrieval in the format "YYYY-MM-DD".
     *                       Must be a valid date string.
     * @param toDate         The end date for the stock data retrieval in the format "YYYY-MM-DD".
     *                       Must be a valid date string.
     * @return A ResponseEntity containing a list of StockPrice objects that were successfully retrieved
     *         and saved. If no data was found or an error occurred, an appropriate HTTP response is returned.
     */
    @PostMapping("/fetch")
    public ResponseEntity<List<StockPrice>> fetchAndSaveStockPrices(
            @RequestParam String companySymbol,
            @RequestParam String fromDate,
            @RequestParam String toDate
    ) {
        List<StockPrice> saved = stockPriceService.fetchAndSavePrices(companySymbol, fromDate, toDate);
        return ResponseEntity.ok(saved);
    }


    /**
     * Retrieves the stock price for a specific company symbol on a given date.
     *
     * @param symbol the stock symbol of the company to look up; must not be null or empty
     * @param date the date for which the stock price is to be retrieved, formatted as ISO_DATE (YYYY-MM-DD); must not be null
     * @return a ResponseEntity containing the StockPrice object if data is found, or a ResponseEntity with a NOT_FOUND status if no data exists
     */
    @GetMapping("/{symbol}")
    public ResponseEntity<StockPrice> getStockPriceBySymbolAndDate(
            @PathVariable("symbol") String symbol,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        StockPrice sp = stockPriceService.getStockPrice(symbol, date);
        if (sp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(sp);
    }
}
