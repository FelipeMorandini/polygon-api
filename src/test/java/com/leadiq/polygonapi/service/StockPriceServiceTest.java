package com.leadiq.polygonapi.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.leadiq.polygonapi.entity.StockPrice;
import com.leadiq.polygonapi.repository.StockPriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class StockPriceServiceTest {

    @Mock
    private PolygonClient polygonClient;

    @Mock
    private StockPriceRepository stockPriceRepository;

    @InjectMocks
    private StockPriceService stockPriceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFetchAndSavePrices_EmptySymbol() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                stockPriceService.fetchAndSavePrices("", "2023-03-13", "2023-03-14", Pageable.unpaged())
        );
        assertEquals("Stock symbol cannot be null or empty", exception.getMessage());
    }

    @Test
    void testFetchAndSavePrices_NullFromDate() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                stockPriceService.fetchAndSavePrices("AAPL", null, "2023-03-13", Pageable.unpaged())
        );
        assertEquals("From date cannot be null or empty", exception.getMessage());
    }

    @Test
    void testFetchAndSavePrices_NullToDate() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                stockPriceService.fetchAndSavePrices("AAPL", "2023-03-13", null, Pageable.unpaged())
        );
        assertEquals("To date cannot be null or empty", exception.getMessage());
    }

    @Test
    void testFetchAndSavePrices_NoDataFound() {
        when(polygonClient.fetchStockData("AAPL", "2023-03-13", "2023-03-14")).thenReturn("[]");
        when(stockPriceRepository.findByCompanySymbolAndDateBetween("AAPL", LocalDate.parse("2023-03-13"), LocalDate.parse("2023-03-14"), Pageable.unpaged()))
                .thenReturn(Page.empty(Pageable.unpaged()));

        Page<StockPrice> result = stockPriceService.fetchAndSavePrices("AAPL", "2023-03-13", "2023-03-14", Pageable.unpaged());
        assertTrue(result.isEmpty());
    }
        @Test
        void testFetchAndSavePrices_Success() {
            String polygonResponse = "{"
                + "\"status\":\"OK\","
                + "\"results\":["
                + "  {"
                + "    \"t\":1673740800000,"
                + "    \"o\":150.0,"
                + "    \"h\":156.0,"
                + "    \"l\":149.0,"
                + "    \"c\":155.0,"
                + "    \"v\":1000000"
                + "  }"
                + "]"
                + "}";

            when(polygonClient.fetchStockData("AAPL", "2023-03-13", "2023-03-14")).thenReturn(polygonResponse);

            List<StockPrice> stockPrices = new ArrayList<>();
            StockPrice stockPrice = new StockPrice();
            stockPrice.setCompanySymbol("AAPL");
            stockPrice.setDate(LocalDate.parse("2023-03-13"));
            stockPrice.setOpenPrice(150.0);
            stockPrice.setClosePrice(155.0);
            stockPrice.setHighPrice(156.0);
            stockPrice.setLowPrice(149.0);
            stockPrice.setVolume(1000000L);
            stockPrices.add(stockPrice);

            when(stockPriceRepository.saveAll(any())).thenReturn(stockPrices);
            when(stockPriceRepository.findByCompanySymbolAndDateBetween(
                    eq("AAPL"),
                    eq(LocalDate.parse("2023-03-13")),
                    eq(LocalDate.parse("2023-03-14")),
                    any(Pageable.class)))
                    .thenReturn(new PageImpl<>(stockPrices, Pageable.unpaged(), stockPrices.size()));

            Page<StockPrice> result = stockPriceService.fetchAndSavePrices("AAPL", "2023-03-13", "2023-03-14", Pageable.unpaged());

            assertEquals(1, result.getTotalElements());
            assertEquals("AAPL", result.getContent().get(0).getCompanySymbol());
        }}