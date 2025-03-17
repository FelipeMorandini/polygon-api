package com.leadiq.polygonapi.repository;

import com.leadiq.polygonapi.entity.StockPrice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class StockPriceRepositoryTest {

    @Autowired
    private StockPriceRepository stockPriceRepository;

    @Test
    void testFindByCompanySymbolAndDate() {
        String companySymbol = "AAPL";
        LocalDate date = LocalDate.of(2025, 3, 13);
        StockPrice stockPrice = new StockPrice();
        stockPrice.setCompanySymbol(companySymbol);
        stockPrice.setDate(date);
        stockPrice.setOpenPrice(100.0);
        stockPrice.setClosePrice(105.0);
        stockPrice.setVolume(10000L);
        stockPrice.setHighPrice(108.0);
        stockPrice.setLowPrice(95.0);

        stockPriceRepository.save(stockPrice);

        Optional<StockPrice> result = stockPriceRepository.findByCompanySymbolAndDate(companySymbol, date);
        assertTrue(result.isPresent());
        assertEquals(stockPrice, result.get());
    }

    @Test
    void testFindByCompanySymbolAndDateBetween() {
        String companySymbol = "AAPL";
        LocalDate fromDate = LocalDate.of(2023, 10, 1);
        LocalDate toDate = LocalDate.of(2023, 10, 31);
        Pageable pageable = PageRequest.of(0, 10); // First page, 10 items per page

        // Create and save some test data
        StockPrice stockPrice1 = new StockPrice();
        stockPrice1.setCompanySymbol(companySymbol);
        stockPrice1.setDate(LocalDate.of(2023, 10, 15));
        stockPriceRepository.save(stockPrice1);

        StockPrice stockPrice2 = new StockPrice();
        stockPrice2.setCompanySymbol(companySymbol);
        stockPrice2.setDate(LocalDate.of(2023, 10, 20));
        stockPriceRepository.save(stockPrice2);

        // Use Page instead of List for the return type
        Page<StockPrice> result = stockPriceRepository.findByCompanySymbolAndDateBetween(
            companySymbol, fromDate, toDate, pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void testFindByCompanySymbolAndDateBetween_NoResults() {
        String companySymbol = "AAPL";
        LocalDate fromDate = LocalDate.of(2023, 10, 1);
        LocalDate toDate = LocalDate.of(2023, 10, 31);
        Pageable pageable = PageRequest.of(0, 10); // First page, 10 items per page

        Page<StockPrice> result = stockPriceRepository.findByCompanySymbolAndDateBetween(
            companySymbol, fromDate, toDate, pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
