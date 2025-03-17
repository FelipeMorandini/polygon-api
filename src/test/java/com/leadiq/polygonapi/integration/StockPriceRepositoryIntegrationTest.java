package com.leadiq.polygonapi.integration;

import com.leadiq.polygonapi.entity.StockPrice;
import com.leadiq.polygonapi.repository.StockPriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class StockPriceRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private StockPriceRepository stockPriceRepository;

    @BeforeEach
    public void setup() {
        stockPriceRepository.deleteAll();
    }

    @Test
    public void testSaveAndFindById() {
        StockPrice stockPrice = StockPrice.builder()
                .companySymbol("AAPL")
                .date(LocalDate.of(2023, 1, 15))
                .openPrice(173.97)
                .closePrice(173.57)
                .highPrice(174.3)
                .lowPrice(173.12)
                .volume(77287356L)
                .build();

        StockPrice savedPrice = stockPriceRepository.save(stockPrice);

        assertNotNull(savedPrice.getId());

        Optional<StockPrice> foundPrice = stockPriceRepository.findById(savedPrice.getId());
        assertTrue(foundPrice.isPresent());
        assertEquals("AAPL", foundPrice.get().getCompanySymbol());
    }

    @Test
    public void testFindByCompanySymbolAndDate() {
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

        Optional<StockPrice> foundPrice = stockPriceRepository.findByCompanySymbolAndDate(
                "AAPL", LocalDate.of(2023, 1, 15));

        assertTrue(foundPrice.isPresent());
        assertEquals("AAPL", foundPrice.get().getCompanySymbol());
        assertEquals(LocalDate.of(2023, 1, 15), foundPrice.get().getDate());

        Optional<StockPrice> notFoundPrice = stockPriceRepository.findByCompanySymbolAndDate(
                "MISSING", LocalDate.of(2023, 1, 15));

        assertFalse(notFoundPrice.isPresent());
    }

    @Test
    public void testFindByCompanySymbolAndDateBetween() {
        List<StockPrice> stockPrices = List.of(
                StockPrice.builder()
                        .companySymbol("AAPL")
                        .date(LocalDate.of(2023, 1, 1))
                        .openPrice(173.97)
                        .closePrice(173.57)
                        .highPrice(174.3)
                        .lowPrice(173.12)
                        .volume(77287356L)
                        .build(),
                StockPrice.builder()
                        .companySymbol("AAPL")
                        .date(LocalDate.of(2023, 1, 2))
                        .openPrice(174.50)
                        .closePrice(176.20)
                        .highPrice(176.80)
                        .lowPrice(174.10)
                        .volume(80123456L)
                        .build(),
                StockPrice.builder()
                        .companySymbol("AAPL")
                        .date(LocalDate.of(2023, 1, 3))
                        .openPrice(175.20)
                        .closePrice(174.80)
                        .highPrice(175.50)
                        .lowPrice(174.30)
                        .volume(75000000L)
                        .build(),
                StockPrice.builder()
                        .companySymbol("MSFT")
                        .date(LocalDate.of(2023, 1, 2))
                        .openPrice(250.10)
                        .closePrice(252.30)
                        .highPrice(253.20)
                        .lowPrice(249.80)
                        .volume(30000000L)
                        .build()
        );

        stockPriceRepository.saveAll(stockPrices);

        Page<StockPrice> result = stockPriceRepository.findByCompanySymbolAndDateBetween(
                "AAPL",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 3),
                PageRequest.of(0, 10)
        );

        assertEquals(3, result.getTotalElements());
        assertEquals(3, result.getContent().size());

        Page<StockPrice> pagedResult = stockPriceRepository.findByCompanySymbolAndDateBetween(
                "AAPL",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 3),
                PageRequest.of(0, 2)
        );

        assertEquals(3, pagedResult.getTotalElements());
        assertEquals(2, pagedResult.getContent().size());

        Page<StockPrice> msftResult = stockPriceRepository.findByCompanySymbolAndDateBetween(
                "MSFT",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 3),
                PageRequest.of(0, 10)
        );

        assertEquals(1, msftResult.getTotalElements());
    }

    @Test
    public void testUniqueConstraint() {
        StockPrice stockPrice1 = StockPrice.builder()
                .companySymbol("AAPL")
                .date(LocalDate.of(2023, 1, 15))
                .openPrice(173.97)
                .closePrice(173.57)
                .highPrice(174.3)
                .lowPrice(173.12)
                .volume(77287356L)
                .build();

        stockPriceRepository.save(stockPrice1);

        StockPrice stockPrice2 = StockPrice.builder()
                .companySymbol("AAPL")
                .date(LocalDate.of(2023, 1, 15))
                .openPrice(174.00)
                .closePrice(174.00)
                .highPrice(175.00)
                .lowPrice(173.00)
                .volume(80000000L)
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> {
            stockPriceRepository.save(stockPrice2);
            stockPriceRepository.flush();
        });
    }
}
