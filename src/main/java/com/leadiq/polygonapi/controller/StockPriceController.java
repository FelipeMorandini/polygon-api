package com.leadiq.polygonapi.controller;

import com.leadiq.polygonapi.entity.StockPrice;
import com.leadiq.polygonapi.service.StockPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * StockPriceController is a REST controller responsible for managing stock price data.
 * It provides endpoints for fetching and saving stock prices as well as retrieving stock prices
 * for a specific company symbol on a given date.
 */
@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
public class StockPriceController {

    private final StockPriceService stockPriceService;

    /**
     * Fetches and saves stock prices for a specific company over a given date range.
     * The method retrieves stock price data for the specified company from an external
     * service or data source, saves the data to the database, and supports pagination of
     * the results.
     *
     * @param companySymbol the stock symbol of the company whose prices are to be fetched; must not be null or empty
     * @param fromDate the start date of the date range for which stock prices are to be fetched, formatted as ISO_DATE (YYYY-MM-DD); must not be null
     * @param toDate the end date of the date range for which stock prices are to be fetched, formatted as ISO_DATE (YYYY-MM-DD); must not be null
     * @param page the page number for pagination; defaults to 0 if not specified
     * @param size the number of records per page for pagination; defaults to 20 if not specified
     * @return a ResponseEntity containing a Page of StockPrice objects with the fetched and saved stock price data
     * @throws IllegalArgumentException if the fromDate is after the toDate
     */
    @GetMapping("/fetch")
    public ResponseEntity<Page<StockPrice>> fetchAndSaveStockPrices(
            @RequestParam String companySymbol,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("From date cannot be after to date");
        }
        Page<StockPrice> saved = stockPriceService.fetchAndSavePrices(
                companySymbol, fromDate.toString(), toDate.toString(), PageRequest.of(page, size));
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
        return ResponseEntity.ok(sp);
    }}
