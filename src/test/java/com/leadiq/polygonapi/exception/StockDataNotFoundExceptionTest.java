package com.leadiq.polygonapi.exception;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StockDataNotFoundExceptionTest {

    @Test
    public void testConstructorSetsMessage() {
        String symbol = "AAPL";
        LocalDate date = LocalDate.of(2023, 10, 1);
        StockDataNotFoundException exception = new StockDataNotFoundException(symbol, date);
        assertEquals("Stock data not found for symbol 'AAPL' on date '2023-10-01'", exception.getMessage());
    }

    @Test
    public void testGetSymbol() {
        String symbol = "GOOGL";
        LocalDate date = LocalDate.of(2023, 10, 1);
        StockDataNotFoundException exception = new StockDataNotFoundException(symbol, date);
        assertEquals(symbol, exception.getSymbol());
    }

    @Test
    public void testGetDate() {
        String symbol = "MSFT";
        LocalDate date = LocalDate.of(2023, 10, 1);
        StockDataNotFoundException exception = new StockDataNotFoundException(symbol, date);
        assertEquals(date, exception.getDate());
    }

    @Test
    public void testExceptionMessageForDifferentInputs() {
        StockDataNotFoundException exception1 = new StockDataNotFoundException("TSLA", LocalDate.of(2023, 10, 1));
        assertEquals("Stock data not found for symbol 'TSLA' on date '2023-10-01'", exception1.getMessage());

        StockDataNotFoundException exception2 = new StockDataNotFoundException("AMZN", LocalDate.of(2023, 10, 2));
        assertEquals("Stock data not found for symbol 'AMZN' on date '2023-10-02'", exception2.getMessage());
    }

    @Test
    public void testExceptionIsThrown() {
        assertThrows(StockDataNotFoundException.class, () -> {
            throw new StockDataNotFoundException("NFLX", LocalDate.of(2023, 10, 3));
        });
    }
}
