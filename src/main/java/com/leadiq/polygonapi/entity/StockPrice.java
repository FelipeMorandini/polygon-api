package com.leadiq.polygonapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Represents the stock price information for a specific company on a particular date.
 * This entity is mapped to the "stock_price" table in the database and includes fields
 * for storing key financial data such as opening price, closing price, highest price,
 * lowest price, and trading volume for a company's stock.
 * Each instance of this class corresponds to a single record in the "stock_price" table.
 * The class is annotated with JPA and Lombok annotations to support persistence and
 * reduce boilerplate code.
 * Fields:
 * - id: The unique identifier for the stock price record.
 * - companySymbol: The stock symbol representing the company (e.g., "AAPL" for Apple Inc.).
 * - date: The date for which the stock price information is recorded.
 * - openPrice: The stock's opening price on the specified date.
 * - closePrice: The stock's closing price on the specified date.
 * - highPrice: The highest trading price of the stock on the specified date.
 * - lowPrice: The lowest trading price of the stock on the specified date.
 * - volume: The total number of shares traded for the stock on the specified date.
 */
@Data
@Entity
@Table(name = "stock_price", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"company_symbol", "date"})
        },
        indexes = {
                @Index(name = "idx_company_symbol", columnList = "company_symbol"),
                @Index(name = "idx_date", columnList = "date")
        }
)
public class StockPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_symbol", nullable = false)
    private String companySymbol;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "open_price")
    private Double openPrice;

    @Column(name = "close_price")
    private Double closePrice;

    @Column(name = "high_price")
    private Double highPrice;

    @Column(name = "low_price")
    private Double lowPrice;

    @Column(name = "volume")
    private Long volume;
}
