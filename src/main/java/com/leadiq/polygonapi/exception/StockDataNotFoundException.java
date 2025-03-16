package com.leadiq.polygonapi.exception;

import java.time.LocalDate;

/**
 * Represents an exception that is thrown when stock data for a particular symbol and date cannot be found.
 * This exception is typically used to indicate a missing or unavailable stock data record during a database query
 * or an API call.
 *
 * It extends the RuntimeException class, making it an unchecked exception.
 */
public class StockDataNotFoundException extends RuntimeException {

    private final String symbol;
    private final LocalDate date;

    /**
     * Constructs a new StockDataNotFoundException with the specified stock symbol and date.
     * This exception is thrown to indicate that the requested stock data could not be found
     * for the given symbol and date.
     *
     * @param symbol the stock symbol for which data could not be found
     * @param date the date for which the stock data is unavailable
     */
    public StockDataNotFoundException(String symbol, LocalDate date) {
        super(String.format("Stock data not found for symbol '%s' on date '%s'", symbol, date));
        this.symbol = symbol;
        this.date = date;
    }

    /**
     * Returns the stock symbol associated with this exception.
     *
     * @return the stock symbol for which data could not be found
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Returns the date for which the stock data could not be found.
     *
     * @return the date associated with this exception
     */
    public LocalDate getDate() {
        return date;
    }
}