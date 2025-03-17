package com.leadiq.polygonapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data Transfer Object for stock price information returned by the API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Stock price information for a specific company on a particular date")
public class StockPriceResponseDTO {

    @Schema(description = "Stock symbol representing the company", example = "AAPL")
    private String symbol;

    @Schema(description = "Date of the stock price information", example = "2023-01-15")
    private LocalDate date;

    @Schema(description = "Opening price of the stock", example = "150.25")
    private Double openPrice;

    @Schema(description = "Closing price of the stock", example = "152.75")
    private Double closePrice;

    @Schema(description = "Highest price of the stock during the day", example = "153.50")
    private Double highPrice;

    @Schema(description = "Lowest price of the stock during the day", example = "149.80")
    private Double lowPrice;

    @Schema(description = "Trading volume for the day", example = "15000000")
    private Long volume;
}
