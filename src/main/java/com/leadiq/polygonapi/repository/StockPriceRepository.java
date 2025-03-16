package com.leadiq.polygonapi.repository;

import com.leadiq.polygonapi.entity.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Repository interface for accessing and managing {@link StockPrice} entities.
 * Extends the {@link JpaRepository} to provide CRUD operations and query execution capabilities.
 */
public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
    Optional<StockPrice> findByCompanySymbolAndDate(String companySymbol, LocalDate date);
}
