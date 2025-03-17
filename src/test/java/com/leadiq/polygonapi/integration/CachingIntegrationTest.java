package com.leadiq.polygonapi.integration;

import com.leadiq.polygonapi.entity.StockPrice;
import com.leadiq.polygonapi.repository.StockPriceRepository;
import com.leadiq.polygonapi.service.StockPriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

public class CachingIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private StockPriceService stockPriceService;

    @Autowired
    private StockPriceRepository stockPriceRepository;

    @SpyBean
    private StockPriceRepository stockPriceRepositorySpy;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    public void setup() {
        stockPriceRepository.deleteAll();

        cacheManager.getCacheNames()
                .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }

    @Test
    public void testStockPriceCaching() {
        StockPrice stockPrice = StockPrice.builder()
                .companySymbol("AAPL")
                .date(LocalDate.of(2023, 1, 15))
                .openPrice(173.97)
                .closePrice(173.57)
                .highPrice(174.3)
                .lowPrice(173.12)
                .volume(77287356L)
                .build();

        stockPriceRepository.save(stockPrice);

        stockPriceService.getStockPrice("AAPL", LocalDate.of(2023, 1, 15));

        verify(stockPriceRepositorySpy, times(1))
                .findByCompanySymbolAndDate("AAPL", LocalDate.of(2023, 1, 15));

        reset(stockPriceRepositorySpy);

        stockPriceService.getStockPrice("AAPL", LocalDate.of(2023, 1, 15));

        verify(stockPriceRepositorySpy, never())
                .findByCompanySymbolAndDate("AAPL", LocalDate.of(2023, 1, 15));
    }
}

