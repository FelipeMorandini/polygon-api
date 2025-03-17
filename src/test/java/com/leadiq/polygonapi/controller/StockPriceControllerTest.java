package com.leadiq.polygonapi.controller;

import com.leadiq.polygonapi.dto.StockPriceResponseDTO;
import com.leadiq.polygonapi.entity.StockPrice;
import com.leadiq.polygonapi.exception.ErrorResponse;
import com.leadiq.polygonapi.service.StockPriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StockPriceControllerTest {

    @Mock
    private StockPriceService stockPriceService;

    @InjectMocks
    private StockPriceController stockPriceController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void fetchAndSaveStockPrices_ShouldReturnBadRequest_WhenFromDateIsAfterToDate() {
        LocalDate fromDate = LocalDate.of(2025, 3, 14);
        LocalDate toDate = LocalDate.of(2025, 3, 13);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            stockPriceController.fetchAndSaveStockPrices("AAPL", fromDate, toDate, 0, 20);
        });

        assertEquals("From date cannot be after to date", exception.getMessage());
    }

    @Test
    void fetchAndSaveStockPrices_ShouldReturnStockPrices_WhenValidInput() {
        LocalDate fromDate = LocalDate.of(2025, 3, 13);
        LocalDate toDate = LocalDate.of(2025, 3, 14);
        StockPrice stockPrice = StockPrice.builder()
                .companySymbol("AAPL")
                .date(fromDate)
                .openPrice(150.0)
                .closePrice(155.0)
                .highPrice(157.0)
                .lowPrice(148.0)
                .volume(1000L)
                .build();;
        Page<StockPrice> stockPricePage = new PageImpl<>(Collections.singletonList(stockPrice));

        when(stockPriceService.fetchAndSavePrices(any(), any(), any(), any())).thenReturn(stockPricePage);

        ResponseEntity<Page<StockPriceResponseDTO>> response = stockPriceController.fetchAndSaveStockPrices("AAPL", fromDate, toDate, 0, 20);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals("AAPL", response.getBody().getContent().get(0).getSymbol());
    }

    @Test
    void getStockPriceBySymbolAndDate_ShouldReturnNotFound_WhenStockPriceDoesNotExist() {
        when(stockPriceService.getStockPrice(any(), any())).thenReturn(null);

        ResponseEntity<StockPriceResponseDTO> response = stockPriceController.getStockPriceBySymbolAndDate("AAPL", LocalDate.of(2025, 3, 1));

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void getStockPriceBySymbolAndDate_ShouldReturnStockPrice_WhenExists() {
        StockPrice stockPrice = StockPrice.builder()
                .companySymbol("AAPL")
                .date(LocalDate.of(2025, 3, 13))
                .openPrice(150.0)
                .closePrice(155.0)
                .highPrice(157.0)
                .lowPrice(148.0)
                .volume(1000L)
                .build();;
        when(stockPriceService.getStockPrice(any(), any())).thenReturn(stockPrice);

        ResponseEntity<StockPriceResponseDTO> response = stockPriceController.getStockPriceBySymbolAndDate("AAPL", LocalDate.of(2025, 3, 14));

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("AAPL", response.getBody().getSymbol());
    }
}
