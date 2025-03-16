package com.leadiq.polygonapi.repository;

import com.leadiq.polygonapi.entity.StockPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Repository interface for accessing and managing {@link StockPrice} entities.
 * Extends the {@link JpaRepository} to provide CRUD operations and query execution capabilities.
 */
public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
    Optional<StockPrice> findByCompanySymbolAndDate(String companySymbol, LocalDate date);

    /**
     * Finds stock prices for a specific company symbol within a date range with pagination support.
     *
     * @param companySymbol the stock symbol to search for
     * @param fromDate the start date of the range (inclusive)
     * @param toDate the end date of the range (inclusive)
     * @param pageable pagination information
     * @return a Page of StockPrice entities matching the criteria
     */
    Page<StockPrice> findByCompanySymbolAndDateBetween(
        String companySymbol,
        LocalDate fromDate,
        LocalDate toDate,
        Pageable pageable
    );
}